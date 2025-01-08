package com.lion.BMWtour.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lion.BMWtour.dto.main.NearbyLocationResponse;
import com.lion.BMWtour.service.MainService;
import com.lion.BMWtour.service.TourInfoService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class TourInfoRestController {

	private final MainService mainService;
	private final TourInfoService tourInfoService;

	@GetMapping("/tour/nearby")
	public ResponseEntity<List<NearbyLocationResponse>> getNearbyLocation(
		@RequestParam(value = "latitude") double latitude,
		@RequestParam(value = "longitude") double longitude, HttpSession session) {
		session.setAttribute("latitude", latitude);
		session.setAttribute("longitude", longitude);
		List<NearbyLocationResponse> nearbyLocationList = mainService.getNearbyLocationList(latitude, longitude);
		return ResponseEntity.ok(nearbyLocationList);
	}

	@GetMapping("/tour/autocomplete")
	public ResponseEntity<List<String>> searchAutocompleteList(@RequestParam(value = "word") String word) {
		List<String> searchAutocompleteList = tourInfoService.getSearchAutocompleteList(word);
		return ResponseEntity.ok(searchAutocompleteList);
	}
}
