package com.example.worker.services;

import lombok.extern.slf4j.Slf4j;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

@Service
@Slf4j
public class OCRWorker {

    private final Tesseract tesseract;

    public OCRWorker() {
        this.tesseract = new Tesseract();
        String dataPath = "/usr/share/tessdata";
        if (Files.exists(Path.of(dataPath))) {
            this.tesseract.setDatapath(dataPath);
        } else {
            log.warn("Tesseract datapath {} does not exist. Ensure the tessdata directory is correctly mounted.", dataPath);
        }

        this.tesseract.setLanguage("eng");
    }

    public String performOCR(InputStream documentStream) {
        try {
            // Check if the InputStream is empty
            if (documentStream.available() == 0) {
                log.error("Received an empty document for OCR.");
                throw new RuntimeException("Received an empty document for OCR.");
            }

            // Generate a unique filename for the temporary file
            String uniqueFileName = "ocr-" + UUID.randomUUID().toString() + ".pdf";

            // Save the input stream to a temporary file with the unique filename
            Path tempFile = Path.of("/tmp", uniqueFileName);  // Use the unique filename here
            Files.copy(documentStream, tempFile);  // Copy the input stream to this unique file

            log.info("Temporary file created at: {}", tempFile.toString());

            // Perform OCR and return the result
            String result = tesseract.doOCR(tempFile.toFile());

            // Clean up temporary file
            Files.delete(tempFile);

            return result;

        } catch (TesseractException e) {
            log.error("OCR failed", e);
            throw new RuntimeException("Failed to extract text using OCR", e);
        } catch (Exception e) {
            log.error("Unexpected error during OCR", e);
            throw new RuntimeException("Unexpected error during OCR", e);
        }
    }
}
