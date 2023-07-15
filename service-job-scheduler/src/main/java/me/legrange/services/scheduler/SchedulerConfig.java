package me.legrange.services.scheduler;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;

/**
 *
 * @author matthewl
 */
public final class SchedulerConfig {
    
    @NotEmpty(message="At least one job must be defined in the job scheduler")
    public List<JobConfig> jobs;

    public List<JobConfig> getJobs() {
        return jobs;
    }

    public void setJobs(List<JobConfig> jobs) {
        this.jobs = jobs;
    }
}
