package com.lion.BMWtour.entitiy;


import org.springframework.data.annotation.Id;
import lombok.*;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;


@Document(indexName = "tourinfos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TourInfo {
    @Id
    private long id;
    // 데이터관리번호
    private String sdNm;              // 시도명
    private String sggNm;              // 시군구명
    private String emdNm;                 // 읍면동명
    private String tourNm;     // 지역문화관광지명
    private String addr;                  // 주소
    private String tourLa;              // 관광지위도
    private String tourLo;              // 관광지경도
    private String tourCrLm;            // 관광지분류명
    private String tourStryNm;           // 관광지이야기명
    @Field(type = FieldType.Text)
    private String tourStrySmr;          // 관광지이야기 요약내용
    private String tourStryUrl;         // 관광지이야기URL
    private String coreKwrd;            // 핵심키워드내용
    private String pbtrnspClNm;           // 대중교통분류명
    private String pbtrnspFcltyNm;        // 대중교통시설명
    private String bstpNoNm;              // 정류장번호명
    private String entrcNm;               // 출입구명
    private String pbtrnspAddr;      // 대중교통시설주소
    private String pbtrnspLa;               // 시설위도
    private String pbtrnspLo;               // 시설경도
    private Double dstncValue;            // 거리값
    private String openDay;                 // 개방일자
}



