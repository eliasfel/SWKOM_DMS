package com.example.worker.services;

import com.example.worker.RabbitMQConfig;
import com.example.worker.minio.MinIOFileStorage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DocumentProcessingServiceTest {

    @Mock
    private MinIOFileStorage minioService;

    @Mock
    private OCRWorker ocrWorker;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private DocumentProcessingService documentProcessingService;

    @BeforeEach
    public void setUp() {
        // Any setup needed before each test can go here
    }

    @Test
    public void testProcessDocument_Success() throws Exception {
        // Arrange
        String objectName = "document1.pdf";
        byte[] documentBytes = "This is a test document.".getBytes();
        String ocrResult = "Extracted text from the document.";

        when(minioService.download(objectName)).thenReturn(documentBytes);
        when(ocrWorker.performOCR(any())).thenReturn(ocrResult);

        // Act
        documentProcessingService.processDocument(objectName);

        // Assert
        verify(minioService, times(1)).download(objectName);
        verify(ocrWorker, times(1)).performOCR(any());
        verify(rabbitTemplate, times(1)).convertAndSend("", RabbitMQConfig.RESULT_QUEUE, ocrResult);
    }

    @Test
    public void testProcessDocument_OCRFailure() throws Exception {
        // Arrange
        String objectName = "document1.pdf";
        byte[] documentBytes = "This is a test document.".getBytes();
        when(minioService.download(objectName)).thenReturn(documentBytes);
        when(ocrWorker.performOCR(any())).thenThrow(new RuntimeException("OCR failed"));

        // Act & Assert
        documentProcessingService.processDocument(objectName);

        // Verify that the OCR method was called, but the result was not sent to RabbitMQ due to failure
        verify(rabbitTemplate, never()).convertAndSend(Optional.ofNullable(any()), any(), any());
    }
}
