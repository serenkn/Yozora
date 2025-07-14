package com.example.yozora.entity;

import lombok.Data;

//locationテーブル
@Data
public class LocationsEntity {

    private Integer locationId;
    private String locationName;
    private Double latitude;
    private Double longitude;
    private String area;
    private String locationDescription;
}
