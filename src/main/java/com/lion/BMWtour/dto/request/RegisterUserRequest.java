package com.lion.BMWtour.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegisterUserRequest {

    private String userId; // 매핑된 요청 필드와 다름


    private String nickname;

    private String password;

    private String confirmPassword;
    private String[] category;
    private MultipartFile file;
}