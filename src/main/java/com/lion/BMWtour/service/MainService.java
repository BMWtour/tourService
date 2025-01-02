package com.lion.BMWtour.service;

import java.util.List;

import com.lion.BMWtour.dto.main.NearbyLocationResponse;
import com.lion.BMWtour.dto.main.PopularRegionsResponse;

public interface MainService {

	List<PopularRegionsResponse> getPopularRegionsList();

	List<NearbyLocationResponse> getNearbyLocationList(Double latitude, Double longitude);
}
