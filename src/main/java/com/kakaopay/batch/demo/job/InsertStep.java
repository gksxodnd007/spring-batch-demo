package com.kakaopay.batch.demo.job;

import com.kakaopay.batch.demo.dto.TakoyakiFoodTruckDto;
import com.kakaopay.batch.demo.entity.TakoyakiFoodTruck;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManagerFactory;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

@Component
public class InsertStep {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final String STEP_NAME = "insertStep";

    private StepBuilderFactory stepBuilderFactory;
    private ResourceLoader resourceLoader;
    private EntityManagerFactory entityManagerFactory;
    private PlatformTransactionManager transactionManager;

    private Queue<String> csvFileDatas;

    @Autowired
    public InsertStep (EntityManagerFactory entityManagerFactory,
                       StepBuilderFactory stepBuilderFactory,
                       ResourceLoader resourceLoader,
                       @Qualifier("demoTransactionManager") PlatformTransactionManager transactionManager) {
        this.entityManagerFactory = entityManagerFactory;
        this.stepBuilderFactory = stepBuilderFactory;
        this.resourceLoader = resourceLoader;
        this.transactionManager = transactionManager;
    }

    @PostConstruct
    public void init() {
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(resourceLoader.getResource("classpath:takoyakiDummyData.txt").getInputStream(), "UTF-8"));
            csvFileDatas = new LinkedList<>();

            bufferedReader.lines().forEach(line -> csvFileDatas.add(line));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //partitioner를 통해 멀티 스레드로 돌릴 경우도 있다.
    //TODO Reader에서 더이상 읽을 데이터가 없으면 JOB이 종료된다.
    private ItemReader<TakoyakiFoodTruckDto> reader() {
        return () -> {
            if (!csvFileDatas.isEmpty()) {
                List<String> line = Arrays.asList(csvFileDatas.poll().split(","));
                TakoyakiFoodTruckDto foodTruckDto = new TakoyakiFoodTruckDto();
                foodTruckDto.setName(line.get(0));
                foodTruckDto.setLatitude(Double.parseDouble(line.get(1)));
                foodTruckDto.setLongitude(Double.parseDouble(line.get(2)));
                foodTruckDto.setRegion(line.get(3));
                foodTruckDto.setDescription(line.get(4));
                logger.info("reader : {}", foodTruckDto.toString());


                return foodTruckDto;
            } else {
                return null;
            }
        };
    }

    private ItemProcessor<TakoyakiFoodTruckDto, TakoyakiFoodTruck> processor() {
        return item -> {
            TakoyakiFoodTruck truck = new TakoyakiFoodTruck();
            truck.setName(item.getName());
            truck.setLatitude(item.getLatitude());
            truck.setLongitude(item.getLongitude());
            truck.setRegion(item.getRegion());
            truck.setDescription(item.getDescription());
            logger.info("processor : {}", item.toString());

            return truck;
        };
    }

    private JpaItemWriter<TakoyakiFoodTruck> writer() {
        JpaItemWriter<TakoyakiFoodTruck> writer = new JpaItemWriter<>();
        writer.setEntityManagerFactory(entityManagerFactory);
        logger.info("writer 실행");
        return writer;
    }

    @Bean(name = "firstStep")
    public Step firstStep() {
        return stepBuilderFactory.get(STEP_NAME)
                .transactionManager(transactionManager)
                .<TakoyakiFoodTruckDto, TakoyakiFoodTruck>chunk(5)
                .reader(reader())
                .processor(processor())
                .writer(writer())
                .build();
    }


}
