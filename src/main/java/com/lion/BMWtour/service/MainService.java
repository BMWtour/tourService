package com.lion.BMWtour.service;

import java.util.List;

import com.lion.BMWtour.dto.main.NearbyLocationResponse;

public interface MainService {

	List<NearbyLocationResponse> getNearbyLocationList(Double latitude, Double longitude);
}
