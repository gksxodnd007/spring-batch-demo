package com.kakaopay.batch.demo.repository;

import com.kakaopay.batch.demo.entity.TakoyakiFoodTruck;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TakoyakiFoodTruckRepository extends JpaRepository<TakoyakiFoodTruck, Integer> {

    Optional<TakoyakiFoodTruck> findByName(String name);

}
