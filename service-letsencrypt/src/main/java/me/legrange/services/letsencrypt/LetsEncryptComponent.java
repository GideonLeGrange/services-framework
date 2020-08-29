package me.legrange.services.letsencrypt;

import me.legrange.service.Component;
import me.legrange.service.ComponentException;
import me.legrange.service.Service;
import me.legrange.services.jetty.JettyComponent;
import me.legrange.services.jetty.WithJetty;
import me.legrange.services.keystore.StoreException;
import me.legrange.services.keystore.WithKeyStore;
import me.legrange.services.logging.WithLogging;
import org.shredzone.acme4j.Account;
import org.shredzone.acme4j.AccountBuilder;
import org.shredzone.acme4j.Authorization;
import org.shredzone.acme4j.Certificate;
import org.shredzone.acme4j.Order;
import org.shredzone.acme4j.Session;
import org.shredzone.acme4j.Status;
import org.shredzone.acme4j.challenge.Http01Challenge;
import org.shredzone.acme4j.exception.AcmeException;
import org.shredzone.acme4j.util.CSRBuilder;
import org.shredzone.acme4j.util.KeyPairUtils;

import java.io.IOException;
import java.security.KeyPair;
import java.security.KeyStoreException;
import java.security.cert.X509Certificate;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import static java.lang.String.format;

public final class LetsEncryptComponent extends Component<Service, LetsEncryptConfig> implements WithJetty, WithKeyStore, WithLogging {

    private static final int MIN_EXPIRY_DAYS = 180;
    private static LetsEncryptComponent instance;
    private LetsEncryptConfig config;
    private final Map<String, String> challengeResponses = new ConcurrentHashMap();

    public LetsEncryptComponent(Service service) {
        super(service);
    }

    @Override
    public void start(LetsEncryptConfig config) throws ComponentException {
        this.config = config;
        this.instance = this;
        jetty().addEndpoint(JettyComponent.Connector.HTTP, "/.well-known/acme-challenge", ChallengeEndpoint.class);
        try {
            if (hasCertificate()) {
                debug("Has certificate");
                service().submit(this::scheduleRenewalCheck);
            } else {
                service().submit(this::obtainCertificate);
            }
        } catch (LetsEcryptException ex) {
            throw new ComponentException(ex.getMessage(), ex);
        }
    }

    @Override
    public String getName() {
        return "letsEncrypt";
    }

    Optional<String> getChallengeResponse(String token) {
        return Optional.ofNullable(challengeResponses.get(token));
    }

    static LetsEncryptComponent getInstance() {
        return instance;
    }

    /**
     * Obtain a new certificate
     */
    private void obtainCertificate() {
        debug("obtainCertificate()");
        try {
            try {
                TimeUnit.SECONDS.sleep(5);
            } catch (InterruptedException e) {
            }
            KeyPair keyPair = obtainKeys();
            Account account = getAccount(keyPair);
            Order order = createOrder(account);
            createCsr(order);
            Certificate cert = downloadCertificate(order);
            List<X509Certificate> chain = cert.getCertificateChain();
            for (X509Certificate c :chain) {
                debug("Storing %s from chain", c.getSubjectDN().getName());
                String alias = c.getSubjectDN().getName();
                if (alias.endsWith(config.getDomain())) {
                    alias = "jetty";
                }
                storeCertificate(alias, c);
            }
            storeKey("jetty", keyPair.getPrivate(), chain);

        } catch (LetsEcryptException ex) {
            error(ex);
        } catch (StoreException e) {
            error(e);
        }
    }

    /**
     * Dowload the signed certificate
     *
     * @param order The order to use
     */
    private Certificate downloadCertificate(Order order) throws LetsEcryptException {
        debug("downloadCertificate()");
        try {
            while (order.getStatus() != Status.VALID) {
                try {
                    TimeUnit.SECONDS.sleep(3);
                } catch (InterruptedException e) {
                }
                order.update();
            }
            Certificate cert = order.getCertificate();
            return cert;
        } catch (AcmeException ex) {
            throw new LetsEcryptException(format("Error downloading certificate for '%s' (%s)", config.getDomain(), ex.getMessage()));
        }
    }

