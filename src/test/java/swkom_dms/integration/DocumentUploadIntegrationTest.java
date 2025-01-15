package swkom_dms.integration;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import swkom_dms.service.DocumentService;
import swkom_dms.minIO.FileStorage;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class DocumentUploadIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DocumentService documentService;

    @MockBean
    private FileStorage fileStorage;

    @Test
    void testDocumentUpload() throws Exception {
        // Prepare mock data
        MockMultipartFile mockFile = new MockMultipartFile(
                "file",
                "test_document.pdf",
                MediaType.APPLICATION_PDF_VALUE,
                "Test content".getBytes()
        );

        String documentName = "test_document";
        Mockito.doNothing().when(fileStorage).upload(Mockito.anyString(), Mockito.any());
        Mockito.doNothing().when(documentService).uploadDocument(Mockito.any());

        // Perform the upload request
        mockMvc.perform(multipart("/api/upload")
                        .file(mockFile)
                        .param("name", documentName)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk()); // Verify status is 200 OK

        // Verify interactions with the mocked services
        Mockito.verify(fileStorage).upload(Mockito.eq("test_document.pdf"), Mockito.any());
        Mockito.verify(documentService).uploadDocument(Mockito.any());
    }
}
