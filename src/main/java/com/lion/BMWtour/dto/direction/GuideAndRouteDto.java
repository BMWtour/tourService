package com.lion.BMWtour.dto.direction;

import com.lion.BMWtour.dto.NaviRouteDto;
import lombok.AllArgsConstructor;
import lombok.Data;

/**가이드 정보(좌표포함) + 이동경로 정보 DTO*/
@Data
@AllArgsConstructor
public class GuideAndRouteDto {
    GuideAndPointDto guideAndPointDto; // 가이드 정보(좌표값 포함).
    NaviRouteDto naviRouteDto; // 이동경로 좌표 정보
}
