package com.kakaopay.batch.demo.job;

import com.kakaopay.batch.demo.repository.TakoyakiFoodTruckRepository;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;

@Component
public class UpdateStep {

    private StepBuilderFactory stepBuilderFactory;
    private TakoyakiFoodTruckRepository repository;
    private PlatformTransactionManager txManager;

    @Autowired
    public UpdateStep(StepBuilderFactory stepBuilderFactory,
                      TakoyakiFoodTruckRepository repository,
                      @Qualifier("demoTransactionManager") PlatformTransactionManager txManager) {
        this.stepBuilderFactory = stepBuilderFactory;
        this.repository = repository;
        this.txManager = txManager;
    }

    //TODO DB에서 데이터를 꺼내와 컬럼 값을 모두 수정한 후 다시 저장하는 Step을 생성해보자.
    private Tasklet taskletStep() {
        return (StepContribution contribution, ChunkContext chunkContext) -> {
            repository.findAll().forEach(
                    truck -> {
                        truck.setName(truck.getName() + "_takoyaki_truck");
                        repository.save(truck);
                    }
            );
            return RepeatStatus.FINISHED;
        };
    }

    @Bean(name = "secondStep")
    public Step secondStep() {
        return stepBuilderFactory.get("secondStep")
                .transactionManager(txManager)
                .tasklet(taskletStep())
                .build();
    }

    @Bean(name = "finalStep")
    public Step finalStep() {
        return stepBuilderFactory.get("finalStep")
                .transactionManager(txManager)
                .tasklet((StepContribution contribution, ChunkContext chunkContext) -> {
                    repository.deleteAll();
                    return RepeatStatus.FINISHED;
                })
                .build();
    }

}
