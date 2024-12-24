package com.lion.BMWtour.controller;

import com.lion.BMWtour.request.FileRequest;
import com.lion.BMWtour.service.FileUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.io.IOException;

@Controller
@RequiredArgsConstructor
public class FileUploadController {

    private final FileUploadService fileUploadService;

    @PostMapping("/api/gcs/upload")
    public ResponseEntity<String> objectUpload(FileRequest request) throws IOException {

        String uuid =  fileUploadService.uploadFile(request);
        // 이미지 주소는
        // https://storage.cloud.google.com/gcs_img_tour_service/UUID?authuser=1

        return new ResponseEntity<>(uuid, HttpStatus.OK);
    }

}