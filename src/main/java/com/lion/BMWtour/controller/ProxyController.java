package com.lion.BMWtour.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/api/proxy")
public class ProxyController {

    @GetMapping("/categories")
    public ResponseEntity<String> proxyElasticsearch() {
        RestTemplate restTemplate = new RestTemplate();
        String url = "http://104.198.205.64:9200/categories/_search?pretty";
        String response = restTemplate.getForObject(url, String.class);
        return ResponseEntity.ok(response);
    }
}