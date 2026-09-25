package com.example.unifiedsecuritymaster.config;

import com.example.unifiedsecuritymaster.batch.CommoditySkipListener;
import com.example.unifiedsecuritymaster.batch.ListUnpackingItemWriter;
import com.example.unifiedsecuritymaster.exception.CommodityApiException;
import com.example.unifiedsecuritymaster.model.CommoditySpotData;
import com.example.unifiedsecuritymaster.model.CommodityWatchList;
import com.example.unifiedsecuritymaster.repository.CommodityWatchListRepository;
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
public class CommodityBatchConfig {

    public static final String JOB_NAME  = "commoditySpotLoadJob";
    public static final String STEP_NAME = "loadCommoditySpotStep";

    private final BatchProperties batchProps;


    @Bean
    public RepositoryItemReader<CommodityWatchList> commodityWatchListReader(
            CommodityWatchListRepository repo) {
        return new RepositoryItemReaderBuilder<CommodityWatchList>()
                .name("commodityWatchListReader")
                .repository(repo)
                .methodName("findAll")
                .pageSize(batchProps.getPageSize())
                .sorts(Map.of("id", Sort.Direction.ASC))
                .saveState(true)
                .build();
    }


    @Bean
    public JdbcBatchItemWriter<CommoditySpotData> commoditySpotJdbcWriter(DataSource ds) {
        return new JdbcBatchItemWriterBuilder<CommoditySpotData>()
                .dataSource(ds)
                .sql("""
                     INSERT INTO commodity_spot_data
                         (symbol, spot_date, spot_price1, spot_price2, spot_price,
                          quotation, price_timestamp, is_final, watchlist_id)
                     VALUES
                         (:symbol, :spotDate, :spotPrice1, :spotPrice2, :spotPrice,
                          :quotation, :priceTimestamp, :isFinal, :watchlistId)
                     ON CONFLICT (symbol, spot_date)
                     DO UPDATE SET spot_price1     = EXCLUDED.spot_price1,
                                   spot_price2     = EXCLUDED.spot_price2,
                                   spot_price      = EXCLUDED.spot_price,
                                   price_timestamp = EXCLUDED.price_timestamp,
                                   is_final        = EXCLUDED.is_final,
                                   watchlist_id    = EXCLUDED.watchlist_id
                     """)
                .beanMapped()
                .assertUpdates(false)
                .build();
    }

    @Bean
    public ItemWriter<List<CommoditySpotData>> commoditySpotWriter(
            JdbcBatchItemWriter<CommoditySpotData> delegate) {
        return new ListUnpackingItemWriter<>(delegate);
    }


    @Bean
    public Step loadCommoditySpotStep(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            RepositoryItemReader<CommodityWatchList> commodityWatchListReader,
            ItemProcessor<CommodityWatchList, List<CommoditySpotData>> commoditySpotProcessor,
            ItemWriter<List<CommoditySpotData>> commoditySpotWriter) {

        ExponentialBackOffPolicy backOff = new ExponentialBackOffPolicy();
        backOff.setInitialInterval(1000);
        backOff.setMultiplier(2.0);
        backOff.setMaxInterval(15000);

        return new StepBuilder(STEP_NAME, jobRepository)
                .<CommodityWatchList, List<CommoditySpotData>>chunk(
                        batchProps.getChunkSize(), transactionManager)
                .reader(commodityWatchListReader)
                .processor(commoditySpotProcessor)
                .writer(commoditySpotWriter)
                .faultTolerant()
                .retry(CommodityApiException.class)
                .retry(ResourceAccessException.class)
                .retryLimit(batchProps.getRetryLimit())
                .backOffPolicy(backOff)
                .skip(CommodityApiException.class)
                .skipLimit(batchProps.getSkipLimit())
                .listener(new CommoditySkipListener())
                .build();
    }


    @Bean
    public Job commoditySpotLoadJob(JobRepository jobRepository, Step loadCommoditySpotStep) {
        return new JobBuilder(JOB_NAME, jobRepository)
                .start(loadCommoditySpotStep)
                .build();
    }
}
