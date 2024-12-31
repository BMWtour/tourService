package com.lion.BMWtour.repository;

import com.lion.BMWtour.entity.Category;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends ElasticsearchRepository<Category, String> {
    // 기본적인 CRUD 메서드는 ElasticsearchRepository에서 제공
}
