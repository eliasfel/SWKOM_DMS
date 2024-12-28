package swkom_dms;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import swkom_dms.DAL.entities.DocumentEntity;
import swkom_dms.DTO.DocumentDTO;
import swkom_dms.mappers.DocumentMapper;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import java.util.Arrays;

class DocumentMapperTest {

    private final DocumentMapper mapper = Mappers.getMapper(DocumentMapper.class);

    @Test
    void testToDTO() {
        DocumentEntity entity = new DocumentEntity();
        entity.setId(1L);
        entity.setName("Test Document");
        entity.setContent("Sample Content".getBytes()); // Set byte[] content
        entity.setDateUploaded(LocalDateTime.now());

        DocumentDTO dto = mapper.toDTO(entity);
        assertEquals(entity.getId(), dto.getId());
        assertEquals(entity.getName(), dto.getName());
        assertTrue(Arrays.equals(entity.getContent(), dto.getContent())); // Compare byte arrays
        assertEquals(entity.getDateUploaded(), dto.getDateUploaded());
    }

    @Test
    void testToEntity() {
        DocumentDTO dto = new DocumentDTO();
        dto.setId(1L);
        dto.setName("Test Document");
        dto.setContent("Sample Content".getBytes()); // Set byte[] content
        dto.setDateUploaded(LocalDateTime.now());

        DocumentEntity entity = mapper.toEntity(dto);
        assertEquals(dto.getId(), entity.getId());
        assertEquals(dto.getName(), entity.getName());
        assertTrue(Arrays.equals(dto.getContent(), entity.getContent())); // Compare byte arrays
        assertEquals(dto.getDateUploaded(), entity.getDateUploaded());
    }
}
