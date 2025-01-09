package com.example.worker.minio;

import io.minio.*;
import io.minio.errors.*;
import org.apache.commons.compress.utils.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Service
public class MinIOFileStorage implements FileStorage {
    private final MinIOConfig minIOConfig;
    private final MinioClient minioClient;

    @Autowired
    public MinIOFileStorage(MinIOConfig minIOConfig, MinioClient minioClient) {
        this.minIOConfig = minIOConfig;
        this.minioClient = minioClient;
    }

    @Override
    public void upload(String objectName, InputStream file) {
        try {
            boolean hasBucketWithName =
                    minioClient.bucketExists(
                            BucketExistsArgs
                                    .builder()
                                    .bucket(minIOConfig.getBucketName())
                                    .build()
                    );
            if (!hasBucketWithName) {
                minioClient.makeBucket(
                        MakeBucketArgs
                                .builder()
                                .bucket(minIOConfig.getBucketName())
                                .build()
                );
            }

            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(minIOConfig.getBucketName())
                            .object(objectName)
                            .stream(file,-1, 1024*1024*5)
                            .build()
            );

        } catch (MinioException e) {
            System.out.println("Error occurred: " + e);
            System.out.println("HTTP trace: " + e.httpTrace());
        } catch (IOException | NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public byte[] download(String objectName) {
        try {
            GetObjectResponse response = minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(minIOConfig.getBucketName())
                            .object(objectName)
                            .build()
            );
            return response.readAllBytes();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
