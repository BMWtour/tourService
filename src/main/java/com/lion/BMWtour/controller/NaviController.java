package com.lion.BMWtour.controller;

import com.lion.BMWtour.dto.*;
import com.lion.BMWtour.dto.direction.DirectionNcpResponse;
import com.lion.BMWtour.dto.direction.GuideAndPointDto;
import com.lion.BMWtour.dto.direction.GuideAndRouteDto;
import com.lion.BMWtour.dto.direction.GuideDto;
import com.lion.BMWtour.dto.rgeocoding.RGeoResponseDto;
import com.lion.BMWtour.service.NaviService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**naver map 관련 api 호출 컨트롤러*/
@Slf4j
@RestController
@RequestMapping("/navigate")
@RequiredArgsConstructor
public class NaviController {

    private final NaviService service;

    /**두 좌표정보를 받아 가이드정보(좌표포함)와 이동경로 좌표들을 return*/
    @PostMapping("/getGuideAndRouteInfo")
    public GuideAndRouteDto getPointsAndGuideInfo(
            @RequestBody
            NaviWithPointsDto dto
    ) {
        DirectionNcpResponse response = service.getDirectionNcpResponse(dto);
        //이동경로
        NaviRouteDto naviRouteDto = service.twoPointRoute2(response.getRoute());
        //이동 가이드 정보
        List<LinkedHashMap<String, Object>> guideList = response.getRoute().get("traoptimal").get(0).getGuide();
        List<GuideDto> guideDtoList = service.getGuideDtoList(guideList);
        //이동 경로 중 가이드 정보의 인덱스와 일치하는 좌표 정보
        List<PointDto> guidePointDtoList = service.getGuidePointList(naviRouteDto.getPath(), guideDtoList);
        //이동 가이드 정보 + 가이드 좌표들 정보
        GuideAndPointDto guideAndPointDto = service.getGuideInfo(guideDtoList, guidePointDtoList);
        //최종반환 DTO
        GuideAndRouteDto guideAndRouteDto = new GuideAndRouteDto(guideAndPointDto, naviRouteDto);
        return guideAndRouteDto;
    }

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
