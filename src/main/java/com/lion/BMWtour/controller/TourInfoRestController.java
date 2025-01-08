package com.lion.BMWtour.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lion.BMWtour.dto.PointNameDto;
import com.lion.BMWtour.dto.TourInfoDto;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
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




//	@PostMapping("/tour/list")
//	public String  list(@RequestParam(name="p", defaultValue = "1") int page,
//					   @RequestParam(name="rc", defaultValue = "10") int researchCount,
//					   @RequestParam(name="c", defaultValue = "") String category,
//					   @RequestParam(name="a", defaultValue = "") String address,
//					   @RequestParam(name="k", defaultValue = "") String keyword,
//					   @RequestParam(name="sf", defaultValue = "") String sortField,
//					   @RequestParam(name="sd", defaultValue = "") String sortDirection,
//					   HttpSession session, Model model) throws JsonProcessingException {
//		Page<TourInfoDto> pagedResult = tourInfoService.getPagedTourInfos(session,page, researchCount, category, address, keyword, sortField, sortDirection);
//		int totalPages = pagedResult.getTotalPages();
//		int startPage = (int) Math.ceil((page - 0.5) / tourInfoService.PAGE_SIZE - 1) * tourInfoService.PAGE_SIZE + 1;
//		int endPage = Math.min(startPage + tourInfoService.PAGE_SIZE - 1, totalPages);
//		List<Integer> pageList = new ArrayList<>();
//		for (int i = startPage; i <= endPage; i++)
//			pageList.add(i);
//		System.out.println("totalPages: " + totalPages);
//		System.out.println("startPage: " + startPage);
//		System.out.println("endPage: " + endPage);
//		System.out.println("pageList: " + pageList);
//
//		List<PointNameDto> pointDtoList = new ArrayList<>();
//		for (TourInfoDto a : pagedResult.getContent()) { // pagedResult.getContent()를 반복
//			PointNameDto pointNameDto = new PointNameDto(a.getTourInfo().getPoint().get(1), a.getTourInfo().getPoint().get(0), a.getTourInfo().getTitle()); // PointDto 객체 생성
//			pointDtoList.add(pointNameDto); // 리스트에 추가
//		}
//
//		ObjectMapper objectMapper = new ObjectMapper();
//		String pointDtoListJson = objectMapper.writeValueAsString(pointDtoList);
//
//		session.setAttribute("currentTourinfoPage", page);
//		model.addAttribute("TourInfoDtoList", pagedResult.getContent());
//		model.addAttribute("pointDtoList", pointDtoListJson);
//		model.addAttribute("page", page);
//		model.addAttribute("category", category);
//		model.addAttribute("address", address);
//		model.addAttribute("keyword", keyword);
//		model.addAttribute("sortField", sortField);
//		model.addAttribute("sortDirection", sortDirection);
//		model.addAttribute("totalPages", totalPages);
//		model.addAttribute("startPage", startPage);
//		model.addAttribute("endPage", endPage);
//		model.addAttribute("pageList", pageList);
//		return "main/search";
//	}



}
