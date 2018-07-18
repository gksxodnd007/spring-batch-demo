package com.kakaopay.batch.demo.job.writer;

import com.kakaopay.batch.demo.dto.TakoyakiFoodTruckDto;
import com.kakaopay.batch.demo.entity.TakoyakiFoodTruck;
import com.kakaopay.batch.demo.repository.TakoyakiFoodTruckRepository;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
@StepScope
public class TakoyakiFoodTruckDataWriter implements ItemWriter<TakoyakiFoodTruckDto> {

    private final TakoyakiFoodTruckRepository repository;

    @Autowired
    public TakoyakiFoodTruckDataWriter(TakoyakiFoodTruckRepository repository) {
        this.repository = repository;
    }

    @Override
    public void write(List<? extends TakoyakiFoodTruckDto> items) throws Exception {
        items.forEach(elem -> {
            TakoyakiFoodTruck foodTruck = new TakoyakiFoodTruck();
            foodTruck.setName(elem.getName());
            foodTruck.setLatitude(foodTruck.getLatitude());
            foodTruck.setLongitude(foodTruck.getLongitude());
            foodTruck.setRegion(foodTruck.getRegion());
            foodTruck.setDescription(foodTruck.getDescription());

            repository.save(foodTruck);
        });
    }
}
