package com.lion.BMWtour.repository;

import com.lion.BMWtour.entitiy.TourInfo;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;


public interface TourInfoRepository extends ElasticsearchRepository<TourInfo, Long> {

}