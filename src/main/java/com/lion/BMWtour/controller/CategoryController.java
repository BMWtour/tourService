package com.lion.BMWtour.controller;

import com.lion.BMWtour.entity.Category;
import com.lion.BMWtour.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

//    @GetMapping("/initialize-categories")
//    public ResponseEntity<String> initializeCategories() {
//        categoryService.initializeCategories();
//        return ResponseEntity.ok("Categories initialized successfully!");
//    }
    @GetMapping("/api/getCategory")
    public ResponseEntity<List> getCategories() {
        List<Category> categories = categoryService.getCategories();
        return ResponseEntity.ok(categories); // JSON 형식으로 반환
    }


}