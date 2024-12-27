package com.lion.BMWtour.controller;

import com.lion.BMWtour.service.TourInfoServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@Controller
@RequiredArgsConstructor
public class TourInfoController {

    private final TourInfoServiceImpl tourInfoService;

    @GetMapping("/TourInfoInsert")
    public String CulturalPlaceInsert() {
        tourInfoService.tourInfoInsert();
        return "fragments/test";
    }

    @GetMapping("/search/{q}")
    public String search(@PathVariable String q, Model model) {
        System.out.println(q);
        model.addAttribute("test",q);
        return "main/search";
    }

}
