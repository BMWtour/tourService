package com.lion.BMWtour.service;

import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.http.HttpHost;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.domain.*;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;
import org.springframework.data.elasticsearch.core.query.GeoDistanceOrder;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.core.query.StringQuery;
import org.springframework.stereotype.Service;

import com.lion.BMWtour.dto.TourInfoDto;
import com.lion.BMWtour.entity.Category;
import com.lion.BMWtour.entity.TourInfo;
import com.lion.BMWtour.entity.User;
import com.lion.BMWtour.repository.CategoryRepository;
import com.lion.BMWtour.repository.TourInfoRepository;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class TourInfoServiceImpl implements TourInfoService {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final TourInfoRepository tourInfoRepository;
    private final UserService userService;
    private final CategoryRepository categoryRepository;
    private final ResourceLoader resourceLoader;

    @Override
    public Map<Page<TourInfoDto>, Integer> getPagedTourInfos(HttpSession session, int page, int researchCount, String category, String address, String keyword, String sortField, String sortDirection) {
        String uid = (String) session.getAttribute("sessUid");
        User user = userService.findByUserId(uid);
        int pageSize = limitResearchCount(researchCount);


        int TempPageSize = pageSize * page > 10000
                ? Math.min(pageSize, 10000 - (page - 1) * pageSize)
                : pageSize;

        Pageable pageable = PageRequest.of(page - 1, TempPageSize);
        Sort sort = null;





        Sort.Direction direction = sortDirection.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        if (sortField.isEmpty()) {
            sort = Sort.by(Sort.Order.desc("_score"));
        }
        else{
            switch (sortField) {
                case "point" :
                    GeoPoint geoPoint = getGeoPointFromSession(session); // 세션에서 좌표를 가져오는 방법

                    sort = Sort.by(Sort.Order.desc("_score")) // 기본 _score 정렬
                            .and(Sort.by(new GeoDistanceOrder("point",geoPoint
                                )).ascending());
                    break;
                default:       sort= Sort.by(Sort.Order.desc("_score"))
                                        .and(Sort.by(direction, sortField));
                    break;
            }
        }

        Query query = null;

        // 로그인 하지 않은 경우
        if (user == null) {
            log.info("TourInfoServiceImpl - PageImpl: 로그인 하지 않은 사용자는 선호도가 없습니다.");
            query = NativeQuery.builder().withQuery(DefaultMatchQuery(category, address, keyword)).withSort(sort).withTrackScores(true).withPageable( pageable).build();

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
                query =   userMatchingInterestList != null ?
                          NativeQuery.builder().withQuery(UserMatchQuery(category, address, keyword, userMatchingInterestList)).withSort(sort).withTrackScores(true).withPageable(pageable).build()
                        : NativeQuery.builder().withQuery(DefaultMatchQuery(category, address, keyword)).withSort(sort).withTrackScores(true).withPageable(pageable).build();

            }
            else{
                query = NativeQuery.builder().withQuery(UserMatchQuery(category, address, keyword, userInterestList)).withSort(sort).withTrackScores(true).withPageable(pageable).build();
            }
        }

        SearchHits<TourInfo> searchHits = elasticsearchTemplate.search(query, TourInfo.class);
        List<TourInfoDto> TourInfoDtoList = searchHits.getSearchHits().stream().map(hit -> new TourInfoDto(hit.getContent(), hit.getScore())).toList();
        long totalHits = searchHits.getTotalHits();


        Page<TourInfoDto> pageResult = new PageImpl<>(TourInfoDtoList, pageable, totalHits);
        return Map.of(pageResult, pageSize);
    }

    private int limitResearchCount(int researchCount) {
        if (researchCount >= 50) {
            return 50;
        } else if (researchCount >= 30) {
            return 30;
        } else if (researchCount >= 20) {
            return 20;
        } else {
            return TourInfoService.PAGE_SIZE; // 기본값
        }
    }

    // 세션에서 좌표 값을 가져오는 메서드
    private GeoPoint getGeoPointFromSession(HttpSession session) {
        try {
            double latitude = (double) session.getAttribute("latitude");
            double longitude = (double) session.getAttribute("longitude");
            return new GeoPoint(latitude, longitude);
        } catch (NumberFormatException | NullPointerException e) {
            // 예외 처리: 세션에서 잘못된 데이터가 있으면 기본 좌표로 처리
            return new GeoPoint(37.566381, 126.977717);
        }
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
                          ], "minimum_should_match": 1
                        }
                      }
                  }
                }
                """, category, address, titlekeyword, summarykeyword);
        log.info("TourInfoServiceImpl - DefaultMatchQuery: queryString: " + queryString);
        return new StringQuery(queryString);
    }

    // 로그인 O 사용자 검색 쿼리
    private Query UserMatchQuery(String cat, String add, String keyword, List<String> preferenceList) {

        String category = createMatchCondition("category", cat, "keyword");
        String address = createMatchCondition("address", add, "text");
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
                                %s
                              ] , "minimum_should_match": 1
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
                    """, category, address, preferenceConditions.toString().trim(), preferenceConditions.toString().trim());
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
                              ], "minimum_should_match": 1
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
                    """, category, address, titlekeyword, summarykeyword, titlekeyword, summarykeyword, preferenceConditions.toString().trim());
        }

        log.info("TourInfoServiceImpl - UserMatchQuery: queryString: " + queryString);
        return new StringQuery(queryString);
    }

    private Query buildMatchQueryForKeywords(String[] keywords, String tourId) {
        // String[]으로 들어간 3개의 키워드를 하나의 쿼리 조건으로 변환
        StringBuilder shouldQueries = new StringBuilder();
        for (String keyword : keywords) {
            String condition = createMatchConditionOne("keyword", keyword);
            shouldQueries.append(condition).append(",");
        }
        // 마지막 쉼표 제거
        if (shouldQueries.length() > 0) {
            shouldQueries.setLength(shouldQueries.length() - 1);
        }

        String mustNotQuery = createKeywordTermCondition("_id", tourId);

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
                """, shouldQueries.toString(), mustNotQuery);
        log.info("buildMatchQueryForKeywords - queryString:" + queryString);
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
//        return String.format("""
//                {
//                    "term": {
//                        "%s": "%s"
//                    }
//                }
//                """, field + ".keyword", value);

        return String.format("""
               {
                    "term": {
                        "%s": "%s"
                    }
               }
                """, field , value);
    }


    //관광지 정보 하나 가져오기
    public TourInfo getTourInfo(String tourId) {
        TourInfo tourInfo = tourInfoRepository.findById(tourId).orElse(null);
        return tourInfo;
    }

    /**
     * 상세페이지 추천 관광지
     */
    public List<TourInfo> getRecommendations(String tourId) {
        TourInfo tourInfo = tourInfoRepository.findById(tourId).orElse(null);
        if (tourInfo == null) {
            return Collections.emptyList();
        }

        // 키워드를 기반으로 유사한 관광지 검색
        String[] keywords = tourInfo.getKeyword().split(", ");

        // 단일 matchQuery를 사용하여 쿼리 생성
        Query query = NativeQuery.builder().withQuery(buildMatchQueryForKeywords(keywords, tourInfo.getId())).build();

        SearchHits<TourInfo> searchHits = elasticsearchTemplate.search(query, TourInfo.class);
        return searchHits.getSearchHits().stream().limit(5).map(hit -> hit.getContent()).collect(Collectors.toList());
    }

    @Override
    public List<String> getSearchAutocompleteList(String word) {
        StringQuery query = new StringQuery(String.format("""
                {
                  "multi_match": {
                    "query": "%s",
                    "fields": ["title.ngram^2", "summary"], // title에 더 높은 가중치
                    "fuzziness": "AUTO"
                  }
                }
                """, word));

        NativeQuery nativeQuery = NativeQuery.builder().withQuery(query).withMaxResults(10).build();

        SearchHits<TourInfo> searchHits = elasticsearchTemplate.search(nativeQuery, TourInfo.class);
        return searchHits.getSearchHits().stream().map(hit -> hit.getContent().getTitle()).toList();
    }


