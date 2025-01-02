package com.lion.BMWtour.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lion.BMWtour.dto.PointDto;
import com.lion.BMWtour.dto.PointNameDto;
import com.lion.BMWtour.dto.TourInfoDto;
import com.lion.BMWtour.entity.TourInfo;
import com.lion.BMWtour.service.TourInfoService;
import jakarta.servlet.http.HttpSession;
import com.lion.BMWtour.entity.TourInfo;
import com.lion.BMWtour.service.TourInfoServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;



@Controller
@RequestMapping("/tourinfo")
@RequiredArgsConstructor
public class TourInfoController {

    //naver map api client id
    @Value("${ncp.api.client-id}")
    String mapClientId;
    @Value("${ncp.api.client-secret}")
    String mapSecretKey;

    private final TourInfoService tourInfoService;


    @GetMapping("/list")
    public String list(@RequestParam(name="p", defaultValue = "1") int page,
                       @RequestParam(name="c", defaultValue = "") String category,
                       @RequestParam(name="a", defaultValue = "") String address,
                       @RequestParam(name="k", defaultValue = "") String keyword,
                       @RequestParam(name="sf", defaultValue = "") String sortField,
                       @RequestParam(name="sd", defaultValue = "") String sortDirection,
                       HttpSession session, Model model) throws JsonProcessingException {
        Page<TourInfoDto> pagedResult = tourInfoService.getPagedTourInfos(page, category, address, keyword, sortField, sortDirection);
        int totalPages = pagedResult.getTotalPages();
        int startPage = (int) Math.ceil((page - 0.5) / tourInfoService.PAGE_SIZE - 1) * tourInfoService.PAGE_SIZE + 1;
        int endPage = Math.min(startPage + tourInfoService.PAGE_SIZE - 1, totalPages);
        System.out.println("totalPages: " + totalPages);
        System.out.println("startPage: " + startPage);
        System.out.println("endPage: " + endPage);
        List<Integer> pageList = new ArrayList<>();
        List<PointNameDto> pointDtoList = new ArrayList<>();



        for (int i = startPage; i <= endPage; i++)
            pageList.add(i);
        for (TourInfoDto a : pagedResult.getContent()) { // pagedResult.getContent()를 반복
            PointNameDto pointNameDto = new PointNameDto(a.getTourInfo().getPoint().get(1), a.getTourInfo().getPoint().get(0), a.getTourInfo().getTitle()); // PointDto 객체 생성
            pointDtoList.add(pointNameDto); // 리스트에 추가
        }

        ObjectMapper objectMapper = new ObjectMapper();
        String pointDtoListJson = objectMapper.writeValueAsString(pointDtoList);


        session.setAttribute("menu", "elastic");
        session.setAttribute("currentTourinfoPage", page);
        model.addAttribute("TourInfoDtoList", pagedResult.getContent());
        model.addAttribute("pointDtoList", pointDtoList);
        model.addAttribute("page", page);
        model.addAttribute("category", category);
        model.addAttribute("address", address);
        model.addAttribute("keyword", keyword);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDirection", sortDirection);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        model.addAttribute("pageList", pageList);

        return "main/search";

    /**상세페이지 컨트롤러*/
    @GetMapping("/tour/detail/{tourId}")
    public String detail(
            @PathVariable String tourId,
            Model model
    ){
        //사용자 정보 이후 추가 필요

        TourInfo tourInfo = tourInfoService.getTourInfo(tourId);
        model.addAttribute("tourInfo", tourInfo);
        model.addAttribute("mapClientId", mapClientId);
        return "detail/detail";

    }



//    @GetMapping("/TourInfoInsert")
//    public String CulturalPlaceInsert() {
//        tourInfoService.tourInfoBulkInsert();
//        return "fragments/test";
//    }

//    /**상세페이지 테스트를 위한 컨트롤러*/
//    @GetMapping("/tour/detail/{tourId}")
//    public String detail(
//            @PathVariable String tourId,
//            Model model
//    ){
//        //사용자 정보 이후 추가 필요
//
//        TourInfo tourInfo = tourInfoService.getTourInfo(tourId);
//        model.addAttribute("tourInfo", tourInfo);
//        model.addAttribute("mapClientId", mapClientId);
//        return "detail/detail";
//    }


    @GetMapping("/search/{q}")
    public String search(@PathVariable String q, Model model) {
        System.out.println(q);
        model.addAttribute("test",q);
        return "main/search";
    }



}
