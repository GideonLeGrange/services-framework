package me.legrange.services.panacea;

import me.legrange.service.WithComponent;

/**
 *
 * @author matthewl
 */
public interface WithPanaceaSMS extends WithComponent {
    
    default PanaceaSMSComponent panacea() { 
        return getComponent(PanaceaSMSComponent.class);
    }
       
}
