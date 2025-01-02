package com.lion.BMWtour.service;

import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.lion.BMWtour.dto.request.FileRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileUploadService {

    @Value("${spring.cloud.gcp.storage.bucket}")
    private String bucketName;

    private final Storage storage; // Spring Bean으로 관리되는 Storage 인스턴스를 주입받음

    public String uploadFile(FileRequest request) throws IOException {

        // !!!!!!!!!!!이미지 업로드 관련 부분!!!!!!!!!!!!!!!
        String uuid = UUID.randomUUID().toString(); // Google Cloud Storage에 저장될 파일 이름
        String ext = request.getFile().getContentType(); // 파일의 형식 ex) JPG
        // Cloud에 이미지 업로드
        BlobInfo blobInfo = storage.create(
                BlobInfo.newBuilder(bucketName, uuid)
                        .setContentType(ext)
                        .build(),
                request.getFile().getInputStream()
        );
        return uuid;
    }
}
