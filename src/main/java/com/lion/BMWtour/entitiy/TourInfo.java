package com.lion.BMWtour.entitiy;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
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
    private String id;
    private String name;
    private String address;
    private float latitude;
    private float longitude;
    @Field(type = FieldType.Text)
    private String summary;
    private String openTime;
    @Field(type = FieldType.Text)
    private String detailInfo;
    private String category;
}



