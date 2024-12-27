package com.lion.BMWtour.controller;

import com.lion.BMWtour.service.TourInfoServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestClient;
import org.yaml.snakeyaml.util.UriEncoder;
import java.net.URLEncoder;
import org.springframework.web.bind.annotation.PathVariable;


@Controller
@RequiredArgsConstructor
public class TourInfoController {

    private final TourInfoServiceImpl tourInfoService;
    //naver map api client id
    @Value("${ncp.api.client-id}")
    String mapClientId;
    @Value("${ncp.api.client-secret}")
    String mapSecretKey;
    @Value("${odsay.map.secret-key}")
    String odsaySecretKey;

    @GetMapping("/TourInfoInsert")
    public String CulturalPlaceInsert() {
        tourInfoService.tourInfoInsert();
        return "fragments/test";
    }

    /**상세페이지 테스트를 위한 컨트롤러*/
    @GetMapping("/tour/detail")
    public String detail(
            Model model
    ){
        model.addAttribute("mapClientId", mapClientId);
        model.addAttribute("odsaySecretKey", odsaySecretKey);
        return "detail/detail";
    }

    @GetMapping("/search/{q}")
    public String search(@PathVariable String q, Model model) {
        System.out.println(q);
        model.addAttribute("test",q);
        return "fragments/test";
    }

}

