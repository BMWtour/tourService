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
            Resource resource = resourceLoader.getResource("classpath:/static/data/TourInfoData.csv");
            try (Reader reader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8);
                 CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader())) {
                int count = 0;
                for (CSVRecord record : csvParser.getRecords()) {
                    // 열 데이터 추출
                    String sdNm = record.get("SD_NM"); // 시도명
                    String sggNm = record.get("SGG_NM"); // 시군구명
                    String emdNm = record.get("EMD_NM"); // 읍면동명
                    String tourNm = record.get("TOUR_NM"); // 지역문화관광지명
                    String addr = record.get("ADDR"); // 주소
                    String tourLa = record.get("TOUR_LA"); // 관광지 위도
                    String tourLo = record.get("TOUR_LO"); // 관광지 경도
                    String tourCrLm = record.get("TOUR_CL_NM"); // 관광지 분류명
                    String tourStryNm = record.get("TOUR_STRY_NM"); // 관광지 이야기명
                    String tourStrySmr = record.get("TOUR_STRY_SMR"); // 관광지 이야기 요약 내용
                    String tourStryUrl = record.get("TOUR_STRY_URL"); // 관광지 이야기 URL
                    String coreKwrd = record.get("CORE_KWRD"); // 핵심 키워드 내용
                    String pbtrnspClNm = record.get("PBTRNSP_CL_NM"); // 대중교통 분류명
                    String pbtrnspFcltyNm = record.get("PBTRNSP_FCLTY_NM"); // 대중교통 시설명
                    String bstpNoNm = record.get("BSTP_NO_NM"); // 정류장 번호명
                    String entrcNm = record.get("ENTRC_NM"); // 출입구명
                    String pbtrnspAddr = record.get("PBTRNSP_ADDR"); // 대중교통 시설 주소
                    String pbtrnspLa = record.get("PBTRNSP_LA"); // 시설 위도
                    String pbtrnspLo = record.get("PBTRNSP_LO"); // 시설 경도
                    Double dstncValue = Double.parseDouble(record.get("DSTNC_VALUE")); // 거리값
                    String openDay = record.get("OPEN_DAY"); // 개방일자


                    TourInfo tourInfo = TourInfo.builder()
                            .sdNm(sdNm)
                            .sggNm(sggNm)
                            .emdNm(emdNm)
                            .tourNm(tourNm)
                            .addr(addr)
                            .tourLa(tourLa)
                            .tourLo(tourLo)
                            .tourCrLm(tourCrLm)
                            .tourStryNm(tourStryNm)
                            .tourStrySmr(tourStrySmr)
                            .tourStryUrl(tourStryUrl)
                            .coreKwrd(coreKwrd)
                            .pbtrnspClNm(pbtrnspClNm)
                            .pbtrnspFcltyNm(pbtrnspFcltyNm)
                            .bstpNoNm(bstpNoNm)
                            .entrcNm(entrcNm)
                            .pbtrnspAddr(pbtrnspAddr)
                            .pbtrnspLa(pbtrnspLa)
                            .pbtrnspLo(pbtrnspLo)
                            .dstncValue(dstncValue)
                            .openDay(openDay)
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


}
