package com.lion.BMWtour.service;


import com.lion.BMWtour.entitiy.TourInfo;
import org.springframework.data.domain.Page;

import java.awt.print.Book;


public interface TourInfoService {
    void tourInfoInsert(TourInfo tourInfo);
    Page<TourInfo> getTourInfos(int page, String field, String query);
}
