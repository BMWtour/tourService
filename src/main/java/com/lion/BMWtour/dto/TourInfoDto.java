package com.lion.BMWtour.dto;


import com.lion.BMWtour.entity.TourInfo;
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
    private String image;
    private float matchScore;
}
