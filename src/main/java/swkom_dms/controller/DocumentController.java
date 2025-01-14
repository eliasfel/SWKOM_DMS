package swkom_dms.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import swkom_dms.DTO.DocumentDTO;
import swkom_dms.minIO.FileStorage;
import swkom_dms.service.DocumentService;

import jakarta.validation.Valid;

import java.time.LocalDateTime;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@CrossOrigin(origins = "http://localhost")
@RestController
@RequestMapping("/api")
public class DocumentController {

    private static final Logger logger = LogManager.getLogger(DocumentController.class);

    private DocumentService documentService;

    private FileStorage fileStorage;

    @Autowired
    public DocumentController(DocumentService documentService, FileStorage fileStorage) {
        this.documentService = documentService;
        this.fileStorage = fileStorage;
    }

    // Get all documents
    @GetMapping("/documents")
    public ResponseEntity<List<DocumentDTO>> getDocuments() {
        logger.info("Fetching all documents.");
        List<DocumentDTO> documents = documentService.getDocumentList();
        logger.info("Successfully fetched {} documents.", documents.size());
        return ResponseEntity.ok(documents);
    }

    @GetMapping("/documents/export")
    public ResponseEntity<byte[]> exportDocumentList() {
        byte[] fileData = documentService.generateExportFile();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=document_list.csv")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(fileData);
    }


    // Upload a new document
    @PostMapping("/upload")
    public ResponseEntity<Void> uploadDocument(@RequestParam("name") String name, @RequestParam("file") MultipartFile file) {
        logger.info("Received request to upload document with name: {}", name);

        DocumentDTO documentDTO = new DocumentDTO();
        documentDTO.setName(file.getOriginalFilename());
        try {
            documentDTO.setContent(file.getBytes()); // Store file content as String or use Base64 encoding
            documentDTO.setDateUploaded(LocalDateTime.now());
            fileStorage.upload(file.getOriginalFilename(), file.getInputStream());
            logger.info("filename is '{}'", file.getOriginalFilename());

            documentService.uploadDocument(documentDTO);
            logger.info("Document with name '{}.pdf' uploaded successfully.", name);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error uploading document with name '{}'.", name, e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    // Get a document by ID
    @GetMapping("/documents/{id}")
    public ResponseEntity<DocumentDTO> getDocumentById(@PathVariable Long id) {
        logger.info("Fetching document with ID: {}", id);
        return documentService.getDocumentById(id)
                .map(documentDTO -> {
                    logger.info("Document with ID: {} found.", id);
                    return ResponseEntity.ok(documentDTO);
                })
                .orElseGet(() -> {
                    logger.warn("Document with ID: {} not found.", id);
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
                });
    }

    // Update an existing document
    @PutMapping("/update/{id}")
    public ResponseEntity<Void> updateDocument(@PathVariable Long id, @RequestBody @Valid DocumentDTO documentDTO) {
        logger.info("Received request to update document with ID: {}", id);

        documentDTO.setId(id);  // Ensure the ID in the path is used
        if (documentService.updateDocument(documentDTO)) {
            logger.info("Document with ID: {} updated successfully.", id);
            return ResponseEntity.noContent().build();  // 204 No Content
        } else {
            logger.warn("Document with ID: {} not found, update failed.", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();  // 404 Not Found
        }
    }

    // Delete a document by ID
    @DeleteMapping("/documents/{id}")
    public ResponseEntity<Void> deleteDocument(@PathVariable Long id) {
        logger.info("Received request to delete document with ID: {}", id);
        if (documentService.deleteDocumentById(id)) {
            logger.info("Document with ID: {} deleted successfully.", id);
            return ResponseEntity.noContent().build();  // 204 No Content
        } else {
            logger.warn("Document with ID: {} not found, delete failed.", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();  // 404 Not Found
        }
    }
}
