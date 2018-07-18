package com.kakaopay.batch.demo.config;

import com.kakaopay.batch.demo.dto.TakoyakiFoodTruckDto;
import com.kakaopay.batch.demo.entity.TakoyakiFoodTruck;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;
import org.springframework.transaction.PlatformTransactionManager;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManagerFactory;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

@Configuration
@EnableBatchProcessing
public class JobConfig {

    private static final String JOB_NAME = "demoJop";
    private static final String STEP_NAME = "demoStep";

    private BufferedReader bufferedReader;
    private Queue<String> csvFileDatas;

    private JobBuilderFactory jobBuilderFactory;
    private EntityManagerFactory entityManagerFactory;
    private StepBuilderFactory stepBuilderFactory;
    private ResourceLoader resourceLoader;
    private PlatformTransactionManager transactionManager;

    @Autowired
    public JobConfig(JobBuilderFactory jobBuilderFactory,
                     EntityManagerFactory entityManagerFactory,
                     StepBuilderFactory stepBuilderFactory,
                     ResourceLoader resourceLoader,
                     @Qualifier("demoTransactionManager") PlatformTransactionManager transactionManager) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.entityManagerFactory = entityManagerFactory;
        this.stepBuilderFactory = stepBuilderFactory;
        this.resourceLoader = resourceLoader;
        this.transactionManager = transactionManager;
    }

    @Bean
    public Job job(Step step) {
        return jobBuilderFactory.get(JOB_NAME)
                .start(step)
                .build();
    }

    @PostConstruct
    public void init() {
        try {
            bufferedReader = new BufferedReader(new InputStreamReader(resourceLoader.getResource("classpath:takoyakiDummyData.txt").getInputStream(), "UTF-8"));
            csvFileDatas = new LinkedList<>();

            bufferedReader.lines().forEach(line -> csvFileDatas.add(line));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //partitioner를 통해 멀티 스레드로 돌릴 경우도 있다.
    //TODO Reader에서 더이상 읽을 데이터가 없으면 JOB이 종료된다.
    @Bean(name = "reader")
    public ItemReader<TakoyakiFoodTruckDto> reader() {
        return () -> {
            if (!csvFileDatas.isEmpty()) {
                List<String> line = Arrays.asList(csvFileDatas.poll().split(","));
                TakoyakiFoodTruckDto foodTruckDto = new TakoyakiFoodTruckDto();
                foodTruckDto.setName(line.get(0));
                foodTruckDto.setLatitude(Double.parseDouble(line.get(1)));
                foodTruckDto.setLongitude(Double.parseDouble(line.get(2)));
                foodTruckDto.setRegion(line.get(3));
                foodTruckDto.setDescription(line.get(4));

                return foodTruckDto;
            } else {
                return null;
            }
        };
    }

    @Bean(name = "processor")
    public ItemProcessor<TakoyakiFoodTruckDto, TakoyakiFoodTruck> processor() {
        return item -> {
            TakoyakiFoodTruck truck = new TakoyakiFoodTruck();
            truck.setName(item.getName());
            truck.setLatitude(item.getLatitude());
            truck.setLongitude(item.getLongitude());
            truck.setRegion(item.getRegion());
            truck.setDescription(item.getDescription());

            return truck;
        };
    }

    @Bean(name = "writer")
    public JpaItemWriter<TakoyakiFoodTruck> writer() {
        JpaItemWriter<TakoyakiFoodTruck> writer = new JpaItemWriter<>();
        writer.setEntityManagerFactory(entityManagerFactory);
        return writer;
    }

    @Bean
    public Step step(ItemReader<TakoyakiFoodTruckDto> reader,
                     ItemProcessor<TakoyakiFoodTruckDto, TakoyakiFoodTruck> processor,
                     JpaItemWriter<TakoyakiFoodTruck> writer) {
        return stepBuilderFactory.get(STEP_NAME)
                .transactionManager(transactionManager)
                .<TakoyakiFoodTruckDto, TakoyakiFoodTruck>chunk(1)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }

}
