package com.lion.BMWtour.service;


import java.util.List;

import com.lion.BMWtour.dto.SearchAutocompleteResponse;
import com.lion.BMWtour.dto.TourInfoDto;
import com.lion.BMWtour.entity.TourInfo;
import jakarta.servlet.http.HttpSession;
import org.springframework.data.domain.Page;
import org.springframework.ui.Model;


public interface TourInfoService {

    public static final int PAGE_SIZE = 5;

    // void tourInfoInsert(TourInfo tourInfo);

    Page<TourInfoDto> getPagedTourInfos(HttpSession session, int page, String category, String address, String keyword, String sortField, String sortDirection);
    TourInfo getTourInfo(String tourId);

    List<SearchAutocompleteResponse> getSearchAutocompleteList(String word);
}
