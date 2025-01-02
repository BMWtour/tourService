package com.lion.BMWtour.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PointNameDto {
    private Double lat;
    private Double lng;
    private String title;

    public String toQueryValue() {
        return String.format("%s:[ %f, %f ] ", title, lng, lat);
    }
}