    /**
     * Create a new certificate
     *
     * @param order The order to use
     * @throws LetsEcryptException
     */
    private void createCsr(Order order) throws LetsEcryptException {
        debug("createCsr()");

        KeyPair domainKeyPair = KeyPairUtils.createKeyPair(2048);
        CSRBuilder csrb = new CSRBuilder();
        csrb.addDomain(config.getDomain());
        IssuerConfig issuer = config.getIssuer();
        csrb.setOrganization(issuer.getOrganization());
        csrb.setCountry(issuer.getCountry());
        csrb.setLocality(issuer.getLocality());
        csrb.setState(issuer.getState());
        csrb.setOrganizationalUnit(issuer.getOrganizationalUnit());
        byte[] csr;
        try {
            csrb.sign(domainKeyPair);
            csr = csrb.getEncoded();
            order.execute(csr);
        } catch (IOException | AcmeException ex) {
            throw new LetsEcryptException(format("Error creating certificate for '%s' (%s)", config.getDomain(), ex.getMessage()), ex);
        }
    }

    /**
     * Create a Let's Encrypt certificate order
     *
     * @param account
     * @return
     */
    private Order createOrder(Account account) throws LetsEcryptException {
        debug("createOrder()");
        try {
            Order order = account.newOrder()
                    .domains(config.getDomain())
                    .create();
            for (Authorization auth : order.getAuthorizations()) {
                if (auth.getStatus() != Status.VALID) {
                    Http01Challenge challenge = auth.findChallenge(Http01Challenge.TYPE);
                    challengeResponses.put(challenge.getToken(), challenge.getAuthorization());
                    challenge.trigger();
                }
                while (auth.getStatus() != Status.VALID) {
                    debug(("Waiting for Let's Encrypt response"));
                    try {
                        TimeUnit.SECONDS.sleep(3);
                    } catch (InterruptedException e) {
                    }
                    auth.update();
                }
            }
            return order;
        } catch (AcmeException ex) {
            throw new LetsEcryptException(format("Error creating certificate order for '%s' (%s)", config.getDomain(), ex.getMessage()), ex);
        }
    }

    /**
     * Create or load Let's Encrypt account
     *
     * @param keyPair Key pair to use to create account
     * @return The account
     * @throws LetsEcryptException
     */
    private Account getAccount(KeyPair keyPair) throws LetsEcryptException {
        debug("createAccount()");
        try {
            Session session = new Session(config.getLetsEncryptUrl());
            return new AccountBuilder()
                    .useKeyPair(keyPair)
                    .agreeToTermsOfService()
                    .create(session);
        } catch (AcmeException e) {
            throw new LetsEcryptException(format("Error creating account on %s (%s)", config.getLetsEncryptUrl(), e.getMessage()), e);
        }
    }

    /**
     * Obtain Let's Encrypt keys
     */
    private KeyPair obtainKeys() throws LetsEcryptException {
        debug("obtainKeys()");
        return KeyPairUtils.createKeyPair(2048);
    }

    /**
     * Schedule a certificate check for the future
     */
    private void scheduleRenewalCheck() {

        Timer timer = new Timer(true);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                checkRenewal();
            }
        }, 0L, Duration.ofDays(1).toMillis());
        debug("Scheduled dialy renewal check");
    }

    private void checkRenewal() {
        try {
            X509Certificate cert = getCertificate(config.getDomain());
            Date notAfter = cert.getNotAfter();
            if (notAfter.toInstant().isBefore(Instant.now().plus(MIN_EXPIRY_DAYS, ChronoUnit.DAYS))) {
                warning("Certificate for %s expires on %s", config.getDomain(), notAfter);
                renewCertificate();
            }
        } catch (LetsEcryptException e) {
            error(e);
        }
    }

    private void renewCertificate() {
        debug("Renew certificate not implemeted yet");
    }

    /**
     * Determine if a certificate exists for the domain being served.
     */
    private boolean hasCertificate() throws LetsEcryptException {
        debug("hasCertificate()");
        try {
//            String alias = makeAlias(config.getDomain());
            String alias = "jetty";
            if (keyStore().getKeyStore().containsAlias(alias)) {
                X509Certificate cert = getCertificate(config.getDomain());
                return cert.getNotAfter().after(new Date());
            }
            return false;
        } catch (KeyStoreException ex) {
            throw new LetsEcryptException(format("Error finding certificate (%s)", ex.getMessage()), ex);
        }
    }

    private X509Certificate getCertificate(String domain) throws LetsEcryptException {
        try {
            return (X509Certificate) keyStore().getKeyStore().getCertificate("jetty");
        } catch (KeyStoreException e) {
            throw new LetsEcryptException(format("Error reading certificate for '%s' from key store (%s)",
                    domain, e.getMessage()), e);
        }
    }

//    private String makeAlias(String domain) {
//        return format("CN=%s", domain);
//    }

}
