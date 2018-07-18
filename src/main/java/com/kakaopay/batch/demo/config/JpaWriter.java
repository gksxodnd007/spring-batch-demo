package com.kakaopay.batch.demo.config;

import com.kakaopay.batch.demo.entity.TakoyakiFoodTruck;
import com.kakaopay.batch.demo.repository.TakoyakiFoodTruckRepository;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * JpaWriter Config -> 이해하지 못한 config 이므로 추후 에러 수정시 삭제 요청
 */
//@Configuration
public class JpaWriter {

    private TakoyakiFoodTruckRepository repository;

//    @Autowired
    public JpaWriter(TakoyakiFoodTruckRepository repository) {
        this.repository = repository;
    }

//    @Bean
    public RepositoryItemWriter<TakoyakiFoodTruck> writer() {
        RepositoryItemWriter<TakoyakiFoodTruck> writer = new RepositoryItemWriter<>();
        writer.setRepository(repository);
        writer.setMethodName("save");
        return writer;
    }

}
