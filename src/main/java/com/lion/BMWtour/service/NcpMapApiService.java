package com.lion.BMWtour.service;

import com.lion.BMWtour.dto.direction.DirectionNcpResponse;
import com.lion.BMWtour.dto.geocoding.GeoNcpResponse;
import com.lion.BMWtour.dto.rgeocoding.RGeoNcpResponse;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;

import java.util.Map;

public interface NcpMapApiService {
    //Direction 5
    @GetExchange("/map-direction/v1/driving")
    DirectionNcpResponse direction5(
            @RequestParam
            Map<String, Object> params
    );
    //Geocoding
    //검색어를 입력하면 상세한 주소와 좌표를 돌려준다.
    @GetExchange("/map-geocode/v2/geocode")
    GeoNcpResponse geocode(
            @RequestParam
            Map<String, Object> params
    );
    //Reverse Geocoding
    //입력한 좌표 값을 주소 정보(법정동, 행정동, 지번 주소, 도로명 주소)로 변환
    @GetExchange("/map-reversegeocode/v2/gc")
    RGeoNcpResponse reverseGeocode(
            @RequestParam
            Map<String, Object> params
    );
}
