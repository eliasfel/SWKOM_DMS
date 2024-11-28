package swkom_dms.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import swkom_dms.DTO.DocumentDTO;
import swkom_dms.service.DocumentService;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api")
public class DocumentController {

    private DocumentService documentService;
    @Autowired
    public DocumentController( DocumentService documentService) {
        this.documentService = documentService;
    }

    @GetMapping("/documents")
    public ResponseEntity<List<DocumentDTO>> getDocuments() {
        List<DocumentDTO> documents = documentService.getDocumentList();
        return ResponseEntity.ok(documents);
    }

    @PostMapping("/upload")
    public void uploadDocument(@RequestBody @Valid DocumentDTO documentDTO) {
        // Pass the validated DTO to the service
        DocumentDTO savedDTO = documentService.uploadDocument(documentDTO);
    }


    // Get a document by its ID
    @GetMapping("/documents/{id}")
    public ResponseEntity<DocumentDTO> getDocumentById(@PathVariable Long id) {
        return documentService.getDocumentById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Void> updateDocument(@PathVariable Long id, @RequestBody @Valid DocumentDTO documentDTO) {
        documentDTO.setId(id);  // Ensure the ID in the path is used.
        if (documentService.updateDocument(documentDTO)) {
            return ResponseEntity.noContent().build();  // 204 No Content when successful
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();  // 404 Not Found if the ID doesn't exist
        }
    }

    // Delete a document by its ID
    @DeleteMapping("/documents/{id}")
    public ResponseEntity<Void> deleteDocument(@PathVariable Long id) {
        if (documentService.deleteDocumentById(id)) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
