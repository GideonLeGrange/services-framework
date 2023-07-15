package me.legrange.services.scheduler;

import jakarta.validation.constraints.NotBlank;

/**
 *
 * @author matthewl
 */
public class JobConfig {

    @NotBlank(message="A scheduled job must have a name")
    private String jobName;

    @NotBlank(message="A scheduled job must have a CRON expression")
    private String cronExpression;

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public String getCronExpression() {
        return cronExpression;
    }

    public void setCronExpression(String cronExpression) {
        this.cronExpression = cronExpression;
    }

}
