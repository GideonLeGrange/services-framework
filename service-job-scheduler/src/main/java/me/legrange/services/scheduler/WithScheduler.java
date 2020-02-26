package me.legrange.services.scheduler;

import me.legrange.service.WithComponent;

/**
 *
 * @author matthewl
 */
public interface WithScheduler extends WithComponent {
    
    default SchedulerComponent jobScheduler() { 
        return getComponent(SchedulerComponent.class);
    }
       
}
