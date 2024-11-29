package swkom_dms.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import swkom_dms.DAL.entities.DocumentEntity;
import swkom_dms.DAL.repositories.DocumentRepository;
import swkom_dms.DTO.DocumentDTO;
import swkom_dms.mappers.DocumentMapper;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final DocumentMapper documentMapper;
    @Autowired
    public DocumentService(DocumentRepository documentRepository, DocumentMapper documentMapper)
    {
        this.documentRepository = documentRepository;
        this.documentMapper = documentMapper;
    }

    // Fetch all documents as DTOs
    public List<DocumentDTO> getDocumentList() {
        List<DocumentEntity> entities = documentRepository.findAll();
        return entities.stream()
                .map(documentMapper::toDTO) // Map Entity to DTO
                .collect(Collectors.toList());
    }

    // Save a new document using DTO
    public void uploadDocument(@Valid DocumentDTO documentDTO) {
        // Map DTO to Entity
        DocumentEntity documentEntity = documentMapper.toEntity(documentDTO);

        // Save to database
        documentRepository.save(documentEntity);

    }

    // Find a document by ID and return as DTO
    public Optional<DocumentDTO> getDocumentById(Long id) {
        Optional<DocumentEntity> entity = documentRepository.findById(id);
        return entity.map(documentMapper::toDTO); // Convert Entity to DTO if present
    }

    public boolean updateDocument(@Valid DocumentDTO documentDTO) {

        DocumentEntity documentEntity = documentMapper.toEntity(documentDTO);
        if (documentRepository.existsById(documentEntity.getId())) {
            try {
                documentRepository.save(documentEntity);
                return true;
            } catch (RuntimeException e) {
                throw e;
            }
        }
        return false;
    }

    // Delete a document by ID
    public boolean deleteDocumentById(Long id) {
        if (documentRepository.existsById(id)) {
            documentRepository.deleteById(id);
            return true;
        } else {
            return false;
        }
    }
}
