package com.example.unifiedsecuritymaster.controller;

import com.example.unifiedsecuritymaster.repository.StockDataRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.JobExecution;
import org.springframework.batch.core.job.parameters.JobParameters;
import org.springframework.batch.core.job.parameters.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.persistence.StepExecution;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/batch")
@RequiredArgsConstructor
public class BatchTriggerController {

    private final JobLauncher jobLauncher;
    private final Job mutualFundNavLoadJob;
    private final Job stockDataLoadJob;
    private final StockDataRepository stockDataRepository;
    private final Job commoditySpotLoadJob;
    private final Job securityMasterSyncJob;
    @PostMapping("/stock-data/run")
    public ResponseEntity<Map<String, Object>> runStockDataJob() throws Exception {

        JobParameters params = new JobParametersBuilder()
                .addJobParameter("businessDate", LocalDate.now().toString(), String.class, true)
                .addJobParameter("runId", System.currentTimeMillis(), Long.class, true)
                .toJobParameters();

        JobExecution execution = jobLauncher.run(stockDataLoadJob, params);

//        long read    = execution.getStepExecutions().stream().mapToLong(StepExecution::getReadCount).sum();
//        long written = execution.getStepExecutions().stream().mapToLong(StepExecution::getWriteCount).sum();
//        long skipped = execution.getStepExecutions().stream().mapToLong(StepExecution::getSkipCount).sum();
        long read = 0;
        long written = 0;
        long skipped = 0;
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("jobExecutionId", execution.getId());
        body.put("status",         execution.getStatus().name());
        body.put("exitCode",       execution.getExitStatus().getExitCode());
        body.put("watchlistRead",  read);
        body.put("stockDataWritten", written);
        body.put("skipped",        skipped);
        body.put("startTime",      String.valueOf(execution.getStartTime()));
        body.put("endTime",        String.valueOf(execution.getEndTime()));

        return ResponseEntity.ok(body);
    }

    @GetMapping("/stock-data/summary")
    public ResponseEntity<List<Object[]>> summary() {
        return ResponseEntity.ok(stockDataRepository.summariseBySymbol());
    }

    @Scheduled(cron = "${securitymaster.schedule.cron}",
            zone = "${securitymaster.schedule.zone}")
    public void scheduledRun() {
        try {
            log.info("Scheduled stock data load starting...");
            runStockDataJob();
        } catch (Exception e) {
            log.error("Scheduled run failed", e);
        }
    }

    @PostMapping("/mutual-fund/run")
    public ResponseEntity<Map<String, Object>> runMutualFundJob() throws Exception {

        JobParameters params = new JobParametersBuilder()
                .addJobParameter("businessDate", LocalDate.now().toString(), String.class, true)
                .addJobParameter("runId", System.currentTimeMillis(), Long.class, true)
                .toJobParameters();

        JobExecution execution = jobLauncher.run(mutualFundNavLoadJob, params);

//        long read    = execution.getStepExecutions().stream().mapToLong(StepExecution::getReadCount).sum();
//        long written = execution.getStepExecutions().stream().mapToLong(StepExecution::getWriteCount).sum();
//        long skipped = execution.getStepExecutions().stream().mapToLong(StepExecution::getSkipCount).sum();
        long read = 0;
        long written = 0;
        long skipped = 0;
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("jobExecutionId", execution.getId());
        body.put("status",         execution.getStatus().name());
        body.put("schemesRead",    read);
        body.put("navRecordsWritten", written);
        body.put("skipped",        skipped);
        return ResponseEntity.ok(body);
    }

    @PostMapping("/commodity/run")
    public ResponseEntity<Map<String, Object>> runCommodityJob() throws Exception {
        JobParameters params = new JobParametersBuilder()
                .addJobParameter("businessDate", LocalDate.now().toString(), String.class, true)
                .addJobParameter("runId", System.currentTimeMillis(), Long.class, true)
                .toJobParameters();

        JobExecution ex = jobLauncher.run(commoditySpotLoadJob, params);

        return ResponseEntity.ok(Map.of(
                "jobExecutionId", ex.getId(),
                "status",  ex.getStatus().name(),
                "written", 0
        ));
    }

    @PostMapping("/security-master/sync")
    public ResponseEntity<Map<String, Object>> syncSecurityMaster() throws Exception {

        JobParameters params = new JobParametersBuilder()
                .addJobParameter("runId", System.currentTimeMillis(), Long.class, true)
                .toJobParameters();

        JobExecution ex = jobLauncher.run(securityMasterSyncJob, params);

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("status", ex.getStatus().name());
        ex.getStepExecutions().forEach(se ->
                body.put(se.getStepName(), Map.of(
                        "read",    se.getReadCount(),
                        "written", se.getWriteCount())));
        return ResponseEntity.ok(body);
    }

}