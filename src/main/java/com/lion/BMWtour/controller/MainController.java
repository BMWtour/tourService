package com.lion.BMWtour.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.lion.BMWtour.dto.main.NearbyLocationResponse;
import com.lion.BMWtour.service.MainService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class MainController {

	private final MainService mainService;

	@GetMapping("/tour/main")
	public String mainPage() {
		return "/main/main";
	}

	@GetMapping("/tour/nearby")
	@ResponseBody
	public ResponseEntity<List<NearbyLocationResponse>> getNearbyLocation(@RequestParam(value = "latitude") double latitude,
		@RequestParam(value = "longitude") double longitude) {
		List<NearbyLocationResponse> nearbyLocationList = mainService.getNearbyLocationList(latitude, longitude);
		return ResponseEntity.ok(nearbyLocationList);
	}
}
