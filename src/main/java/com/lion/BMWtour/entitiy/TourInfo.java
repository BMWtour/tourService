package com.lion.BMWtour.entitiy;


import com.lion.BMWtour.dto.PointDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.*;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;

import java.util.ArrayList;
import java.util.List;


@Document(indexName = "tourinfos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setting(settingPath = "elasticsearch/tourinfos-setting.json")
@Mapping(mappingPath = "elasticsearch/tourinfos-mapping.json")
public class TourInfo {
    @Id
    private String id;
    private String title;
    @Field(type = FieldType.Text)
    private String address;
    private List<Double> point = new ArrayList<>();
    @Field(type = FieldType.Text)
    private String summary;
    private String openTime;
    @Field(type = FieldType.Text)
    private String detailInfo;
    private String category;
}