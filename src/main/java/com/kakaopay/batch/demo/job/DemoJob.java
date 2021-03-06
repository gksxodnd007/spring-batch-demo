package com.kakaopay.batch.demo.job;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableBatchProcessing
public class DemoJob {

    private static final String JOB_NAME = "demoJop";

    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    public DemoJob(JobBuilderFactory jobBuilderFactory) {
        this.jobBuilderFactory = jobBuilderFactory;
    }

    @Bean
    public Job job(Step firstStep, Step secondStep, Step finalStep) {
        return jobBuilderFactory.get(JOB_NAME)
                .start(firstStep)
                .next(secondStep)
                .next(finalStep)
                .build();
    }

}
