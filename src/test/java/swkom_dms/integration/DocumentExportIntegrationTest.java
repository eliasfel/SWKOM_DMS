package swkom_dms.integration;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import swkom_dms.controller.DocumentController;
import swkom_dms.DAL.entities.DocumentEntity;
import swkom_dms.DAL.repositories.DocumentRepository;
import swkom_dms.DTO.DocumentDTO;
import swkom_dms.mappers.DocumentMapper;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
public class DocumentExportIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private DocumentMapper documentMapper;

    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    // Setup mock data for testing the export functionality
    private void createMockDocuments() {
        DocumentEntity doc1 = new DocumentEntity();
        doc1.setName("Document 1");
        doc1.setDateUploaded(java.time.LocalDateTime.now());
        documentRepository.save(doc1);

        DocumentEntity doc2 = new DocumentEntity();
        doc2.setName("Document 2");
        doc2.setDateUploaded(java.time.LocalDateTime.now());
        documentRepository.save(doc2);
    }

    @Test
    void testExportDocuments() throws Exception {
        // Setup mock data
        createMockDocuments();

        // Send GET request to the export API
        var result = mockMvc.perform(get("http://localhost:8081/api/documents/export"))
                .andExpect(status().isOk())  // Expect status 200 OK
                .andReturn();

        // Extract the CSV content from the response
        byte[] csvContent = result.getResponse().getContentAsByteArray();
        String csvString = new String(csvContent);

        // Check if the CSV contains the expected content
        assertEquals(true, csvString.contains("\"ID\",\"Name\",\"Upload Date\""));
        assertEquals(true, csvString.contains("\"Document 1\""));
        assertEquals(true, csvString.contains("\"Document 2\""));
    }
}
