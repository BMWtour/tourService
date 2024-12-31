package com.lion.BMWtour.service;

import com.lion.BMWtour.dto.*;
import com.lion.BMWtour.dto.direction.DirectionNcpResponse;
import com.lion.BMWtour.dto.direction.DirectionRoute;
import com.lion.BMWtour.dto.direction.GuideAndPointDto;
import com.lion.BMWtour.dto.direction.GuideDto;
import com.lion.BMWtour.dto.geocoding.GeoNcpResponse;
import com.lion.BMWtour.dto.geolocation.GeoLocationNcpResponse;
import com.lion.BMWtour.dto.rgeocoding.RGeoNcpResponse;
import com.lion.BMWtour.dto.rgeocoding.RGeoRegion;
import com.lion.BMWtour.dto.rgeocoding.RGeoResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class NaviService {

    private final NcpMapApiService mapApiService;
    private final NcpGeolocationService geolocationService;

    /**direction 5 정보 반환*/
    public DirectionNcpResponse getDirectionNcpResponse(NaviWithPointsDto dto){
        Map<String, Object> params = new HashMap<>();
        params.put("start", dto.getStart().toQueryValue());
        params.put("goal", dto.getGoal().toQueryValue());
        //direction5 api 요청
        DirectionNcpResponse response = mapApiService.direction5(params);
        log.info("response: {}", response);
        return response;
    }
    
    /**direction 5 이동경로를 반환 
     * DirectionNcpResponse의 정보를 이미 받은 상태
     * */
    public NaviRouteDto twoPointRoute2(Map<String, List<DirectionRoute>> route){
        //이동경로를 담을 리스트
        List<PointDto> path = new ArrayList<>();
        //이동경로를 dto에 넣어 반환
        route
            .get("traoptimal")//최적경로. 최상단
            .get(0)
            .getPath()
            .forEach(point ->
                    path.add(new PointDto(point.get(1), point.get(0)))
            );
        return new NaviRouteDto(path);
    }

    /**direction 5 이동경로 반환*/
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

        return new NaviRouteDto(path);
    }

    /**direction 5의 이동 경로 중 가이드 정보의 인덱스와 일치하는 좌표 return*/
    public List<PointDto> getGuidePointList(List<PointDto> path, List<GuideDto> guideDtoList){
        List<PointDto> pointDtoList = new ArrayList<>();
        int[] indices = new int[guideDtoList.size()];
        //좌표인덱스 쌓기
        for (int i = 0; i < guideDtoList.size(); i++) {
            indices[i] = guideDtoList.get(i).getPointIndex();
        }
        // 특정 인덱스 값들을 기반으로 추출
        for (int index : indices) {
            if (index >= 0 && index < path.size()) {
                pointDtoList.add(path.get(index));
            }
        }
        return pointDtoList;
    }
    
    /**direction 5의 이동 가이드 정보 + 가이드 좌표들 정보 return*/
    public GuideAndPointDto getGuideInfo(List<GuideDto> guideDtoList, List<PointDto> pointDtoList){
        return new GuideAndPointDto(guideDtoList,pointDtoList);
    }

    /**이동 가이드 정보 리스트 형태로 반환*/
    public List<GuideDto> getGuideDtoList(List<LinkedHashMap<String, Object>> guideList) {
        List<GuideDto> guideDtoList = new ArrayList<>();
        for (LinkedHashMap<String, Object> guide : guideList) {
            GuideDto guideDto = new GuideDto();
            guideDto.setPointIndex((Integer) guide.get("pointIndex"));
            guideDto.setType((Integer) guide.get("type"));
            guideDto.setInstructions((String) guide.get("instructions"));
            guideDto.setDistance((Integer) guide.get("distance"));
            guideDto.setDuration((Integer) guide.get("duration"));
            guideDtoList.add(guideDto);
        }
        return guideDtoList;
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
