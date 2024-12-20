package com.lion.BMWtour.repository;

import com.lion.BMWtour.entitiy.User;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface UserRepository extends ElasticsearchRepository<User, Long> {
}
