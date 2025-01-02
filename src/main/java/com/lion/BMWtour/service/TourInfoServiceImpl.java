package com.lion.BMWtour.service;

import com.lion.BMWtour.dto.TourInfoDto;
import com.lion.BMWtour.entity.TourInfo;
import com.lion.BMWtour.dto.PointDto;
import com.lion.BMWtour.entity.TourInfo;
import com.lion.BMWtour.repository.TourInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.core.query.StringQuery;
import org.springframework.stereotype.Service;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.core.query.StringQuery;
import org.springframework.stereotype.Service;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.List;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TourInfoServiceImpl implements TourInfoService {


    private final ElasticsearchTemplate elasticsearchTemplate;

    @Override
    public PageImpl<TourInfoDto> getPagedTourInfos(int page, String category, String region, String keyword, String sortField, String sortDirection) {
        Pageable pageable = PageRequest.of(page - 1, TourInfoService.PAGE_SIZE);
        ;
        Sort.Direction direction = sortDirection.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Query query = NativeQuery.builder()
                .withQuery(buildMatchQuery(category, region, keyword))
                .withSort(Sort.by(Sort.Order.desc("_score")))       // 1. matchScore 기준 정렬
                .withSort(Sort.by(direction, sortField))     // 2. titel/author.keyword 기준 정렬
                .withTrackScores(true)
                .withPageable(pageable)
                .build();
        SearchHits<TourInfo> searchHits = elasticsearchTemplate.search(query, TourInfo.class);
        List<TourInfoDto> TourInfoDtoList = searchHits
                .getSearchHits()
                .stream()
                .map(hit -> new TourInfoDto(hit.getContent(), hit.getScore()))
                .toList();

        long totalHits = searchHits.getTotalHits();
        return new PageImpl<>(TourInfoDtoList, pageable, totalHits);
    }


    // 조건에 따라 match 또는 match_all을 반환하는 함수
    private String createMatchCondition(String field, String value, String type) {
        switch (type) {
            case "keyword":
                return value.isEmpty() ? "{ \"match_all\": {} }" : String.format("{ \"term\": { \"%s\": { \"value\": \"%s\" } } }", field, value);
            case "text":
                return value.isEmpty() ? "{ \"match_all\": {} }" : String.format("{ \"match\": { \"%s\": { \"query\": \"%s\" } } }", field, value);


//    public Page<TourInfo> getPagedTourInfos(int page, String field, String keyword, String sortField, String sortDirection) {
//        Pageable pageable = PageRequest.of(page - 1, PAGE_SIZE);
//        // 정렬 필드에 keyword 서브 필드 사용
//        String sortFieldToUse = sortField + ".keyword";
//        Sort.Direction direction = sortDirection.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
//        Query query = NativeQuery.builder()
//                .withQuery(buildMatchQuery(field, keyword))
//                .withSort(Sort.by(Sort.Order.desc("_score")))       // 1. matchScore 기준 정렬
//                .withSort(Sort.by(direction, sortFieldToUse))     // 2. titel/author.keyword 기준 정렬
//                .withTrackScores(true)
//                .withPageable(pageable)
//                .build();
//        SearchHits<TourInfo> searchHits = elasticsearchTemplate.search(query, TourInfo.class);
//        List<TourInfoDto> TourInfoDtoList = searchHits
//                .getSearchHits()
//                .stream()
//                .map(hit -> new TourInfoDto(hit.getContent(), hit.getScore()))
//                .toList();

    /// / curl --location --request GET "http://104.198.205.64:9200/tourinfos/_search?q=경기도%20시흥시%20피울길%20167"
//        long totalHits = searchHits.getTotalHits();
//        return new PageImpl<>(TourInfoDtoList, pageable, totalHits);
//    }
    private Query buildMatchQuery(String field, String keyword) {
        if (keyword.isEmpty()) {
            return new StringQuery("{\"match_all\": {}}");
        }
        return "";
    }

    private Query buildMatchQuery(String category, String address, String keyword) {
        category = createMatchCondition("category", category, "keyword");
        address = createMatchCondition("address", address, "text");
        keyword = createMatchCondition("summary", keyword, "text");

        String queryString = String.format("""
                        {
                                "bool": {
                                    "must": [
                                        %s,
                                        %s,
                                        %s
                                    ]
                                }
                            }
                        """,
                category,
                address,
                keyword
        );
        // System.out.println("TourInfoServiceImpl - queryString: " + queryString);
        return new StringQuery(queryString);
    }
}

      
    }
    
    //관광지 정보 하나 가져오기
    public TourInfo getTourInfo(String tourId) {
        TourInfo tourInfo = tourInfoRepository.findById(tourId).orElse(null);
        return tourInfo;
    }

