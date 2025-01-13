package com.example.worker.elasticSearch;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.Result;
import co.elastic.clients.elasticsearch.core.*;
import com.example.worker.elasticSearch.entities.DocumentEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.openapitools.jackson.nullable.JsonNullable;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ElasticSearchServiceTest {

    private ElasticsearchClient mockClient;
    private ElasticSearchService elasticSearchService;

    @BeforeEach
    void setUp() {
        mockClient = mock(ElasticsearchClient.class);
        elasticSearchService = new ElasticSearchService(mockClient);
    }

    @Test
    void testIndexDocument_Success() throws IOException {
        // Arrange
        DocumentEntity document = new DocumentEntity();
        document.setId(1L);
        document.setName(JsonNullable.of("Test Title"));
        document.setContent(JsonNullable.of("Test Content"));
        IndexResponse mockResponse = mock(IndexResponse.class);
        when(mockResponse.result()).thenReturn(Result.Created);
        when(mockResponse.id()).thenReturn("1");
        when(mockClient.index(any(IndexRequest.class))).thenReturn(mockResponse);

        // Act
        Result result = elasticSearchService.indexDocument(document);

        // Assert
        assertEquals(Result.Created, result);
        verify(mockClient, times(1)).index(any(IndexRequest.class));
    }

    @Test
    void testIndexDocument_ThrowsException() throws IOException {
        // Arrange
        DocumentEntity document = new DocumentEntity();
        document.setId(1L);
        document.setName(JsonNullable.of("Test Title"));
        document.setContent(JsonNullable.of("Test Content"));
        when(mockClient.index(any(IndexRequest.class))).thenThrow(IOException.class);

        // Act & Assert
        assertThrows(IOException.class, () -> elasticSearchService.indexDocument(document));
        verify(mockClient, times(1)).index(any(IndexRequest.class));
    }

    @Test
    void testGetDocumentById_Found() throws IOException {
        // Arrange
        int documentId = 1;
        DocumentEntity document = new DocumentEntity();
        document.setId(1L);
        document.setName(JsonNullable.of("Test Title"));
        document.setContent(JsonNullable.of("Test Content"));
        GetResponse<DocumentEntity> mockResponse = mock(GetResponse.class);
        when(mockResponse.found()).thenReturn(true);
        when(mockResponse.source()).thenReturn(document);
        when(mockClient.get(any(GetRequest.class), eq(DocumentEntity.class))).thenReturn(mockResponse);

        // Act
        Optional<DocumentEntity> result = elasticSearchService.getDocumentById(documentId);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(document, result.get());
        verify(mockClient, times(1)).get(any(GetRequest.class), eq(DocumentEntity.class));
    }

    @Test
    void testGetDocumentById_NotFound() throws IOException {
        // Arrange
        int documentId = 1;
        GetResponse<DocumentEntity> mockResponse = mock(GetResponse.class);
        when(mockResponse.found()).thenReturn(false);
        when(mockClient.get(any(GetRequest.class), eq(DocumentEntity.class))).thenReturn(mockResponse);

        // Act
        Optional<DocumentEntity> result = elasticSearchService.getDocumentById(documentId);

        // Assert
        assertFalse(result.isPresent());
        verify(mockClient, times(1)).get(any(GetRequest.class), eq(DocumentEntity.class));
    }

    @Test
    void testGetDocumentById_ThrowsException() throws IOException {
        // Arrange
        int documentId = 1;
        when(mockClient.get(any(GetRequest.class), eq(DocumentEntity.class))).thenThrow(IOException.class);

        // Act
        Optional<DocumentEntity> result = elasticSearchService.getDocumentById(documentId);

        // Assert
        assertFalse(result.isPresent());
        verify(mockClient, times(1)).get(any(GetRequest.class), eq(DocumentEntity.class));
    }

    @Test
    void testDeleteDocumentById_Success() throws IOException {
        // Arrange
        int documentId = 1;
        DeleteResponse mockResponse = mock(DeleteResponse.class);
        when(mockResponse.result()).thenReturn(Result.Deleted);
        when(mockClient.delete(any(DeleteRequest.class))).thenReturn(mockResponse);

        // Act
        boolean result = elasticSearchService.deleteDocumentById(documentId);

        // Assert
        assertTrue(result);
        verify(mockClient, times(1)).delete(any(DeleteRequest.class));
    }

    @Test
    void testDeleteDocumentById_NotFound() throws IOException {
        // Arrange
        int documentId = 1;
        DeleteResponse mockResponse = mock(DeleteResponse.class);
        when(mockResponse.result()).thenReturn(Result.NotFound);
        when(mockClient.delete(any(DeleteRequest.class))).thenReturn(mockResponse);

        // Act
        boolean result = elasticSearchService.deleteDocumentById(documentId);

        // Assert
        assertFalse(result);
        verify(mockClient, times(1)).delete(any(DeleteRequest.class));
    }

    @Test
    void testDeleteDocumentById_ThrowsException() throws IOException {
        // Arrange
        int documentId = 1;
        when(mockClient.delete(any(DeleteRequest.class))).thenThrow(IOException.class);

        // Act
        boolean result = elasticSearchService.deleteDocumentById(documentId);

        // Assert
        assertFalse(result);
        verify(mockClient, times(1)).delete(any(DeleteRequest.class));
    }
}
