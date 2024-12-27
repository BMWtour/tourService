package com.lion.BMWtour.service;

import com.lion.BMWtour.dto.*;
import com.lion.BMWtour.dto.direction.DirectionNcpResponse;
import com.lion.BMWtour.dto.geocoding.GeoNcpResponse;
import com.lion.BMWtour.dto.geolocation.GeoLocationNcpResponse;
import com.lion.BMWtour.dto.rgeocoding.RGeoNcpResponse;
import com.lion.BMWtour.dto.rgeocoding.RGeoRegion;
import com.lion.BMWtour.dto.rgeocoding.RGeoResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class NaviService {

    private final NcpMapApiService mapApiService;
    private final NcpGeolocationService geolocationService;

    public NaviRouteDto twoPointRoute(NaviWithPointsDto dto){
        Map<String, Object> params = new HashMap<>();
        params.put("start", dto.getStart().toQueryValue());
        params.put("goal", dto.getGoal().toQueryValue());
        //direction5 api 요청
        DirectionNcpResponse response = mapApiService.direction5(params);
        log.info("response: {}", response);
        //이동경로를 담을 리스트
        List<PointDto> path = new ArrayList<>();
        //이동경로를 dto에 넣어 반환
        response.getRoute()
                .get("traoptimal")//최적경로. 최상단
                .get(0)
                .getPath()
                .forEach(point ->
                        path.add(new PointDto(point.get(1), point.get(0)))
                );
        //이동 상세 텍스트 받아오려면 추가작업이 필요함
        //NaviRouteDto에 변수하나 추가해서? List<String>?
        return new NaviRouteDto(path);
    }

    public RGeoResponseDto getAddress(PointDto pointDto){
        Map<String, Object> params = new HashMap<>();
        params.put("coords", pointDto.toQueryValue());
        params.put("output", "json");
        RGeoNcpResponse response = mapApiService.reverseGeocode(params);
        log.info(response.toString());
        RGeoRegion region = response
                .getResults()
                .get(0).
                getRegion();

        String address = region.getArea1().getName() + " " +
                region.getArea2().getName() + " " +
                region.getArea3().getName() + " " +
                region.getArea4().getName();
        //빈칸 자르기
        return new RGeoResponseDto(address.trim());

    }
    //중심점에서 주소까지(가장 근접한 결과로)
    //시작좌표는 구해져있음.
    // 찾는 곳 input에 적은 주소로 들어온 좌표를 빼내어 활용하기
    public NaviRouteDto startQuery(NaviWithQueryDto dto){
        // 주소의 좌표부터 찾기
        Map<String, Object> params = new HashMap<>();
        params.put("query", dto.getQuery());
        params.put("coordinate", dto.getStart().toQueryValue());
        params.put("page", 1);
        params.put("count", 1);
        GeoNcpResponse response = mapApiService.geocode(params);
        log.info(response.toString());
        Double lat = Double.valueOf(response.getAddresses().get(0).getY());
        Double lng = Double.valueOf(response.getAddresses().get(0).getX());
        PointDto goal = new PointDto(lat,lng);
        //경로를 찾아서 반환하기
        return this.twoPointRoute(new NaviWithPointsDto(
                dto.getStart(),
                goal
        ));
    }
    //두 ip를 받아 경로 리턴
    public NaviRouteDto withIpAddresses(NaviWithIpsDto dto) {
        Map<String, Object> params = new HashMap<>();
        //시작 ip
        params.put("ip", dto.getStartIp());
        params.put("responseFormatType", "json");
        params.put("ext", "t");
        GeoLocationNcpResponse startInfo
                = geolocationService.geoLocation(params);
        log.info(startInfo.toString());
        //도착 ip
        params.put("ip", dto.getGoalIp());
        GeoLocationNcpResponse goalInfo
                = geolocationService.geoLocation(params);
        log.info(goalInfo.toString());

        PointDto start = new PointDto(
                startInfo.getGeoLocation().getLat(),
                startInfo.getGeoLocation().getLng()
        );
        PointDto goal = new PointDto(
                goalInfo.getGeoLocation().getLat(),
                goalInfo.getGeoLocation().getLng()
        );

        return twoPointRoute(new NaviWithPointsDto(start, goal));
    }

    //주소를 받아 좌표를 리턴
    public PointDto addressToPoints(AddressDto dto){
        // 사용자가 입력한 주소의 좌표부터 찾기
        Map<String, Object> params = new HashMap<>();
        params.put("query", dto.getAddress());
        params.put("page", 1);
        params.put("count", 1);

        GeoNcpResponse response = mapApiService.geocode(params);
        log.info(response.toString());
        Double lat = Double.valueOf(response.getAddresses().get(0).getY());
        Double lng = Double.valueOf(response.getAddresses().get(0).getX());
        PointDto goal = new PointDto(lat,lng);
        //경로를 찾아서 반환하기
        return goal;
    }
}
