package swkom_dms.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import swkom_dms.service.DocumentService;

@RestController
public class DocumentController {

    @Autowired
    private DocumentService documentService;

    @GetMapping("/documents")
    public ResponseEntity<String> getDocuments() {
        // Delegate the logic to the service
        return new ResponseEntity<>(documentService.getDocumentList(), HttpStatus.OK);
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadDocument() {
        return new ResponseEntity<>(documentService.uploadDocument(), HttpStatus.OK);
    }
}
