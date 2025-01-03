package com.lion.BMWtour.service;


import com.lion.BMWtour.dto.TourInfoDto;
import com.lion.BMWtour.entity.TourInfo;
import org.springframework.data.domain.Page;

import java.util.List;


public interface TourInfoService {

    public static final int PAGE_SIZE = 5;

    // void tourInfoInsert(TourInfo tourInfo);

    Page<TourInfoDto> getPagedTourInfos(int page, String category, String address, String keyword, String sortField, String sortDirection);
    TourInfo getTourInfo(String tourId);
    //상세페이지 추천관광지
    List<TourInfo> getRecommendations(String tourId);
}
