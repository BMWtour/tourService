package com.lion.BMWtour.service;


import com.lion.BMWtour.dto.TourInfoDto;
import org.springframework.data.domain.Page;


public interface TourInfoService {

    public static final int PAGE_SIZE = 5;

    // void tourInfoInsert(TourInfo tourInfo);

    Page<TourInfoDto> getPagedTourInfos(int page, String category, String address, String keyword, String sortField, String sortDirection);

}
