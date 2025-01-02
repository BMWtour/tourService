package com.lion.BMWtour.dto.request;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class UpdateUserRequest {
    private String userId;
    private String userPw;
    private String userNickname;
    private String[] interestList;
    private MultipartFile file;
}
