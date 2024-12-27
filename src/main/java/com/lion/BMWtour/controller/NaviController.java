package com.lion.BMWtour.controller;

import com.lion.BMWtour.dto.*;
import com.lion.BMWtour.dto.rgeocoding.RGeoResponseDto;
import com.lion.BMWtour.service.NaviService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**naver map 관련 api 호출 컨트롤러*/
@Slf4j
@RestController
@RequestMapping("/navigate")
@RequiredArgsConstructor
public class NaviController {

    private final NaviService service;

    // 두 좌표를 받아 이동경로를 반환하는 메서드
    // 두 점 경로 구하기
    //directions 5
    @PostMapping("/points")
    public NaviRouteDto withPoints(
            @RequestBody
            NaviWithPointsDto dto
    ) {
        return service.twoPointRoute(dto);
    }

    // 하나의 좌표를 입력받아, 주소를 반환하는 메서드
    // eversegeocode
    // 지도에서 멈춘 곳의 좌표를 front에서 받음
    // 중심점 주소
    //geocoding
    @PostMapping("/get-address")
    public RGeoResponseDto getAddress(
            @RequestBody
            PointDto point
    ) {
        return service.getAddress(point);
    }

    // 하나의 좌표와 주소를 입력받아, 좌표에서
    // 주소검색 결과 위치로의 이동경로를 반환하는 메서드
    //중심점에서 주소까지(가장 근접한 결과로)
    //중심점 좌표에서 - 내가 입력한 주소까지의 이동경로 반환


    //NaviWithQueryDto의 query를 geocode로 상세주소와 좌표를 알아낸다.
    //NaviWithQueryDto의 start와 geocode로 받아온 좌표로 direction5 실행
    //NaviRouteDto로 받아 return
    @PostMapping("/start-query")
    public NaviRouteDto withQuery(
            @RequestBody
            NaviWithQueryDto dto
    ) {
        return service.startQuery(dto);
    }

    // 두 IP 주소를 입력받아 이동경로를 반환하는 메서드
    //시작 IP - 도착 IP 경로 구하기
    @PostMapping("/ips")
    public NaviRouteDto withIpAddresses(
            @RequestBody
            NaviWithIpsDto dto
    ) {
        return service.withIpAddresses(dto);
    }

    //하나의 주소를 받아 좌표로 반환`
    @PostMapping("/getPoints")
    public PointDto getPoints(
            @RequestBody
            AddressDto dto
    ){
        return service.addressToPoints(dto);
    }
}
