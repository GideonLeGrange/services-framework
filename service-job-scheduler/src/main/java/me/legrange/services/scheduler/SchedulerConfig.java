package me.legrange.services.scheduler;

import java.util.List;
import me.legrange.config.Configuration;
import javax.validation.constraints.NotEmpty;

/**
 *
 * @author matthewl
 */
public class SchedulerConfig extends Configuration {
    
    @NotEmpty(message="At least one job must be defined in the job scheduler")
    public List<JobConfig> jobs;

    public List<JobConfig> getJobs() {
        return jobs;
    }

    public void setJobs(List<JobConfig> jobs) {
        this.jobs = jobs;
    }
}
