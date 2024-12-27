package com.lion.BMWtour.controller;

import com.lion.BMWtour.service.TourInfoServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@Controller
@RequiredArgsConstructor
public class TourInfoController {

    //naver map api client id
    @Value("${ncp.api.client-id}")
    String mapClientId;
    @Value("${ncp.api.client-secret}")
    String mapSecretKey;

    private final TourInfoServiceImpl tourInfoService;

    @GetMapping("/TourInfoInsert")
    public String CulturalPlaceInsert() {
        // tourInfoService.csvFileToElasticSearch();
        return "fragments/test";
    }

    /**상세페이지 테스트를 위한 컨트롤러*/
    @GetMapping("/tour/detail")
    public String detail(
            Model model
    ){
        model.addAttribute("mapClientId", mapClientId);
        return "detail/detail";
    }

    @GetMapping("/search/{q}")
    public String search(@PathVariable String q, Model model) {
        System.out.println(q);
        model.addAttribute("test",q);
        return "main/search";
    }

}
