package swkom_dms;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import swkom_dms.DAL.entities.DocumentEntity;
import swkom_dms.DAL.repositories.DocumentRepository;
import swkom_dms.DTO.DocumentDTO;
import swkom_dms.mappers.DocumentMapper;
import swkom_dms.service.DocumentService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DocumentServiceTest {

    private DocumentRepository documentRepository;
    private DocumentMapper documentMapper;
    private DocumentService documentService;

    @BeforeEach
    void setUp() {
        documentRepository = mock(DocumentRepository.class);
        documentMapper = mock(DocumentMapper.class);
        documentService = new DocumentService(documentRepository, documentMapper);
    }

    @Test
    void testUploadDocument_Success() {
        DocumentDTO documentDTO = new DocumentDTO();
        documentDTO.setName("Test Document");

        DocumentEntity documentEntity = new DocumentEntity();
        documentEntity.setId(1L);
        documentEntity.setName("Test Document");

        when(documentMapper.toEntity(documentDTO)).thenReturn(documentEntity);
        when(documentRepository.save(documentEntity)).thenReturn(documentEntity);

        assertDoesNotThrow(() -> documentService.uploadDocument(documentDTO));
        verify(documentRepository, times(1)).save(documentEntity);
    }

    @Test
    void testUploadDocument_Failure() {
        DocumentDTO documentDTO = new DocumentDTO();
        documentDTO.setName("Test Document");

        when(documentMapper.toEntity(documentDTO)).thenThrow(new RuntimeException("Error during mapping"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> documentService.uploadDocument(documentDTO));
        assertEquals("Error during mapping", exception.getMessage());

        verify(documentRepository, never()).save(any());
    }

    @Test
    void testGetDocumentById_Success() {
        DocumentEntity entity = new DocumentEntity();
        entity.setId(1L);
        entity.setName("Test Document");

        when(documentRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(documentMapper.toDTO(entity)).thenReturn(new DocumentDTO());

        Optional<DocumentDTO> dto = documentService.getDocumentById(1L);

        assertTrue(dto.isPresent());
        verify(documentRepository, times(1)).findById(1L);
    }

    @Test
    void testGetDocumentById_Failure() {
        when(documentRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<DocumentDTO> dto = documentService.getDocumentById(99L);

        assertFalse(dto.isPresent());
        verify(documentRepository, times(1)).findById(99L);
    }

    @Test
    void testGetDocumentList_Success() {
        DocumentEntity entity = new DocumentEntity();
        entity.setId(1L);
        entity.setName("Test Document");

        when(documentRepository.findAll()).thenReturn(List.of(entity));
        when(documentMapper.toDTO(entity)).thenReturn(new DocumentDTO());

        assertDoesNotThrow(() -> {
            assertEquals(1, documentService.getDocumentList().size());
        });

        verify(documentRepository, times(1)).findAll();
    }

    @Test
    void testGetDocumentList_Failure() {
        when(documentRepository.findAll()).thenThrow(new RuntimeException("Database error"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> documentService.getDocumentList());
        assertEquals("Database error", exception.getMessage());

        verify(documentRepository, times(1)).findAll();
    }
}
