package com.lion.BMWtour.service;

import com.lion.BMWtour.dto.PointDto;
import com.lion.BMWtour.entitiy.TourInfo;
import com.lion.BMWtour.repository.TourInfoRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.core.query.StringQuery;
import org.springframework.stereotype.Service;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
@RequiredArgsConstructor
public class TourInfoServiceImpl {
    public static final int PAGE_SIZE = 5;
    private final TourInfoRepository tourInfoRepository;
    private final ResourceLoader resourceLoader;
    private final ElasticsearchTemplate elasticsearchTemplate;

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
        String queryString = String.format("""
                                {
                                    "match": {
                                        "%s": {
                                            "query": "%s",
                                            "fuzziness": "AUTO"
                                        }
                                    }
                                }        
                        """,
                field, keyword
        );
        return new StringQuery(queryString);
    }


//    public void tourInfoBulkInsert() {
//        try {
//
//
//            RestHighLevelClient client = new RestHighLevelClient(
//                    RestClient.builder(new HttpHost("104.198.205.64", 9200, "http"))
//            );
//
//            BulkRequest bulkRequest = new BulkRequest();
//
//            Resource resource = resourceLoader.getResource("classpath:/static/data/문화관광데이터.csv");
//            try (Reader reader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8);
//                 CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader())) {
//
//
//                int count = 0;
//                for (CSVRecord record : csvParser.getRecords()) {
//
//                    // 데이터 추출
//                    String title = record.get("\uFEFF명칭");
//                    String address = record.get("주소");
//                    float latitude = Float.parseFloat(record.get("위도"));
//                    float longitude = Float.parseFloat(record.get("경도"));
//                    String summary = record.get("개요");
//                    String openTime = record.get("이용시간");
//                    String detailInfo = record.get("상세정보");
//                    String category = record.get("카테고리");
//
//                    // GeoPoint를 사용하여 위도, 경도 데이터 추가
//                    Map<String, Object> jsonMap = new HashMap<>();
//                    jsonMap.put("title", title);
//                    jsonMap.put("address", address);
//                    //jsonMap.put("location", new GeoPoint(latitude, longitude));
//                    jsonMap.put("location", new double[]{latitude, longitude});  // GeoPoint 대신 배열로 저장
//                    jsonMap.put("summary", summary);
//                    jsonMap.put("open_time", openTime);
//                    jsonMap.put("detail_info", detailInfo);
//                    jsonMap.put("category", category);
//                    System.out.println("count: " + ++count);
//                    if (count % 5000 ==0 || count == 47042) {
//                        // Elasticsear  ch에 삽입할 IndexRequest 생성
//                        org.elasticsearch.action.index.IndexRequest indexRequest = new org.elasticsearch.action.index.IndexRequest("tourinfos")
//                                .id(null)  // 예시로 ID를 2로 설정 (실제 데이터에 맞게 변경)
//                                .source(jsonMap);
//
//                        // BulkRequest에 추가 (이후 BulkRequest에 추가된 요청들을 한 번에 전송)
//                        bulkRequest.add(indexRequest);
//
//                        // Bulk 요청 실행
//                        BulkResponse bulkResponse = client.bulk(bulkRequest, RequestOptions.DEFAULT);
//
//                        if (bulkResponse.hasFailures()) {
//                            System.out.println("Bulk insert failed");
//                        } else {
//                            System.out.println("Bulk insert successful");
//                        }
//                    }
//                }
//
//
//                client.close();
//                System.out.println("삽입 완료");
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }



    public void tourInfoInsert() {
        try {
            Resource resource = resourceLoader.getResource("classpath:/static/data/TourInfoData.csv");
            try (Reader reader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8);
                 CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader())) {
                int count = 0;
                for (CSVRecord record : csvParser.getRecords()) {
                    // 열 데이터 추출

                    String title = record.get("\uFEFF명칭");
                    String address = record.get("주소");
                    float latitude = Float.parseFloat(record.get("위도"));
                    float longitude = Float.parseFloat(record.get("경도"));
                    String summary = record.get("개요");
                    String openTime = record.get("이용시간");
                    String detailInfo = record.get("상세정보");
                    String category = record.get("카테고리");


                    TourInfo tourInfo = TourInfo.builder()
                            .title(title)                      // name (명칭)
                            .address(address)                   // address (주소)
                            .point((List<Double>) new PointDto((double) latitude, (double) longitude))
                            .summary(summary)                   // summary (개요)
                            .openTime(openTime)              // openTime (이용시간)
                            .detailInfo(detailInfo)            // detailInfo (상세정보)
                            .category(category)              // category (카테고리)
                            .build();


                    tourInfoRepository.save(tourInfo);


                    // if (count++ == 1000) {
                    // System.out.println("count = " + count);
                    // break;
                    // }
                }
                System.out.println("삽입 완료");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    //관광지 정보 하나 가져오기
    public TourInfo getTourInfo(String tourId) {
        Object test1 = tourInfoRepository.findById("PUJDBpQBo6YNDptrFbT_").orElse(null);
        //PUJDBpQBo6YNDptrFbT_
        TourInfo tourInfo = tourInfoRepository.findById("PUJDBpQBo6YNDptrFbT_").orElse(null);
        return tourInfo;
    }

}
