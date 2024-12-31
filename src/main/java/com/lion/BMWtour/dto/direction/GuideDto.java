package com.lion.BMWtour.dto.direction;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

/**분기점 안내 정보.
 * 가이드 DTO*/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GuideDto {
    int pointIndex; // 경로를 구성하는 좌표의 인덱스
    int type; // 분기점 안내 타입
    String instructions; // 경로 안내 문구
    //	이전 분기점의 경로 구성 좌표 인덱스로부터 해당 분기점의 경로 구성 좌표 인덱스까지의 거리(m)
    int distance;
    //	이전 분기점의 경로 구성 좌표 인덱스로부터 해당 분기점의 경로 구성 좌표 인덱스까지의 소요 시간(밀리초
    int duration;
}
