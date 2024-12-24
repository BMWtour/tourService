package com.lion.BMWtour.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

	@GetMapping("/tour/main")
	public String mainPage() {
		return "/main/main";
	}
}
