package com.lion.BMWtour.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "categories") // Elasticsearch 인덱스 이름
public class Category {

    @Id
    private String id; // 고유 ID
    private String name; // 카테고리 이름
    private List<String> subcategories; // 하위 카테고리 리스트
}
