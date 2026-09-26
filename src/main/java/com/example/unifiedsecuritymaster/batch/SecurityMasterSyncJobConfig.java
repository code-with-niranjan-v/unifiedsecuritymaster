package com.example.unifiedsecuritymaster.batch;

import com.example.unifiedsecuritymaster.model.*;
import com.example.unifiedsecuritymaster.repository.BondRepository;
import com.example.unifiedsecuritymaster.repository.CommodityWatchListRepository;
import com.example.unifiedsecuritymaster.repository.MutualFundWatchListRepository;
import com.example.unifiedsecuritymaster.repository.StockWatchListRepository;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.batch.infrastructure.item.data.RepositoryItemReader;
import org.springframework.batch.infrastructure.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.batch.infrastructure.item.database.JpaItemWriter;
import org.springframework.batch.infrastructure.item.database.builder.JpaItemWriterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.Map;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class SecurityMasterSyncJobConfig {

    public static final String JOB_NAME = "securityMasterSyncJob";

    private static final int CHUNK = 50;


    @Bean
    public JpaItemWriter<SecurityMaster> securityMasterWriter(EntityManagerFactory emf) {
        return new JpaItemWriterBuilder<SecurityMaster>()
                .entityManagerFactory(emf)
                .usePersist(false)
                .build();
    }


    @Bean
    public RepositoryItemReader<StockWatchList> smStockReader(StockWatchListRepository repo) {
        return new RepositoryItemReaderBuilder<StockWatchList>()
                .name("smStockReader").repository(repo).methodName("findAll")
                .pageSize(CHUNK).sorts(Map.of("id", Sort.Direction.ASC)).build();
    }

    @Bean
    public Step syncStocksStep(JobRepository jobRepository,
                               PlatformTransactionManager txManager,
                               RepositoryItemReader<StockWatchList> smStockReader,
                               ItemProcessor<StockWatchList, SecurityMaster> stockToSecurityProcessor,
                               JpaItemWriter<SecurityMaster> securityMasterWriter) {
        return new StepBuilder("syncStocksStep", jobRepository)
                .<StockWatchList, SecurityMaster>chunk(CHUNK, txManager)
                .reader(smStockReader)
                .processor(stockToSecurityProcessor)
                .writer(securityMasterWriter)
                .build();
    }

    // ==================== STEP 2: MUTUAL FUNDS ====================
    @Bean
    public RepositoryItemReader<MutualFundWatchList> smFundReader(MutualFundWatchListRepository repo) {
        return new RepositoryItemReaderBuilder<MutualFundWatchList>()
                .name("smFundReader").repository(repo).methodName("findAll")
                .pageSize(CHUNK).sorts(Map.of("id", Sort.Direction.ASC)).build();
    }

    @Bean
    public Step syncFundsStep(JobRepository jobRepository,
                              PlatformTransactionManager txManager,
                              RepositoryItemReader<MutualFundWatchList> smFundReader,
                              ItemProcessor<MutualFundWatchList, SecurityMaster> mutualFundToSecurityProcessor,
                              JpaItemWriter<SecurityMaster> securityMasterWriter) {
        return new StepBuilder("syncFundsStep", jobRepository)
                .<MutualFundWatchList, SecurityMaster>chunk(CHUNK, txManager)
                .reader(smFundReader)
                .processor(mutualFundToSecurityProcessor)
                .writer(securityMasterWriter)
                .build();
    }

    // ==================== STEP 3: COMMODITIES ====================
    @Bean
    public RepositoryItemReader<CommodityWatchList> smCommodityReader(CommodityWatchListRepository repo) {
        return new RepositoryItemReaderBuilder<CommodityWatchList>()
                .name("smCommodityReader").repository(repo).methodName("findAll")
                .pageSize(CHUNK).sorts(Map.of("id", Sort.Direction.ASC)).build();
    }

    @Bean
    public Step syncCommoditiesStep(JobRepository jobRepository,
                                    PlatformTransactionManager txManager,
                                    RepositoryItemReader<CommodityWatchList> smCommodityReader,
                                    ItemProcessor<CommodityWatchList, SecurityMaster> commodityToSecurityProcessor,
                                    JpaItemWriter<SecurityMaster> securityMasterWriter) {
        return new StepBuilder("syncCommoditiesStep", jobRepository)
                .<CommodityWatchList, SecurityMaster>chunk(CHUNK, txManager)
                .reader(smCommodityReader)
                .processor(commodityToSecurityProcessor)
                .writer(securityMasterWriter)
                .build();
    }

    // ==================== JOB ====================
    @Bean
    public Job securityMasterSyncJob(JobRepository jobRepository,
                                     Step syncStocksStep,
                                     Step syncFundsStep,
                                     Step syncCommoditiesStep,Step syncBondsStep) {
        return new JobBuilder(JOB_NAME, jobRepository)
                .start(syncStocksStep)
                .next(syncFundsStep)
                .next(syncCommoditiesStep)
                .next(syncBondsStep)
                .build();
    }

    @Bean
    public RepositoryItemReader<Bond> smBondReader(BondRepository repo) {
        return new RepositoryItemReaderBuilder<Bond>()
                .name("smBondReader")
                .repository(repo)
                .methodName("findAll")
                .pageSize(CHUNK)
                .sorts(Map.of("id", Sort.Direction.ASC))
                .build();
    }

    @Bean
    public Step syncBondsStep(JobRepository jobRepository,
                              PlatformTransactionManager txManager,
                              RepositoryItemReader<Bond> smBondReader,
                              ItemProcessor<Bond, SecurityMaster> bondToSecurityProcessor,
                              JpaItemWriter<SecurityMaster> securityMasterWriter) {
        return new StepBuilder("syncBondsStep", jobRepository)
                .<Bond, SecurityMaster>chunk(CHUNK, txManager)
                .reader(smBondReader)
                .processor(bondToSecurityProcessor)
                .writer(securityMasterWriter)
                .build();
    }
}
