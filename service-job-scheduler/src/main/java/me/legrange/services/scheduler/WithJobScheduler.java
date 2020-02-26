package me.legrange.services.scheduler;

import me.legrange.service.WithComponent;

/**
 *
 * @author matthewl
 */
public interface WithJobScheduler extends WithComponent {
    
    default SchedulerComponent jobScheduler() { 
        return getComponent(SchedulerComponent.class);
    }
       
}
