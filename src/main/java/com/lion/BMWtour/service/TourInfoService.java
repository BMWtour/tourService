package com.lion.BMWtour.service;


import com.lion.BMWtour.entity.TourInfo;
import org.springframework.data.domain.Page;


public interface TourInfoService {
    void tourInfoInsert(TourInfo tourInfo);
    Page<TourInfo> getTourInfos(int page, String field, String query);
}
