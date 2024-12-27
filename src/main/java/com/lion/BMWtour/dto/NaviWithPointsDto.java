package com.lion.BMWtour.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
@Data
@AllArgsConstructor
public class NaviWithPointsDto {
    private PointDto start;
    private PointDto goal;
}
