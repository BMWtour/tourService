package com.lion.BMWtour.entitiy;


import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import org.springframework.data.annotation.Id;
import lombok.*;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.w3c.dom.Text;


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



