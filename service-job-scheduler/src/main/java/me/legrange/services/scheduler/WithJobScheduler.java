package me.legrange.services.scheduler;

import za.co.adept.services.WithComponent;

/**
 *
 * @author matthewl
 */
public interface WithJobScheduler extends WithComponent {
    
    default SchedulerComponent jobScheduler() { 
        return getComponent(SchedulerComponent.class);
    }
       
}
