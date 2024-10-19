package swkom_dms.service;

import org.springframework.stereotype.Service;

@Service
public class DocumentService {

    // Hardcoded response for GET request
    public String getDocumentList() {
        return "Hardcoded Document List";
    }

    // Hardcoded logic for POST request
    public String uploadDocument() {
        // In the future, this will handle file storage logic
        return "Document uploaded successfully";
    }
}
