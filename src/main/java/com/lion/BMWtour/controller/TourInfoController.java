package com.lion.BMWtour.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lion.BMWtour.dto.PointNameDto;
import com.lion.BMWtour.dto.TourInfoDto;
import com.lion.BMWtour.entity.TourInfo;
import com.lion.BMWtour.service.TourInfoService;
import com.lion.BMWtour.service.TourLogService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Controller
@RequiredArgsConstructor
public class TourInfoController {

    private final TourInfoService tourInfoService;
    private final TourLogService tourLogService;
    //naver map api client id
    @Value("${ncp.api.client-id}")
    String mapClientId;
    @Value("${ncp.api.client-secret}")
    String mapSecretKey;

    @GetMapping("/tour/list")
    public String list(@RequestParam(name = "p", defaultValue = "1") int page,
                       @RequestParam(name = "rc", defaultValue = "10") int researchCount,
                       @RequestParam(name = "c", defaultValue = "") String category,
                       @RequestParam(name = "a", defaultValue = "") String address,
                       @RequestParam(name = "k", defaultValue = "") String keyword,
                       @RequestParam(name = "sf", defaultValue = "") String sortField,
                       @RequestParam(name = "sd", defaultValue = "") String sortDirection,
                       HttpSession session, Model model) throws JsonProcessingException {
        // pagedResult, a> = tourInfoService.getPagedTourInfos(session,page, researchCount, category, address, keyword, sortField, sortDirection);

        Map<Page<TourInfoDto>, Integer> pagedResult = tourInfoService.getPagedTourInfos(session, page, researchCount, category, address, keyword, sortField, sortDirection);
        Page<TourInfoDto> pageContent = pagedResult.keySet().iterator().next(); // Page 객체 가져오기
        Integer pageSize = pagedResult.values().iterator().next();            // Integer 값 가져오기
        int totalPages = pageContent.getTotalPages() ;
        // int totalResults  = (totalPages * pageSize ) - (pageContent.getContent().size());
        int totalResults  = (totalPages * pageSize ) > 10000? 10000: (totalPages * pageSize );
        int startPage = (int) Math.ceil((page - 0.5) / tourInfoService.PAGE_SIZE - 1) * tourInfoService.PAGE_SIZE + 1;
        int endPage = Math.min(startPage + tourInfoService.PAGE_SIZE - 1, totalPages);
        List<Integer> pageList = new ArrayList<>();

        for (int i = startPage; i <= endPage; i++)
            pageList.add(i);
        System.out.println("pageContent.getContent().size(): " + pageContent.getContent().size());
        System.out.println("totalPages: " + totalPages);
        System.out.println("startPage: " + startPage);
        System.out.println("endPage: " + endPage);
        System.out.println("pageList: " + pageList);
        System.out.println("pageSize: " + pageSize);


        List<PointNameDto> pointDtoList = new ArrayList<>();
        for (TourInfoDto a : pageContent.getContent()) { // pagedResult.getContent()를 반복
            PointNameDto pointNameDto = new PointNameDto(a.getTourInfo().getPoint().get(1), a.getTourInfo().getPoint().get(0), a.getTourInfo().getTitle()); // PointDto 객체 생성
            pointDtoList.add(pointNameDto); // 리스트에 추가
        }

        ObjectMapper objectMapper = new ObjectMapper();
        String pointDtoListJson = objectMapper.writeValueAsString(pointDtoList);


        session.setAttribute("menu", "elastic");
        session.setAttribute("currentTourinfoPage", page);
        model.addAttribute("TourInfoDtoList", pageContent.getContent());
        model.addAttribute("pointDtoList", pointDtoListJson);
        model.addAttribute("page", page);
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("category", category);
        model.addAttribute("address", address);
        model.addAttribute("keyword", keyword);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDirection", sortDirection);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("totalResults", totalResults);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        model.addAttribute("pageList", pageList);

        return "main/search";
    }

    /**
     * 상세페이지 컨트롤러
     **/
    @GetMapping("/tour/detail/{tourId}")
    public String detail(
            @PathVariable String tourId,
            Model model
    ) {
        //사용자 정보 이후 추가 필요
        System.out.println("tourId: " + tourId);
        TourInfo tourInfo = tourInfoService.getTourInfo(tourId);
        //추천 관광지 추가
        List<TourInfo> recommendations = tourInfoService.getRecommendations(tourId); // 추천 관광지 추가
        //추천 관광지에서 현재 관광지 정보가 존재한다면 remove
//        if (recommendations.contains(tourInfo)) {
//            recommendations.remove(tourInfo);
//        }

        tourLogService.saveTourLog(tourInfo);
        model.addAttribute("tourInfo", tourInfo);
        model.addAttribute("mapClientId", mapClientId);
        model.addAttribute("recommendations", recommendations);
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
        model.addAttribute("test", q);
        return "main/search";
    }


}
