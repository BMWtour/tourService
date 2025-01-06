package com.lion.BMWtour.service;


import java.util.List;

import com.lion.BMWtour.dto.SearchAutocompleteResponse;
import com.lion.BMWtour.dto.TourInfoDto;
import com.lion.BMWtour.entity.TourInfo;
import jakarta.servlet.http.HttpSession;
import org.springframework.data.domain.Page;
import org.springframework.ui.Model;


public interface TourInfoService {

    public static final int PAGE_SIZE = 10;

    // void tourInfoBulkInsert();

    Page<TourInfoDto> getPagedTourInfos(HttpSession session, int page, String category, String address, String keyword, String sortField, String sortDirection);
    TourInfo getTourInfo(String tourId);

    List<SearchAutocompleteResponse> getSearchAutocompleteList(String word);

    //상세페이지 추천관광지
    List<TourInfo> getRecommendations(String tourId);
}
