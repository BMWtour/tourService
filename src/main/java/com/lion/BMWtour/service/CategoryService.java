package com.lion.BMWtour.service;

import com.lion.BMWtour.entity.Category;
import com.lion.BMWtour.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    public void initializeCategories() {
        // 카테고리 데이터 생성
        List<Category> categories = Arrays.asList(
                new Category(null, "관광지", Arrays.asList("자연관광지", "역사관광지", "체험관광지")),
                new Category(null, "문화시설", Arrays.asList("박물관", "기념관", "전시관", "미술관", "공연장", "문화원", "도서관")),
                new Category(null, "축제공연행사", Arrays.asList("축제", "공연행사")),
                new Category(null, "레포츠", Arrays.asList("육상레포츠", "수상레포츠", "항공레포츠", "캠핑")),
                new Category(null, "쇼핑", Arrays.asList("시장", "백화점", "특산물")),
                new Category(null, "음식점", Arrays.asList("한식", "서양식", "일식", "중식", "카페"))
        );

        // 데이터 저장
        categoryRepository.saveAll(categories);
    }
}
