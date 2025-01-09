package com.example.worker.services;

import com.example.worker.RabbitMQConfig;
import com.example.worker.minio.MinIOFileStorage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
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

    @Test
    public void testProcessDocument_Success() throws Exception {
        String objectName = "document1.pdf";
        byte[] documentBytes = "This is a test document.".getBytes();
        String ocrResult = "Extracted text from the document.";

        when(minioService.download(objectName)).thenReturn(documentBytes);
        when(ocrWorker.performOCR(any())).thenReturn(ocrResult);

        documentProcessingService.processDocument(objectName);

        verify(minioService, times(1)).download(objectName);
        verify(ocrWorker, times(1)).performOCR(any());
        verify(rabbitTemplate, times(1)).convertAndSend("", RabbitMQConfig.RESULT_QUEUE, ocrResult);
    }

    @Test
    public void testProcessDocument_OCRFailure() throws Exception {
        String objectName = "document1.pdf";
        byte[] documentBytes = "This is a test document.".getBytes();

        when(minioService.download(objectName)).thenReturn(documentBytes);
        when(ocrWorker.performOCR(any())).thenThrow(new RuntimeException("OCR failed"));

        documentProcessingService.processDocument(objectName);

        verify(minioService, times(1)).download(objectName);
        verify(ocrWorker, times(1)).performOCR(any());
        verify(rabbitTemplate, never()).convertAndSend(anyString(), anyString(), Optional.ofNullable(any()));
    }

    @Test
    public void testProcessDocument_EmptyFile() throws Exception {
        String objectName = "document1.pdf";
        byte[] emptyDocumentBytes = new byte[0];

        when(minioService.download(objectName)).thenReturn(emptyDocumentBytes);

        documentProcessingService.processDocument(objectName);

        verify(minioService, times(1)).download(objectName);
        verify(ocrWorker, never()).performOCR(any());
        verify(rabbitTemplate, never()).convertAndSend(anyString(), anyString(), Optional.ofNullable(any()));
    }

    @Test
    public void testProcessDocument_MinIODownloadFailure() throws Exception {
        String objectName = "document1.pdf";

        when(minioService.download(objectName)).thenThrow(new RuntimeException("Download failed"));

        documentProcessingService.processDocument(objectName);

        verify(minioService, times(1)).download(objectName);
        verify(ocrWorker, never()).performOCR(any());
        verify(rabbitTemplate, never()).convertAndSend(anyString(), anyString(), Optional.ofNullable(any()));
    }
}
