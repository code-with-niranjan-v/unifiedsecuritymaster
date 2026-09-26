package com.example.unifiedsecuritymaster.controller;

import com.example.unifiedsecuritymaster.repository.StockDataRepository;
import com.example.unifiedsecuritymaster.service.BatchJobService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.JobExecution;
import org.springframework.batch.core.job.parameters.JobParameters;
import org.springframework.batch.core.job.parameters.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.persistence.StepExecution;
import org.springframework.beans.factory.annotation.Value;
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
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@RestController
@RequestMapping("/api/v1/batch")
@RequiredArgsConstructor
public class BatchTriggerController {

    private final BatchJobService batchJobService;

    @Value("${securitymaster.scheduler.enabled:false}")
    private boolean schedulerEnabled;

    /** Stops a long job overlapping with the next trigger. */
    private final AtomicBoolean running = new AtomicBoolean(false);



    @PostMapping("/security-master/sync")
    public ResponseEntity<Map<String, Object>> syncSecurityMaster() {
        return ResponseEntity.ok(batchJobService.runSecuritySync());
    }

    @PostMapping("/stock-data/run")
    public ResponseEntity<Map<String, Object>> runStock() {
        return ResponseEntity.ok(batchJobService.runStockLoad());
    }

    @PostMapping("/mutual-fund/run")
    public ResponseEntity<Map<String, Object>> runMutualFund() {
        return ResponseEntity.ok(batchJobService.runMutualFundLoad());
    }

    @PostMapping("/commodity/run")
    public ResponseEntity<Map<String, Object>> runCommodity() {
        return ResponseEntity.ok(batchJobService.runCommodityLoad());
    }

    @PostMapping("/run-all")
    public ResponseEntity<Map<String, Object>> runAll() {
        return ResponseEntity.ok(batchJobService.runAll());
    }



    @Scheduled(cron = "${securitymaster.scheduler.security-sync-cron}",
            zone = "${securitymaster.scheduler.zone}")
    public void scheduledSecuritySync() {
        guarded("securityMasterSyncJob", batchJobService::runSecuritySync);
    }

    @Scheduled(cron = "${securitymaster.scheduler.stock-cron}",
            zone = "${securitymaster.scheduler.zone}")
    public void scheduledStockLoad() {
        guarded("stockDataLoadJob", batchJobService::runStockLoad);
    }

    @Scheduled(cron = "${securitymaster.scheduler.commodity-cron}",
            zone = "${securitymaster.scheduler.zone}")
    public void scheduledCommodityLoad() {
        guarded("commoditySpotLoadJob", batchJobService::runCommodityLoad);
    }

    @Scheduled(cron = "${securitymaster.scheduler.mf-cron}",
            zone = "${securitymaster.scheduler.zone}")
    public void scheduledMutualFundLoad() {
        guarded("mutualFundNavLoadJob", batchJobService::runMutualFundLoad);
    }



    private void guarded(String label, java.util.function.Supplier<Map<String, Object>> task) {

        if (!schedulerEnabled) {
            log.debug("SCHEDULER disabled - skipping [{}]", label);
            return;
        }
        if (!running.compareAndSet(false, true)) {
            log.warn("SCHEDULER: skipping [{}] - another job is still running", label);
            return;
        }
        try {
            log.info("SCHEDULER: starting [{}]", label);
            Map<String, Object> result = task.get();
            log.info("SCHEDULER: [{}] -> {}", label, result);
        } catch (Exception e) {
            log.error("SCHEDULER: [{}] failed: {}", label, e.getMessage(), e);
        } finally {
            running.set(false);
        }
    }
}