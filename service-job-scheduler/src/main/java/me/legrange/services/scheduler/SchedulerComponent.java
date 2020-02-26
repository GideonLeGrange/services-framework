package me.legrange.services.scheduler;

import it.sauronsoftware.cron4j.Scheduler;
import java.util.Optional;
import za.co.adept.log.Log;
import za.co.adept.services.Component;
import za.co.adept.services.ComponentException;
import za.co.adept.services.Service;

/**
 *
 * @author matthewl
 */
public class SchedulerComponent extends Component<SchedulerConfig, Service> {

    private SchedulerConfig config;

    public SchedulerComponent(Service service) {
        super(service);
    }

    public SchedulerComponent() {
        super();
    }

    public void scheduleJob(final ScheduledJob scheduledJob) throws JobNotFoundException {

        JobConfig configForJob = getConfigForJob(scheduledJob);

        if (!configForJob.getCronExpression().toUpperCase().equals("DONT RUN")) {
            Log.info("Scheduled job '%s' with cron '%s'", configForJob.getJobName(), configForJob.getCronExpression());
            final Scheduler scheduler = new Scheduler();
            scheduler.schedule(configForJob.getCronExpression(), () -> {
                try {
                    scheduledJob.run();
                } catch (Throwable ex) {
                    String name = scheduledJob.getClass().getSimpleName();
                    name = (name == null) ? scheduledJob.getClass().getName() : name;
                    Log.critical("Uncaught exception in task '%s': %s", name, ex);
                }
            });
            scheduler.start();
        } else {
            Log.info("Scheduled job '%s' set to '%s'. Not starting", configForJob.getJobName(), configForJob.getCronExpression());
        }
    }

    private JobConfig getConfigForJob(final ScheduledJob job) throws JobNotFoundException {

        Optional<JobConfig> jobConfig = config.getJobs().stream().filter(j -> j.getJobName().equals(job.getJobName())).findAny();
        if (jobConfig.isPresent()) {
            return jobConfig.get();
        } else {
            throw new JobNotFoundException(String.format("Could not find job config for %s", job.getJobName()));
        }
    }

    @Override
    public void start(SchedulerConfig config) throws ComponentException {
        this.config = config;
    }

    @Override
    public String getName() {
        return "jobScheduler";
    }

    @Override
    public Class<WithJobScheduler> getWithClass() {
        return WithJobScheduler.class;
    }
}
