package com.example.unifiedsecuritymaster.config;

import com.example.unifiedsecuritymaster.batch.ListUnpackingItemWriter;
import com.example.unifiedsecuritymaster.batch.MutualFundSkipListener;
import com.example.unifiedsecuritymaster.exception.MutualFundApiException;
import com.example.unifiedsecuritymaster.model.MutualFundNav;
import com.example.unifiedsecuritymaster.model.MutualFundWatchList;
import com.example.unifiedsecuritymaster.repository.MutualFundWatchListRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.batch.infrastructure.item.ItemWriter;
import org.springframework.batch.infrastructure.item.data.RepositoryItemReader;
import org.springframework.batch.infrastructure.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.batch.infrastructure.item.database.JdbcBatchItemWriter;
import org.springframework.batch.infrastructure.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.web.client.ResourceAccessException;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class MutualFundBatchConfig {

    public static final String JOB_NAME  = "mutualFundNavLoadJob";
    public static final String STEP_NAME = "loadMutualFundNavStep";

    private final BatchProperties batchProps;

    // ==================== READER ====================
    @Bean
    public RepositoryItemReader<MutualFundWatchList> mfWatchListReader(
            MutualFundWatchListRepository repo) {

        return new RepositoryItemReaderBuilder<MutualFundWatchList>()
                .name("mfWatchListReader")
                .repository(repo)
                .methodName("findAll")
                .pageSize(batchProps.getPageSize())
                .sorts(Map.of("id", Sort.Direction.ASC))
                .saveState(true)
                .build();
    }

    // ==================== WRITER ====================
    @Bean
    public JdbcBatchItemWriter<MutualFundNav> mfNavJdbcWriter(DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<MutualFundNav>()
                .dataSource(dataSource)
                .sql("""
                     INSERT INTO mutualfund_nav
                         (isin, scheme_code, scheme_name, nav_date, nav, watchlist_id)
                     VALUES
                         (:isin, :schemeCode, :schemeName, :navDate, :nav, :watchlistId)
                     ON CONFLICT (isin, nav_date)
                     DO UPDATE SET nav          = EXCLUDED.nav,
                                   scheme_code  = EXCLUDED.scheme_code,
                                   scheme_name  = EXCLUDED.scheme_name,
                                   watchlist_id = EXCLUDED.watchlist_id
                     """)
                .beanMapped()
                .assertUpdates(false)
                .build();
    }

    @Bean
    public ItemWriter<List<MutualFundNav>> mfNavWriter(
            JdbcBatchItemWriter<MutualFundNav> delegate) {
        return new ListUnpackingItemWriter<>(delegate);
    }

    // ==================== STEP ====================
    @Bean
    public Step loadMutualFundNavStep(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            RepositoryItemReader<MutualFundWatchList> mfWatchListReader,
            ItemProcessor<MutualFundWatchList, List<MutualFundNav>> mutualFundNavProcessor,
            ItemWriter<List<MutualFundNav>> mfNavWriter) {

        ExponentialBackOffPolicy backOff = new ExponentialBackOffPolicy();
        backOff.setInitialInterval(1000);
        backOff.setMultiplier(2.0);
        backOff.setMaxInterval(15000);

        return new StepBuilder(STEP_NAME, jobRepository)
                .<MutualFundWatchList, List<MutualFundNav>>chunk(
                        batchProps.getChunkSize(), transactionManager)
                .reader(mfWatchListReader)
                .processor(mutualFundNavProcessor)
                .writer(mfNavWriter)
                .faultTolerant()
                .retry(MutualFundApiException.class)
                .retry(ResourceAccessException.class)
                .retryLimit(batchProps.getRetryLimit())
                .backOffPolicy(backOff)
                .skip(MutualFundApiException.class)
                .skipLimit(batchProps.getSkipLimit())
                .listener(new MutualFundSkipListener())
                .build();
    }

    // ==================== JOB ====================
    @Bean
    public Job mutualFundNavLoadJob(JobRepository jobRepository,
                                    Step loadMutualFundNavStep) {
        return new JobBuilder(JOB_NAME, jobRepository)
                .start(loadMutualFundNavStep)
                .build();
    }
}