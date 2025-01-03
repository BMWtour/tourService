package com.lion.BMWtour.service;

import com.lion.BMWtour.dto.TourInfoDto;
import com.lion.BMWtour.entity.TourInfo;
import com.lion.BMWtour.repository.TourInfoRepository;
import lombok.RequiredArgsConstructor;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
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

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TourInfoServiceImpl implements TourInfoService {


    private final ElasticsearchTemplate elasticsearchTemplate;
    private final TourInfoRepository tourInfoRepository;

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

    private Query buildMatchQueryForKeywords(String[] keywords, String tourId) {
        // String[]으로 들어간 3개의 키워드를 하나의 쿼리 조건으로 변환
        StringBuilder shouldQueries = new StringBuilder();
        for (String keyword : keywords) {
            String condition = createMatchConditionOne("keywords", keyword);
            shouldQueries.append(condition).append(",");
        }
        // 마지막 쉼표 제거
        if (shouldQueries.length() > 0) {
            shouldQueries.setLength(shouldQueries.length() - 1);
        }

        String mustNotQuery = createKeywordTermCondition("id", tourId);

        String queryString = String.format("""
                    {
                        "bool": {
                            "should": [
                                %s
                            ],
                            "must_not": [
                                %s
                            ]
                        }
                    }
                """,
                shouldQueries.toString(),
                mustNotQuery
        );

        return new StringQuery(queryString);
    }

    private String createMatchConditionOne(String field, String value) {
        return String.format("""
            {
                "match": {
                    "%s": "%s"
                }
            }
            """, field, value);
    }

    private String createKeywordTermCondition(String field, String value) {
        return String.format("""
            {
                "term": {
                    "%s": "%s"
                }
            }
            """, field + ".keyword", value);
    }



    //관광지 정보 하나 가져오기
    public TourInfo getTourInfo(String tourId) {
        TourInfo tourInfo = tourInfoRepository.findById(tourId).orElse(null);
        return tourInfo;
    }

    /**상세페이지 추천 관광지*/
    public List<TourInfo> getRecommendations(String tourId) {
        TourInfo tourInfo = tourInfoRepository.findById(tourId).orElse(null);
        if (tourInfo == null) {
            return Collections.emptyList();
        }

        // 키워드를 기반으로 유사한 관광지 검색
        String[] keywords = tourInfo.getKeywords().split(", ");

        // 단일 matchQuery를 사용하여 쿼리 생성
        Query query = NativeQuery.builder()
                .withQuery(buildMatchQueryForKeywords(keywords, tourInfo.getId()))
                .build();

        SearchHits<TourInfo> searchHits = elasticsearchTemplate.search(query, TourInfo.class);
        return searchHits
                .getSearchHits()
                .stream()
                .limit(6)
                .map(hit -> hit.getContent())
                .collect(Collectors.toList());
    }










}

