package swkom_dms;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import swkom_dms.DAL.entities.DocumentEntity;
import swkom_dms.DAL.repositories.DocumentRepository;
import swkom_dms.DTO.DocumentDTO;
import swkom_dms.mappers.DocumentMapper;
import swkom_dms.service.DocumentService;
import swkom_dms.service.EchoService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DocumentServiceTest {

    private DocumentRepository documentRepository;
    private DocumentMapper documentMapper;
    private DocumentService documentService;
    private EchoService echoService;

    @BeforeEach
    void setUp() {
        documentRepository = mock(DocumentRepository.class);
        documentMapper = mock(DocumentMapper.class);
        echoService = mock(EchoService.class);
        documentService = new DocumentService(documentRepository, documentMapper, echoService);
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

    @Test
    void testGenerateExportFile_Success() {
        DocumentEntity doc1 = new DocumentEntity();
        doc1.setId(1L);
        doc1.setName("Document 1");
        doc1.setDateUploaded(LocalDateTime.of(2023, 1, 1, 10, 0));

        DocumentEntity doc2 = new DocumentEntity();
        doc2.setId(2L);
        doc2.setName("Document 2");
        doc2.setDateUploaded(LocalDateTime.of(2023, 2, 1, 11, 30));

        when(documentRepository.findAll()).thenReturn(List.of(doc1, doc2));

        byte[] csvBytes = documentService.generateExportFile();
        String csvContent = new String(csvBytes, StandardCharsets.UTF_8);

        // Verify the CSV content
        String expectedHeader = "\"ID\",\"Name\",\"Upload Date\"";
        String expectedRow1 = "\"1\",\"Document 1\",\"2023-01-01T10:00\"";
        String expectedRow2 = "\"2\",\"Document 2\",\"2023-02-01T11:30\"";

        assertTrue(csvContent.contains(expectedHeader));
        assertTrue(csvContent.contains(expectedRow1));
        assertTrue(csvContent.contains(expectedRow2));

        verify(documentRepository, times(1)).findAll();
    }

    @Test
    void testGenerateExportFile_Exception() {
        when(documentRepository.findAll()).thenThrow(new RuntimeException("Error while generating CSV"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> documentService.generateExportFile());
        assertEquals("Error while generating CSV", exception.getMessage());

        verify(documentRepository, times(1)).findAll();
    }

}
