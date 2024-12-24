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


@Controller
@RequiredArgsConstructor
public class TourInfoController {

    private final TourInfoServiceImpl tourInfoService;
    //naver map api client id
    @Value("${naver.map.client-id}")
    String mapClientId;
    @Value("${naver.map.secret-key}")
    String mapSecretKey;
    @Value("${odsay.map.secret-key}")
    String odsaySecretKey;

    @GetMapping("/TourInfoInsert")
    public String CulturalPlaceInsert() {
        // tourInfoService.csvFileToElasticSearch();
        return "fragments/test";
    }

    /**상세페이지 테스트를 위한 컨트롤러
     * html 페이지 확인을 위한 임시 컨트롤러
     * 추후 삭제 예정
     */
    @GetMapping("/tour/detail")
    public String detail(
            Model model
    ){

        model.addAttribute("mapClientId", mapClientId);
        model.addAttribute("odsaySecretKey", odsaySecretKey);
        return "detail/detail";
    }

    /*odsay api 테스트용 컨트롤러
    * 추후삭제예정*/
    @GetMapping("/tour/findMapTest")
    @ResponseBody
    public ResponseEntity<String> findMapTest(){
        odsaySecretKey = URLEncoder.encode(odsaySecretKey);
        RestClient restClient = RestClient.builder()
                .baseUrl("https://api.odsay.com/v1/api")
                .build();
//https://api.odsay.com/v1/api/searchPubTransPathT?SX=126.972317&SY=37.556103&EX=129.334068&EY=35.822547&apiKey=LGGyP%2BCBYgb9Zt%2BplYLTNRBIUJoGdoJmpwJgdsIs%2Fu4
        //https://api.odsay.com/v1/api/searchPubTransPathT?lang=0&SX=129.338408&SY=35.842406&EX=126.972295&EY=37.556086&OPT=0&SearchType=0
        ResponseEntity<String> response = restClient.get()
                .uri(uriBuilder -> uriBuilder.path("/searchPubTransPathT")
                        .queryParam("lang", 0)
                        .queryParam("SX", 126.9931257)
                        .queryParam("SY", 37.5680965)
                        .queryParam("EX", 126.8294568287)
                        .queryParam("EY", 37.3682496114)
                        .queryParam("OPT", 0)
                        .queryParam("SearchType", 0)
                        .queryParam("apiKey",  odsaySecretKey)
                        .build())
                .retrieve()
                .toEntity(String.class);
       ;
        return response.getBody() == null ? ResponseEntity.noContent().build() : response;
    }
}
