package com.example.unifiedsecuritymaster.service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.JobExecution;
import org.springframework.batch.core.job.parameters.JobParameters;
import org.springframework.batch.core.job.parameters.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.launch.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.step.StepExecution;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
@Slf4j
@Service
@RequiredArgsConstructor
public class BatchJobService {

    private final JobLauncher jobLauncher;

    private final Job securityMasterSyncJob;
    private final Job stockDataLoadJob;
    private final Job mutualFundNavLoadJob;
    private final Job commoditySpotLoadJob;

    // ==================== PUBLIC API ====================

    public Map<String, Object> runSecuritySync() {
        return run(securityMasterSyncJob);
    }

    public Map<String, Object> runStockLoad() {
        return run(stockDataLoadJob);
    }

    public Map<String, Object> runMutualFundLoad() {
        return run(mutualFundNavLoadJob);
    }

    public Map<String, Object> runCommodityLoad() {
        return run(commoditySpotLoadJob);
    }

    /**
     * Runs the full pipeline in dependency order:
     * reference data first, then the three price feeds.
     */
    public Map<String, Object> runAll() {
        return runAll(false);
    }

    /**
     * @param stopOnFailure if true, aborts the remaining jobs when one fails
     */
    public Map<String, Object> runAll(boolean stopOnFailure) {

        long startedAt = System.currentTimeMillis();
        Map<String, Object> summary = new LinkedHashMap<>();

        Job[] pipeline = {
                securityMasterSyncJob,   // 1. reference data
                stockDataLoadJob,        // 2. equities + ETFs
                commoditySpotLoadJob,    // 3. commodity spot
                mutualFundNavLoadJob     // 4. mutual fund NAV
        };

        log.info("==== PIPELINE START ({} jobs) ====", pipeline.length);

        for (Job job : pipeline) {
            Map<String, Object> result = run(job);
            summary.put(job.getName(), result);

            boolean failed = !"COMPLETED".equals(result.get("status"))
                    && !"SKIPPED".equals(result.get("status"));

            if (failed && stopOnFailure) {
                log.error("==== PIPELINE ABORTED at [{}] ====", job.getName());
                summary.put("aborted", true);
                summary.put("abortedAt", job.getName());
                break;
            }
        }

        long totalMs = System.currentTimeMillis() - startedAt;
        summary.put("totalDurationMs", totalMs);
        log.info("==== PIPELINE END ({} ms) ====", totalMs);

        return summary;
    }

    // ==================== CORE LAUNCH ====================

    private Map<String, Object> run(Job job) {

        String jobName  = job.getName();
        long   startedAt = System.currentTimeMillis();

        log.info("---- Launching [{}] ----", jobName);

        try {
            JobExecution execution = jobLauncher.run(job, buildParameters());
            return toResult(jobName, execution, startedAt);

        } catch (JobInstanceAlreadyCompleteException e) {
            // Same businessDate already processed - idempotency guard, not a failure
            log.info("[{}] already completed for today - skipping", jobName);
            return simpleResult(jobName, "SKIPPED",
                    "Already completed for businessDate " + LocalDate.now(), startedAt);

        } catch (JobExecutionAlreadyRunningException e) {
            log.warn("[{}] is already running - skipping", jobName);
            return simpleResult(jobName, "SKIPPED", "Job already running", startedAt);

        } catch (Exception e) {
            log.error("[{}] launch failed: {}", jobName, e.getMessage(), e);
            return simpleResult(jobName, "ERROR",
                    e.getClass().getSimpleName() + ": " + e.getMessage(), startedAt);
        }
    }

    /**
     * businessDate is identifying -> one run per job per day (idempotent).
     * runId is identifying too so a manual re-run is always possible.
     */
    private JobParameters buildParameters() {
        return new JobParametersBuilder()
                .addJobParameter("businessDate", LocalDate.now().toString(), String.class, true)
                .addJobParameter("runId", System.currentTimeMillis(), Long.class, true)
                .addJobParameter("launchedAt", LocalDateTime.now().toString(), String.class, false)
                .toJobParameters();
    }

    // ==================== RESULT MAPPING ====================

    private Map<String, Object> toResult(String jobName, JobExecution execution, long startedAt) {

        long read    = execution.getStepExecutions().stream().mapToLong(StepExecution::getReadCount).sum();
        long written = execution.getStepExecutions().stream().mapToLong(StepExecution::getWriteCount).sum();
        long skipped = execution.getStepExecutions().stream().mapToLong(StepExecution::getSkipCount).sum();
        long filtered= execution.getStepExecutions().stream().mapToLong(StepExecution::getFilterCount).sum();

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("jobName",        jobName);
        result.put("jobExecutionId", execution.getId());
        result.put("status",         execution.getStatus().name());
        result.put("exitCode",       execution.getExitStatus().getExitCode());
        result.put("read",           read);
        result.put("written",        written);
        result.put("filtered",       filtered);
        result.put("skipped",        skipped);
        result.put("startTime",      String.valueOf(execution.getStartTime()));
        result.put("endTime",        String.valueOf(execution.getEndTime()));
        result.put("durationMs",     System.currentTimeMillis() - startedAt);

        // per-step breakdown
        Map<String, Object> steps = new LinkedHashMap<>();
        for (StepExecution se : execution.getStepExecutions()) {
            steps.put(se.getStepName(), Map.of(
                    "status",  se.getStatus().name(),
                    "read",    se.getReadCount(),
                    "written", se.getWriteCount(),
                    "skipped", se.getSkipCount()));
        }
        result.put("steps", steps);

        // surface the first failure so callers don't have to dig through logs
        execution.getAllFailureExceptions().stream().findFirst()
                .ifPresent(t -> result.put("error", t.getMessage()));

        if (execution.getStatus() == BatchStatus.COMPLETED) {
            log.info("[{}] COMPLETED  read={} written={} skipped={} in {} ms",
                    jobName, read, written, skipped, result.get("durationMs"));
        } else {
            log.error("[{}] {}  read={} written={} skipped={}",
                    jobName, execution.getStatus(), read, written, skipped);
        }
        return result;
    }

    private Map<String, Object> simpleResult(String jobName, String status,
                                             String message, long startedAt) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("jobName",    jobName);
        result.put("status",     status);
        result.put("message",    message);
        result.put("durationMs", System.currentTimeMillis() - startedAt);
        return result;
    }
}
