package com.lion.BMWtour.dto.direction;

import com.lion.BMWtour.dto.PointDto;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
/**가이드 정보 + 가이드 경로 구성 좌표 DTO*/
@Data
@AllArgsConstructor
public class GuideAndPointDto {
    List<GuideDto> guideDtoList; // 가이드 정보 리스트
    List<PointDto> pointDtoList;//가이드 경로를 구성하는 좌표 리스트
}
