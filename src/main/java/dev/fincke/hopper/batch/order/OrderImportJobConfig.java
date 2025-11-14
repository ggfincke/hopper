package dev.fincke.hopper.batch.order;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

// Spring Batch configuration for the order import job
@Configuration
@EnableBatchProcessing
@EnableConfigurationProperties(OrderImportJobProperties.class)
@SuppressWarnings("null")
public class OrderImportJobConfig
{
    @Bean
    public Job orderImportJob(JobRepository jobRepository, Step orderImportStep)
    {
        return new JobBuilder("orderImportJob", jobRepository)
            .incrementer(new RunIdIncrementer())
            .start(orderImportStep)
            .build();
    }

    @Bean
    public Step orderImportStep(JobRepository jobRepository,
                                PlatformTransactionManager transactionManager,
                                ItemReader<ExternalOrderRecord> reader,
                                ItemProcessor<ExternalOrderRecord, OrderImportRequest> processor,
                                ItemWriter<OrderImportRequest> writer,
                                OrderImportJobProperties properties)
    {
        int chunkSize = Math.max(1, properties.getChunkSize());

        return new StepBuilder("orderImportStep", jobRepository)
            .<ExternalOrderRecord, OrderImportRequest>chunk(chunkSize, transactionManager)
            .reader(reader)
            .processor(processor)
            .writer(writer)
            .build();
    }
}
