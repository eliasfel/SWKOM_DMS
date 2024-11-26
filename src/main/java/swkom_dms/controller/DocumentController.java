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
public class DocumentController {

    @Autowired
    private DocumentService documentService;


    @GetMapping("/documents")
    public ResponseEntity<List<DocumentDTO>> getDocuments() {
        List<DocumentDTO> documents = documentService.getDocumentList();
        return ResponseEntity.ok(documents);
    }

    @PostMapping("/upload")
    public ResponseEntity<DocumentDTO> uploadDocument(@RequestBody @Valid DocumentDTO documentDTO) {
        // Pass the validated DTO to the service
        DocumentDTO savedDTO = documentService.uploadDocument(documentDTO);
        return new ResponseEntity<>(savedDTO, HttpStatus.CREATED);
    }


    // Get a document by its ID
    @GetMapping("/documents/{id}")
    public ResponseEntity<DocumentDTO> getDocumentById(@PathVariable Long id) {
        return documentService.getDocumentById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
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
