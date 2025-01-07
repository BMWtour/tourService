package com.lion.BMWtour.service;

import java.util.List;

import org.springframework.data.domain.Page;

import com.lion.BMWtour.dto.TourInfoDto;
import com.lion.BMWtour.entity.TourInfo;

import jakarta.servlet.http.HttpSession;


public interface TourInfoService {

    public static final int PAGE_SIZE = 10;

    // void tourInfoBulkInsert();

    Page<TourInfoDto> getPagedTourInfos(HttpSession session, int page, String category, String address, String keyword, String sortField, String sortDirection);
    TourInfo getTourInfo(String tourId);

    //상세페이지 추천관광지
    List<TourInfo> getRecommendations(String tourId);

    List<String> getSearchAutocompleteList(String word);
}
