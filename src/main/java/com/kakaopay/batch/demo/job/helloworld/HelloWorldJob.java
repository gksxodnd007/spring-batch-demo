package com.kakaopay.batch.demo.job.helloworld;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.stream.IntStream;

@Configuration
@EnableBatchProcessing
public class HelloWorldJob {

    private static final String HELLO_WORLD_JOB_NAME = "helloWorldJob";

    private JobBuilderFactory jobBuilderFactory;
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    public HelloWorldJob(JobBuilderFactory jobBuilderFactory,
                         StepBuilderFactory stepBuilderFactory) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
    }

    @Bean
    public Job printHelloWorldJob(Step helloWorldStep) {
        return jobBuilderFactory.get(HELLO_WORLD_JOB_NAME)
                .start(helloWorldStep)
                .build();
    }

    private Tasklet helloTasklet() {
        return (StepContribution configuration, ChunkContext context) -> {
            IntStream.range(1, 10).forEach(num -> {
                System.out.println("hello world spring batch : " + num);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
            return RepeatStatus.FINISHED;
        };
    }

    @Bean
    public Step helloWorldStep() {
        return stepBuilderFactory.get("helloStep")
                .tasklet(helloTasklet())
                .build();
    }
}
