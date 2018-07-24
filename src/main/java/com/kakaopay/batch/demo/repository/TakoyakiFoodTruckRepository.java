package com.kakaopay.batch.demo.repository;

import com.kakaopay.batch.demo.entity.TakoyakiFoodTruck;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TakoyakiFoodTruckRepository extends JpaRepository<TakoyakiFoodTruck, Integer> {

    @Modifying
    @Query(value = "DELETE FROM TB_TAKOYAKI_FOOD_TRUCK", nativeQuery = true)
    void deleteAll();

}
