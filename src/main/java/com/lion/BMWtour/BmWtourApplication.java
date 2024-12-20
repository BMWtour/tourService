package com.lion.BMWtour;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

@SpringBootApplication // 엔티티 클래스 경로 설정
public class BMWtourApplication {

	public static void main(String[] args) {
		SpringApplication.run(BMWtourApplication.class, args);
	}

}
