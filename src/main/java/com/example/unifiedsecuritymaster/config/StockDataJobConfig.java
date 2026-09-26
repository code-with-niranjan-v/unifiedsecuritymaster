package com.example.unifiedsecuritymaster.config;

import com.example.unifiedsecuritymaster.batch.ListUnpackingItemWriter;
import com.example.unifiedsecuritymaster.batch.StockSkipListener;
import com.example.unifiedsecuritymaster.exception.CsvDownloadException;
import com.example.unifiedsecuritymaster.model.StockData;
import com.example.unifiedsecuritymaster.model.StockWatchList;
import com.example.unifiedsecuritymaster.repository.StockWatchListRepository;
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

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class StockDataJobConfig {

    public static final String JOB_NAME  = "stockDataLoadJob";
    public static final String STEP_NAME = "loadStockDataStep";

    private final BatchProperties batchProps;


    @Bean
    public RepositoryItemReader<StockWatchList> watchListReader(StockWatchListRepository repo) {
        return new RepositoryItemReaderBuilder<StockWatchList>()
                .name("watchListReader")
                .repository(repo)
                .methodName("findAll")                       // inherited Page<T> findAll(Pageable)
                .pageSize(batchProps.getPageSize())
                .sorts(Map.of("id", Sort.Direction.ASC))
                .saveState(true)
                .build();
    }

    // ==================== WRITER ====================
    @Bean
    public JdbcBatchItemWriter<StockData> stockDataJdbcWriter(DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<StockData>()
                .dataSource(dataSource)
                .sql("""
                     INSERT INTO stock_data
                         (symbol, trade_date, open_price, close_price, watchlist_id)
                     VALUES
                         (:symbol, :tradeDate, :openPrice, :closePrice, :watchlistId)
                     ON CONFLICT (symbol, trade_date)
                     DO UPDATE SET open_price   = EXCLUDED.open_price,
                                   close_price  = EXCLUDED.close_price,
                                   watchlist_id = EXCLUDED.watchlist_id
                     """)
                .beanMapped()
                .assertUpdates(false)
                .build();
    }

    @Bean
    public ItemWriter<List<StockData>> stockDataWriter(JdbcBatchItemWriter<StockData> delegate) {
        return new ListUnpackingItemWriter<>(delegate);
    }


    @Bean
    public Step loadStockDataStep(JobRepository jobRepository,
                                  PlatformTransactionManager transactionManager,
                                  RepositoryItemReader<StockWatchList> watchListReader,
                                  ItemProcessor<StockWatchList, List<StockData>> stockCsvProcessor,
                                  ItemWriter<List<StockData>> stockDataWriter) {

        ExponentialBackOffPolicy backOff = new ExponentialBackOffPolicy();
        backOff.setInitialInterval(1000);
        backOff.setMultiplier(2.0);
        backOff.setMaxInterval(15000);

        return new StepBuilder(STEP_NAME, jobRepository)
                .<StockWatchList, List<StockData>>chunk(batchProps.getChunkSize(), transactionManager)
                .reader(watchListReader)
                .processor(stockCsvProcessor)
                .writer(stockDataWriter)
                .faultTolerant()
                .retry(CsvDownloadException.class)
                .retryLimit(batchProps.getRetryLimit())
                .backOffPolicy(backOff)
                .skip(CsvDownloadException.class)
                .skipLimit(batchProps.getSkipLimit())
                .listener(new StockSkipListener())
                .build();
    }


    @Bean
    public Job stockDataLoadJob(JobRepository jobRepository, Step loadStockDataStep) {
        return new JobBuilder(JOB_NAME, jobRepository)
                .start(loadStockDataStep)
                .build();
    }
}
