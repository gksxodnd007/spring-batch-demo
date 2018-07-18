package com.kakaopay.batch.demo.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "TB_TAKOYAKI_FOOD_TRUCK")
@Getter @Setter
public class TakoyakiFoodTruck {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "latitude", nullable = false)
    private double latitude;

    @Column(name = "longitude", nullable = false)
    private double longitude;

    @Column(name = "region", nullable = false)
    private String region;

    @Column(name = "description", nullable = false, length = 200)
    private String description;

}
