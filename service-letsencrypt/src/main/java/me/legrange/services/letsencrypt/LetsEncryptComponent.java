package me.legrange.services.letsencrypt;

import me.legrange.service.Component;
import me.legrange.service.ComponentException;
import me.legrange.service.Service;
import me.legrange.services.jetty.WithJetty;
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyPair;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import static java.lang.String.format;

public final class LetsEncryptComponent extends Component<Service<?>, LetsEncryptConfig> implements WithJetty, WithLogging {

    private static LetsEncryptComponent instance;
    private LetsEncryptConfig config;
    private final Map<String, String> challengeResponses = new ConcurrentHashMap<>();

    public LetsEncryptComponent(Service<?> service) {
        super(service);
    }

    @Override
    public void start(LetsEncryptConfig config) throws ComponentException {
        this.config = config;
        instance = this;
        var dir = Path.of(config.getDataDirectory());
        if (!Files.exists(dir)) {
            try {
                Files.createDirectories(dir);
            } catch (IOException e) {
                throw new ComponentException(format("Certificate directory %s does not exist and cannot be created (%s)",
                        dir, e.getMessage()));
            }
        }
        jetty().addEndpoint("/.well-known/acme-challenge", ChallengeEndpoint.class);
        if (hasCertificate()) {
            try {
                activateCertificate();
            } catch (LetsEcryptException e) {
                throw new ComponentException(e.getMessage(), e);
            }
            service().submit(this::scheduleRenewalCheck);
        } else {
            service().submit(this::obtainCertificate);
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
     * Activate the certificate
     */
    private void activateCertificate() throws LetsEcryptException {
        try {
            var cf = CertificateFactory.getInstance("X.509");
            var certs = cf.generateCertificates(new FileInputStream(getCertificateFileName()));
            for (var cert : certs) {
         //       jetty().addCertificate(UUID.randomUUID().toString(), cert);
            }
        } catch (FileNotFoundException | CertificateException e) {
            throw new LetsEcryptException(format("Error activating certificate (%s)", e.getMessage()), e);
        }
    }

    /**
     * Obtain a new certificate
     */
    private void obtainCertificate() {
        debug("obtainCertificate()");
        try {
            try {
                TimeUnit.SECONDS.sleep(10);
            } catch (InterruptedException ignored) {
            }
            KeyPair keyPair;
            if (!hasKeys()) {
                keyPair = obtainKeys();
            } else {
                keyPair = loadKeys();
            }
            Account account = createAccount(keyPair, hasAcmeUrl());
            Order order = createOrder(account);
            createCsr(order);
            downloadCertificate(order);
        } catch (LetsEcryptException ex) {
            error(ex);
        }
    }

    /**
     * Download the signed certificate
     *
     * @param order The order to use
     */
    private void downloadCertificate(Order order) throws LetsEcryptException {
        debug("downloadCertificate()");
        try {
            while (order.getStatus() != Status.VALID) {
                try {
                    TimeUnit.SECONDS.sleep(3);
                } catch (InterruptedException ignored) {
                }
                order.update();
            }
            Certificate cert = order.getCertificate();
            try (FileWriter fw = new FileWriter(getCertificateFileName())) {
                cert.writeCertificate(fw);
            }
        } catch (AcmeException | IOException ex) {
            throw new LetsEcryptException(format("Error downloading certificate for '%s' (%s)", config.getDomain(), ex.getMessage()));
        }
    }

    /**
     * Create a new certificate
     *
     * @param order The order to use
     */
    private void createCsr(Order order) throws LetsEcryptException {
        debug("createCsr()");
        var domainKeyPair = KeyPairUtils.createKeyPair(2048);
        var csrb = new CSRBuilder();
        csrb.addDomain(config.getDomain());
        csrb.setOrganization(config.getOrganization());
        byte[] csr;
        try {
            csrb.sign(domainKeyPair);
            csr = csrb.getEncoded();
            csrb.write(new FileWriter(getCertificateFileName()));
            order.execute(csr);
        } catch (IOException | AcmeException ex) {
            throw new LetsEcryptException(format("Error creating certificate for '%s' (%s)", config.getDomain(), ex.getMessage()), ex);
        }
    }

    /**
     * Create a Let's Encrypt certificate order
     *
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
                    try {
                        TimeUnit.SECONDS.sleep(3);
                    } catch (InterruptedException ignored) {
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
     */
    private Account createAccount(KeyPair keyPair, boolean onlyExisting) throws LetsEcryptException {
        debug("createAccount()");
        Session session = new Session(config.getLetsEncryptUrl());
        try {
            AccountBuilder builder = new AccountBuilder();
            builder = (onlyExisting) ? builder.onlyExisting() : builder;
            Account account = builder
                    .useKeyPair(keyPair)
                    .agreeToTermsOfService()
                    .create(session);
            if (onlyExisting) {
                try (FileWriter out = new FileWriter(getUrlFileName())) {
                    out.write(account.getLocation().toString());
                } catch (IOException e) {
                    throw new LetsEcryptException(format("Error storing account in %s (%s)", getUrlFileName(), e.getMessage()), e);
                }
            }
            return account;
        } catch (AcmeException e) {
            throw new LetsEcryptException(format("Error creating account on %s (%s)", config.getLetsEncryptUrl(), e.getMessage()), e);
        }
    }

    /**
     * Obtain Let's Encrypt keys
     */
    private KeyPair obtainKeys() throws LetsEcryptException {
        debug("obtainKeys()");
        KeyPair accountKeyPair = KeyPairUtils.createKeyPair(2048);
        try (FileWriter fw = new FileWriter(getKeyFileName())) {
            KeyPairUtils.writeKeyPair(accountKeyPair, fw);
        } catch (IOException e) {
            throw new LetsEcryptException(format("Error writing key pair to file '%s' (%s)", getKeyFileName(), e.getMessage()), e);
        }
        return accountKeyPair;
    }

    private KeyPair loadKeys() throws LetsEcryptException {
        debug("loadKeys()");
        try {
            return KeyPairUtils.readKeyPair(new FileReader(getKeyFileName()));
        } catch (IOException e) {
            throw new LetsEcryptException(format("Error loading key pair from file '%s' (%s)", getKeyFileName(), e.getMessage()), e);
        }
    }

    /**
     * Schedule a certificate check for the future
     */
    private void scheduleRenewalCheck() {

    }

    /**
     * Determine if Let's Encrypt keys exist
     */
    private boolean hasKeys() {
        return hasFile(getKeyFileName());
    }

    /**
     * Determine if a url exists for the Let's Encrypt account being served.
     */
    private boolean hasAcmeUrl() {
        return hasFile(getUrlFileName());
    }

    /**
     * Determine if a certificate exists for the domain being served.
     */
    private boolean hasCertificate() {
        return hasFile(getCertificateFileName());
    }

    /**
     * Make the url file name
     */
    private String getUrlFileName() {
        return format("%s/acme.url", config.getDataDirectory());
    }

    /**
     * Make the CSR file name
     */
    private String getCsrFileName() {
        return format("%s/%s.csr", config.getDataDirectory(), config.getDomain());
    }

    /**
     * Make the certificate file name
     */
    private String getCertificateFileName() {
        return format("%s/%s.crt", config.getDataDirectory(), config.getDomain());
    }

    /**
     * Make the key file name
     */
    private String getKeyFileName() {
        return format("%s/letsencrypt.pem", config.getDataDirectory());
    }

    private boolean hasFile(String fileName) {
        return new File(fileName).exists();
    }

}
