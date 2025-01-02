package com.lion.BMWtour.entity;

import jakarta.persistence.Id;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
//import org.springframework.data.elasticsearch.annotations.Field;
//import org.springframework.data.elasticsearch.annotations.FieldType;

@Document(indexName = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    private String id;
    private String userId;
    private String userPw;
    private String userNickname;
    private String interest1;
    private String interest2;
    private String interest3;
    //private String interestList;
    private String[] interestList;
    private String provider;
    private String useYn;
    private String userImgUri;
    @Field(type = FieldType.Date, format = DateFormat.date)
    private LocalDate regDate;

//    @Field(type = FieldType.Text)
//    private String USER_IMGURI;
}
