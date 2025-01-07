package com.lion.BMWtour.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lion.BMWtour.service.TourInfoService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class TourInfoRestController {

	private final TourInfoService tourInfoService;

	@GetMapping("/tour/autocomplete")
	public ResponseEntity<List<String>> searchAutocompleteList(@RequestParam(value = "word") String word) {
		List<String> searchAutocompleteList = tourInfoService.getSearchAutocompleteList(word);
		return ResponseEntity.ok(searchAutocompleteList);
	}
}
