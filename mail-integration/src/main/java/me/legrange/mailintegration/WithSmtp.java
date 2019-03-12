package me.legrange.mailintegration;

import me.legrange.service.ServiceException;
import me.legrange.service.WithComponent;

/**
 *
 * @author gideon
 */
public interface WithSmtp extends WithComponent {
    
    default SmtpComponent smtp() throws ServiceException { 
        return getComponent(SmtpComponent.class);
    }
       
}
