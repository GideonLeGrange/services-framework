package me.legrange.services.jooq;

import me.legrange.service.Component;
import me.legrange.service.ComponentException;
import me.legrange.service.Service;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;

/**
 *
 * @author matt
 */
public class JooqComponent extends Component<Service, JooqConfig> {

    private JooqConfig config;

    public JooqComponent(Service service) {
        super(service);
    }

    @Override
    public void start(JooqConfig config) throws ComponentException {
        this.config = config;
        try {
            //test the connection
            DSLContext dsl = getDSL();
            if (dsl == null) {
                throw new ComponentException("Could not get DSLContext for jooq");
            }
        } catch (Throwable ex) {
            throw new ComponentException(ex.getMessage(), ex);
        }
    }

    @Override
    public String getName() {
        return "jooq";
    }

    public DSLContext getDSL() {
        return DSL.using(config.getMysql().getUrl(), config.getMysql().getUsername(), config.getMysql().getPassword());
    }
}
