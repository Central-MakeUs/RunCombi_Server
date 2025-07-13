package com.runcombi.server.global.s3.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.runcombi.server.global.exception.CustomException;
import com.runcombi.server.global.s3.dto.S3ImageReturnDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

import static com.runcombi.server.global.exception.code.CustomErrorList.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3Service {
    private final AmazonS3 s3Client;
    @Value("${spring.s3.bucket-name}")
    private final String bucketName;
    private String defaultUrl = "https://runcombi.s3.ap-northeast-2.amazonaws.com/";

    public S3ImageReturnDto uploadMemberImage(MultipartFile file, Long memberId) {
        String uuid = UUID.randomUUID().toString();
        String imageName = "member/" + memberId + uuid;
        try {
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, imageName, file.getInputStream(), getObjectMetaData(file));
            s3Client.putObject(putObjectRequest);
            return S3ImageReturnDto
                    .builder()
                    .imageUrl(defaultUrl + imageName)
                    .imageKey(imageName)
                    .build();
        }catch(IOException e) {
            throw new CustomException(S3_IMAGE_UPLOAD_ERROR);
        }
    }

    public S3ImageReturnDto uploadPetImage(MultipartFile file, Long petId) {
        String uuid = UUID.randomUUID().toString();
        String imageName = "pet/" + petId + uuid;
        try {
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, imageName, file.getInputStream(), getObjectMetaData(file));
            s3Client.putObject(putObjectRequest);
            return S3ImageReturnDto
                    .builder()
                    .imageUrl(defaultUrl + imageName)
                    .imageKey(imageName)
                    .build();
        }catch(IOException e) {
            throw new CustomException(S3_IMAGE_UPLOAD_ERROR);
        }
    }

    public S3ImageReturnDto uploadRunImage(MultipartFile file, Long runId) {
        String uuid = UUID.randomUUID().toString();
        String imageName = "run/" + runId + uuid;
        try {
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, imageName, file.getInputStream(), getObjectMetaData(file));
            s3Client.putObject(putObjectRequest);
            return S3ImageReturnDto
                    .builder()
                    .imageUrl(defaultUrl + imageName)
                    .imageKey(imageName)
                    .build();
        }catch(IOException e) {
            throw new CustomException(S3_IMAGE_UPLOAD_ERROR);
        }
    }

    public void deleteImage(String key) {
        try {
            if(s3Client.doesObjectExist(bucketName, key)) s3Client.deleteObject(bucketName, key);
        } catch(Exception e) {
            throw new CustomException(S3_IMAGE_DELETE_ERROR);
        }
    }
    public ObjectMetadata getObjectMetaData(MultipartFile file) {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(file.getContentType());
        objectMetadata.setContentLength(file.getSize());
        return objectMetadata;
    }
}
