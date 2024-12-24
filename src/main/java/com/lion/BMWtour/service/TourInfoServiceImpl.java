package com.lion.BMWtour.service;

import com.lion.BMWtour.entitiy.TourInfo;
import com.lion.BMWtour.repository.TourInfoRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;


@Service
@RequiredArgsConstructor
public class TourInfoServiceImpl {

    private final TourInfoRepository tourInfoRepository;
    private final ResourceLoader resourceLoader;

    public void tourInfoInsert() {
        try {
            Resource resource = resourceLoader.getResource("classpath:/static/data/문화관광데이터.csv");
            try (Reader reader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8);
                 CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader())) {


                int count = 0;
                for (CSVRecord record : csvParser.getRecords()) {
                    // 열 데이터 추출
                    String name = record.get("\uFEFF명칭");
                    String address = record.get("주소");
                    float latitude = Float.parseFloat(record.get("위도"));
                    float longitude = Float.parseFloat(record.get("경도"));
                    String summary = record.get("개요");
                    String openTime = record.get("이용시간");
                    String detailInfo = record.get("상세정보");
                    String category = record.get("카테고리");


                    TourInfo tourInfo = TourInfo.builder()
                            .name(name)                      // name (명칭)
                            .address(address)                   // address (주소)
                            .latitude(latitude) // latitude (위도)
                            .longitude(longitude) // longitude (경도)
                            .summary(summary)                   // summary (개요)
                            .openTime(openTime)              // openTime (이용시간)
                            .detailInfo(detailInfo)            // detailInfo (상세정보)
                            .category(category)              // category (카테고리)
                            .build();


                    tourInfoRepository.save(tourInfo);




                    if (count++ == 1000) {
                        System.out.println("count = " + count);
                        break;
                    }
                }
                System.out.println("삽입 완료");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
