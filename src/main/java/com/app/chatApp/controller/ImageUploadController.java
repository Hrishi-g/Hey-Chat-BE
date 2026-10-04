package com.app.chatApp.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.time.Duration;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("user")
public class ImageUploadController {

    private final S3Presigner presigner;

    @Value("${cloudflare.r2.bucket-name}")
    private String bucketName;

    @Value("${cloudflare.r2.public-domain}")
    private String publicDomain;

    public ImageUploadController(S3Presigner presigner) {
        this.presigner = presigner;
    }

    @GetMapping("/presigned-url")
    public ResponseEntity<?> getPresignedUrl(@RequestParam String fileExtension) {
        // Validate file extension to prevent malicious uploads
        String ext = fileExtension.toLowerCase();
        if (!ext.equals("jpg") && !ext.equals("jpeg") && !ext.equals("png") && !ext.equals("webp") && !ext.equals("gif")) {
            return ResponseEntity.badRequest().body("Invalid file extension");
        }

        // Generate a unique filename: e.g., "avatars/a1b2c3d4.jpg"
        String objectKey = "uploads/" + UUID.randomUUID() + "." + ext;

        PutObjectRequest objectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(objectKey)
                .contentType("image/" + fileExtension)
                .build();

        // Expire presigned URL in 10 minutes
        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(10))
                .putObjectRequest(objectRequest)
                .build();

        String uploadUrl = presigner.presignPutObject(presignRequest).url().toString();
        String filePublicUrl = publicDomain + "/" + objectKey;

        return ResponseEntity.ok(Map.of(
                "uploadUrl", uploadUrl, // Temporary URL React uses to PUT the file
                "filePublicUrl", filePublicUrl // Permanent URL saved in your database
        ));
    }
}