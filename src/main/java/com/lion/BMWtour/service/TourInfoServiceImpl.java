package com.lion.BMWtour.service;

import com.lion.BMWtour.dto.SearchAutocompleteResponse;
import com.lion.BMWtour.dto.TourInfoDto;
import com.lion.BMWtour.entity.Category;
import com.lion.BMWtour.entity.TourInfo;
import com.lion.BMWtour.entity.User;
import com.lion.BMWtour.repository.CategoryRepository;
import com.lion.BMWtour.repository.TourInfoRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
@Slf4j
public class TourInfoServiceImpl implements TourInfoService {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final TourInfoRepository tourInfoRepository;
    private final UserService userService;
    private final CategoryRepository categoryRepository;

    @Override
    public PageImpl<TourInfoDto> getPagedTourInfos(HttpSession session, int page, String category, String address, String keyword, String sortField, String sortDirection) {
        String uid = (String) session.getAttribute("sessUid");
        User user = userService.findByUserId(uid);
        Pageable pageable = PageRequest.of(page - 1, TourInfoService.PAGE_SIZE);
        Sort.Direction direction = sortDirection.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Query query = null;
        // 로그인 하지 않은 경우
        if (user == null) {
            log.info("TourInfoServiceImpl - PageImpl: 로그인 하지 않은 사용자는 선호도가 없습니다.");
            query = NativeQuery.builder().withQuery(DefaultMatchQuery(category, address, keyword))
                    .withSort(Sort.by(Sort.Order.desc("_score")))
                    .withTrackScores(true).withPageable(pageable).build();
        } else { // 로그인을 한 경우
            // 사용자 하위카테고리
            List<String> userInterestList = List.of(user.getInterestList());
            List<String> userMatchingInterestList = new ArrayList<>();
            if (!category.isEmpty()) {
                // 사용자 상위-하위카테고리
                Map<String, ArrayList<String>> categoryAnduserSubcategory = new HashMap<>();

                // category인덱스에서 카테고리-하위카테고리(ex: 관광지-[자연관광지]) 가져오기
                Map<String, ArrayList<String>> categoryAndSubcategory = StreamSupport.stream(categoryRepository.findAll().spliterator(), false).collect(Collectors.toMap(Category::getName,  // -> category.getName()
                        item -> new ArrayList<>(item.getSubcategories())));

                // 사용자 관심사를 기반으로 카테고리-하위카테고리 매핑
                for (String userInterest : userInterestList) {
                    // 관심사가 포함된 카테고리 찾기 (하위 카테고리 중에 userInterest가 포함되면 해당 카테고리 반환)
                    categoryAndSubcategory.entrySet().stream().filter(entry -> entry.getValue().contains(userInterest)).findFirst().ifPresent(entry -> {  // ifPresent는 Optional 객체가 값이 있을 때만 실행되는 콜백 함수
                        categoryAnduserSubcategory.computeIfAbsent(entry.getKey(), k -> new ArrayList<>()) // 카테고리 "음식점"이 categoryAnduserSubcategory에 있다면 원래 배열, 없으면 새로운 ArrayList<String>을 생성
                                .add(userInterest); // 관심사 추가
                    });
                }
                // log.info(categoryAnduserSubcategory);

                // 매칭시킨 하위카테고리
                userMatchingInterestList = categoryAnduserSubcategory.getOrDefault(category, null);

                query = NativeQuery.builder().withQuery(UserMatchQuery(category, address, keyword, userMatchingInterestList))
                        .withSort(Sort.by(Sort.Order.desc("_score")))       // 1. matchScore 기준 정렬// 2. titel/author.keyword 기준 정렬
                        .withTrackScores(true).withPageable(pageable).build();
            }
        }
        SearchHits<TourInfo> searchHits = elasticsearchTemplate.search(query, TourInfo.class);
        List<TourInfoDto> TourInfoDtoList = searchHits.getSearchHits().stream().map(hit -> new TourInfoDto(hit.getContent(), hit.getScore())).toList();
        long totalHits = searchHits.getTotalHits();
        return new PageImpl<>(TourInfoDtoList, pageable, totalHits);
    }


    // 조건에 따라 match or match_all 반환
    private String createMatchCondition(String field, String value, String type) {
        switch (type) {
            case "keyword":
                return value.isEmpty() ? "{ \"match_all\": {} }" : String.format("{ \"term\": { \"%s\": { \"value\": \"%s\" } } }", field, value);
            case "text":
                return value.isEmpty() ? "{ \"match_all\": {} }" : String.format("{ \"match\": { \"%s\": { \"query\": \"%s\" } } }", field, value);
        }
        return "";
    }

