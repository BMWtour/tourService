package com.lion.BMWtour.controller;

import com.lion.BMWtour.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/initialize-categories")
    public ResponseEntity<String> initializeCategories() {
        categoryService.initializeCategories();
        return ResponseEntity.ok("Categories initialized successfully!");
    }
}