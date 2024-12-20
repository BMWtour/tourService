package com.lion.BMWtour.controller;

import com.lion.BMWtour.service.TourInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
@RequiredArgsConstructor
public class TourInfoController {

    private final TourInfoService tourInfoService;

    @GetMapping("/TourInfoInsert")
    public String CulturalPlaceInsert() {
        // tourInfoService.csvFileToElasticSearch();
        return "fragments/test";
    }
}
