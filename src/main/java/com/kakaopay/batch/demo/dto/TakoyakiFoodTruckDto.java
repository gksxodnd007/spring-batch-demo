package com.kakaopay.batch.demo.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter
@ToString
public class TakoyakiFoodTruckDto {

    private String name;
    private double latitude;
    private double longitude;
    private String region;
    private String description;

}
