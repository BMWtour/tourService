package com.lion.BMWtour.repository;

import com.lion.BMWtour.entity.User;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.Optional;

public interface UserRepository extends ElasticsearchRepository<User, String> {
    Optional<User> findByUserId(String userId);
    Optional<User> findByUserNickname(String userNickname); // 닉네임 중복 체크
}