    // 로그인 X 사용자 검색 쿼리
    private Query DefaultMatchQuery(String category, String address, String keyword) {

        category = createMatchCondition("category", category, "keyword");
        address = createMatchCondition("address", address, "text");
        String titlekeyword = createMatchCondition("title", keyword, "text");
        String summarykeyword = createMatchCondition("summary", keyword, "text");


        String queryString = String.format("""
                            {
                                "function_score": {
                                  "query": {
                                    "bool": {
                                      "filter": [
                                        %s,
                                        %s
                                      ],
                                      "should": [
                                        %s,
                                        %s
                                      ]
                                    }
                                  }
                              }
                            }
                            """, category, address,
                                titlekeyword, summarykeyword);
        log.info("TourInfoServiceImpl - DefaultMatchQuery: queryString: " + queryString);
        return new StringQuery(queryString);
    }

    // 로그인 O 사용자 검색 쿼리
    private Query UserMatchQuery(String category, String address, String keyword, List<String> preferenceList) {

        category = createMatchCondition("category", category, "keyword");
        address = createMatchCondition("address", address, "text");
        String titlekeyword = createMatchCondition("title", keyword, "text");
        String summarykeyword = createMatchCondition("summary", keyword, "text");
        // preferenceList에 대해서 조건을 누적
        StringBuilder preferenceConditions = new StringBuilder();  // string 은 문자열 변경 시 새 객체, string bulider 는 원본 객체
        preferenceList.forEach(preference -> {
            preferenceConditions.append(createMatchCondition("summary", preference, "text")).append(", ");
        });
        preferenceConditions.setLength(preferenceConditions.length() - 2);  // substring -> string 새 객체 생성, setLength -> string bulider 원본 객체
        String queryString;

        if (keyword.isEmpty()) {
            queryString = String.format("""
                            {
                                "function_score": {
                                  "query": {
                                    "bool": {
                                      "filter": [
                                        %s,
                                        %s
                                      ],
                                      "should": [
                                        %s,
                                        %s
                                      ]
                                    }
                                  },
                                  "functions": [
                                    {
                                      "filter": {
                                        "bool": {
                                          "should": [
                                            %s
                                          ]
                                        }
                                      },
                                      "weight": 2.0
                                    }
                                  ],
                                  "boost_mode": "multiply"
                                }
                              }
                            """, category, address,
                                titlekeyword, summarykeyword,
                                preferenceConditions.toString().trim());
        } else {
            queryString = String.format("""
                            {
                                "function_score": {
                                  "query": {
                                    "bool": {
                                      "filter": [
                                        %s,
                                        %s
                                      ],
                                      "should": [
                                        %s,
                                        %s
                                      ]
                                    }
                                  },
                                  "functions": [
                                    {
                                      "filter": {
                                        "bool": {
                                          "should": [
                                            %s,
                                            %s
                                          ]
                                        }
                                      },
                                      "weight": 3.0
                                    },
                                    {
                                      "filter": {
                                        "bool": {
                                          "should": [
                                            %s
                                          ]
                                        }
                                      },
                                      "weight": 2.0
                                    }
                                  ],
                                  "boost_mode": "multiply"
                                }
                              }
                            """, category, address,
                    titlekeyword, summarykeyword,
                    titlekeyword, summarykeyword,
                    preferenceConditions.toString().trim());
        }

        log.info("TourInfoServiceImpl - UserMatchQuery: queryString: " + queryString);
        return new StringQuery(queryString);
    }


    //관광지 정보 하나 가져오기
    public TourInfo getTourInfo(String tourId) {
        TourInfo tourInfo = tourInfoRepository.findById(tourId).orElse(null);
        return tourInfo;
    }

    @Override
    public List<SearchAutocompleteResponse> getSearchAutocompleteList(String word) {
        StringQuery query = new StringQuery(String.format("""
            {
              "multi_match": {
                "query": "%s",
                "fields": ["title.ngram", "summary"],
                "fuzziness": "AUTO"
              }
            }
            """, word));

        NativeQuery nativeQuery = NativeQuery.builder()
            .withQuery(query)
            .withSort(Sort.by(Sort.Direction.ASC, "title"))
            .withMaxResults(10)
            .build();

        SearchHits<TourInfo> searchHits = elasticsearchTemplate.search(nativeQuery, TourInfo.class);
        return searchHits.getSearchHits().stream()
            .map(hit -> SearchAutocompleteResponse.builder()
                .title(hit.getContent().getTitle())
                .build())
            .toList();
    }
}






