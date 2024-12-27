package com.lion.BMWtour.entitiy;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TourInfoDto {
    private TourInfo tourInfo;
    private int infoCount;
    private float matchScore;
}
