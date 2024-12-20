package com.lion.BMWtour.controller;

import com.lion.BMWtour.service.TourInfoService;
import com.lion.BMWtour.service.TourInfoServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
@RequiredArgsConstructor
public class TourInfoController {

    private final TourInfoServiceImpl tourInfoService;

    @GetMapping("/TourInfoInsert")
    public String CulturalPlaceInsert() {
        // tourInfoService.csvFileToElasticSearch();
        return "fragments/test";
    }
}
