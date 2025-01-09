package com.example.worker;

import com.example.worker.minio.MinIOConfig;
import com.example.worker.minio.MinIOFileStorage;
import io.minio.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MinIOFileStorageTest {

    @Mock
    private MinIOConfig minIOConfigMock;

    @Mock
    private io.minio.MinioClient minioClientMock;

    @InjectMocks
    private MinIOFileStorage fileStorage;

    @BeforeEach
    void setUp() {
    }

    @Test
    void testUploadCreatesBucketIfNotExists() throws Exception {
        String bucketName = "test-bucket";
        String objectName = "test-object";
        InputStream fileStream = new ByteArrayInputStream("Test content".getBytes());
        when(minIOConfigMock.getBucketName()).thenReturn(bucketName);
        when(minioClientMock.bucketExists(any(BucketExistsArgs.class))).thenReturn(false);

        fileStorage.upload(objectName, fileStream);

        verify(minioClientMock, times(1)).makeBucket(any(MakeBucketArgs.class));
        verify(minioClientMock, times(1)).putObject(any(PutObjectArgs.class));
    }

    @Test
    void testUploadDoesNotCreateBucketIfExists() throws Exception {
        String bucketName = "test-bucket";
        String objectName = "test-object";
        InputStream fileStream = new ByteArrayInputStream("Test content".getBytes());
        when(minIOConfigMock.getBucketName()).thenReturn(bucketName);
        when(minioClientMock.bucketExists(any(BucketExistsArgs.class))).thenReturn(true);

        fileStorage.upload(objectName, fileStream);

        verify(minioClientMock, never()).makeBucket(any(MakeBucketArgs.class));
        verify(minioClientMock, times(1)).putObject(any(PutObjectArgs.class));
    }

    @Test
    void testDownloadReturnsFileContent() throws Exception {
        String bucketName = "test-bucket";
        String objectName = "test-object";
        byte[] expectedContent = "Test content".getBytes();

        when(minIOConfigMock.getBucketName()).thenReturn(bucketName);

        GetObjectResponse getObjectResponseMock = mock(GetObjectResponse.class);
        when(getObjectResponseMock.readAllBytes()).thenReturn(expectedContent);

        when(minioClientMock.getObject(any(GetObjectArgs.class))).thenReturn(getObjectResponseMock);

        byte[] result = fileStorage.download(objectName);

        assertArrayEquals(expectedContent, result);
        verify(minioClientMock, times(1)).getObject(any(GetObjectArgs.class));
    }



    @Test
    void testUploadThrowsRuntimeExceptionOnError() throws Exception {
        String bucketName = "test-bucket";
        String objectName = "test-object";
        InputStream fileStream = new ByteArrayInputStream("Test content".getBytes());
        when(minIOConfigMock.getBucketName()).thenReturn(bucketName);

        when(minioClientMock.bucketExists(any(BucketExistsArgs.class)))
                .thenThrow(new RuntimeException("Test Exception"));

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            fileStorage.upload(objectName, fileStream);
        });

        verify(minioClientMock, times(1)).bucketExists(any(BucketExistsArgs.class));
    }


    @Test
    void testDownloadThrowsRuntimeExceptionOnError() throws Exception {
        String bucketName = "test-bucket";
        String objectName = "test-object";
        when(minIOConfigMock.getBucketName()).thenReturn(bucketName);

        when(minioClientMock.getObject(any(GetObjectArgs.class)))
                .thenThrow(new RuntimeException("Test Exception"));

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            fileStorage.download(objectName);
        });

        verify(minioClientMock, times(1)).getObject(any(GetObjectArgs.class));
    }

}
