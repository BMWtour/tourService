package com.lion.BMWtour.entitiy;

import jakarta.persistence.Id;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Document;
//import org.springframework.data.elasticsearch.annotations.Field;
//import org.springframework.data.elasticsearch.annotations.FieldType;

@Document(indexName = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    private long id;
    private String user_id;
    private String user_pw;
    private String user_nickname;
    private String interest1;
    private String interest2;
    private String interest3;
    private String provider;
    private String use_yn;
    private String user_imgUri;
    private LocalDate regDate;

//    @Field(type = FieldType.Text)
//    private String USER_IMGURI;
}
