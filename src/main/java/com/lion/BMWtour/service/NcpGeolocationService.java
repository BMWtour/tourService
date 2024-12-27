package com.lion.BMWtour.service;

import com.lion.BMWtour.dto.geolocation.GeoLocationNcpResponse;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;

import java.util.Map;

public interface NcpGeolocationService {
    @GetExchange
    GeoLocationNcpResponse geoLocation(
            @RequestParam
            Map<String, Object> params
    );
}
