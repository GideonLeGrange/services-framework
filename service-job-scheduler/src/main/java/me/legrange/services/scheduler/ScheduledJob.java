package me.legrange.services.scheduler;

/**
 *
 * @author matthewl
 */
public abstract class ScheduledJob implements Runnable{

    private final String jobName;

    public ScheduledJob(String jobName) {
        this.jobName = jobName;
    }

    public String getJobName() {
        return jobName;
    }
    
    @Override
    public final void run(){
        execute();
    }
    
    public abstract void execute();
}