//    public void tourInfoBulkInsert() {
//        try {
//            RestHighLevelClient client = new RestHighLevelClient(RestClient.builder(new HttpHost("104.198.205.64", 9200, "http")));
//            BulkRequest bulkRequest = new BulkRequest();
//            Resource resource = resourceLoader.getResource("classpath:/static/data/문화관광데이터.csv");
//            try (Reader reader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8); CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader())) {
//                int count = 0;
//                for (CSVRecord record : csvParser.getRecords()) {
//                    // 데이터 추출
//                    String title = record.get("\uFEFF명칭");
//                    String address = record.get("주소");
//                    float latitude = Float.parseFloat(record.get("위도"));
//                    float longitude = Float.parseFloat(record.get("경도"));
//                    String summary = record.get("개요");
//                    String openTime = record.get("이용시간");
//                    String detailInfo = record.get("상세정보");
//                    String category = record.get("카테고리");
//                    String keyword = record.get("키워드");
//                    // GeoPoint를 사용하여 위도, 경도 데이터 추가
//                    Map<String, Object> jsonMap = new HashMap<>();
//                    jsonMap.put("title", title);
//                    jsonMap.put("address", address);
//                    //jsonMap.put("point", new GeoPoint(latitude, longitude));
//                    jsonMap.put("point", new double[]{longitude, latitude});
//                    // jsonMap.put("point", new double[]{latitude, longitude});  // GeoPoint 대신 배열로 저장
//                    jsonMap.put("summary", summary);
//                    jsonMap.put("openTime", openTime);
//                    jsonMap.put("detailInfo", detailInfo);
//                    jsonMap.put("category", category);
//                    jsonMap.put("keyword", keyword);
//                    System.out.println("count: " + ++count);
//
//
//                    // Elasticsear  ch에 삽입할 IndexRequest 생성
//                    IndexRequest indexRequest = new IndexRequest("tourinfos").id(null)  // 예시로 ID를 2로 설정 (실제 데이터에 맞게 변경)
//                            .source(jsonMap);
//
//                    // BulkRequest에 추가 (이후 BulkRequest에 추가된 요청들을 한 번에 전송)
//                    bulkRequest.add(indexRequest);
//
//
//                    if (count % 5000 == 0 || count == 47042) {
//                        // Bulk 요청 실행
//                        BulkResponse bulkResponse = client.bulk(bulkRequest, RequestOptions.DEFAULT);
//
//                        if (bulkResponse.hasFailures()) {
//                            bulkResponse.buildFailureMessage();
//                            System.out.println("Bulk insert failed");
//                        } else {
//                            System.out.println("Bulk insert successful");
//                        }
//                        bulkRequest = new BulkRequest();
//                    }
//                }
//                client.close();
//                System.out.println("삽입 완료");
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

}
