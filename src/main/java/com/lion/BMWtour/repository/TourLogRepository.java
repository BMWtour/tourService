package com.lion.BMWtour.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.lion.BMWtour.entity.TourLog;

public interface TourLogRepository extends ElasticsearchRepository<TourLog, String> {
}
