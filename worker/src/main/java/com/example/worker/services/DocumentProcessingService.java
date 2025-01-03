package com.example.worker.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.example.worker.RabbitMQConfig;
import com.example.worker.services.OCRWorker;
import com.example.worker.minio.MinIOFileStorage;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

@Component
@Slf4j
public class DocumentProcessingService {

    private final MinIOFileStorage minioService;
    private final OCRWorker ocrWorker;
    private final RabbitTemplate rabbitTemplate;

    @Autowired
    public DocumentProcessingService(MinIOFileStorage minioService, OCRWorker ocrWorker, RabbitTemplate rabbitTemplate) {
        this.minioService = minioService;
        this.ocrWorker = ocrWorker;
        this.rabbitTemplate = rabbitTemplate;
    }

    @RabbitListener(queues = RabbitMQConfig.OCR_QUEUE)
    public void processDocument(String objectName) {
        log.info("Received document for processing: {}", objectName);

        try {
            // Step 1: Download file from MinIO
            byte[] documentBytes = minioService.download(objectName);
            log.info("Downloaded document from MinIO: {}", objectName);

            if (documentBytes == null || documentBytes.length == 0) {
                throw new IllegalArgumentException("Downloaded document is empty");
            }


            // Convert byte[] to InputStream
            InputStream documentStream = new ByteArrayInputStream(documentBytes);

            // Step 2: Perform OCR on the document
            String extractedContent = ocrWorker.performOCR(documentStream);
            log.info("OCR result for document {}: {}", objectName, extractedContent);

            // Step 3: Send extracted content to results queue
            rabbitTemplate.convertAndSend("", RabbitMQConfig.RESULT_QUEUE, extractedContent);
            log.info("Sent OCR result to RabbitMQ for further processing");

        } catch (Exception e) {
            log.error("Error processing document {}: {}", objectName, e.getMessage(), e);
        }
    }
}

