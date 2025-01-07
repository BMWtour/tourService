package com.lion.BMWtour.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.lion.BMWtour.dto.main.PopularByCategoryResponse;
import com.lion.BMWtour.dto.main.PopularRegionsResponse;
import com.lion.BMWtour.service.MainService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class MainController {

	private final MainService mainService;

	@GetMapping("/tour/main")
	public String mainPage(Model model) {
		List<PopularRegionsResponse> popularRegionsList = mainService.getPopularRegionsList();
		List<PopularByCategoryResponse> popularByCategoryList = mainService.getPopularByCategoryList();

		model.addAttribute("popularRegions", popularRegionsList);
		model.addAttribute("popularByCategory", popularByCategoryList);
		return "main/main";
	}


}
