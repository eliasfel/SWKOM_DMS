package com.example.worker.elasticSearch;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.Result;
import co.elastic.clients.elasticsearch.core.DeleteRequest;
import co.elastic.clients.elasticsearch.core.DeleteResponse;
import co.elastic.clients.elasticsearch.core.GetRequest;
import co.elastic.clients.elasticsearch.core.GetResponse;
import co.elastic.clients.elasticsearch.core.IndexRequest;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import com.example.worker.elasticSearch.entities.DocumentEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Optional;

@Service
@Slf4j
public class ElasticSearchService implements SearchIndexService {

    private final ElasticsearchClient elasticsearchClient;

    public ElasticSearchService(ElasticsearchClient elasticsearchClient) {
        this.elasticsearchClient = elasticsearchClient;
    }

    @Override
    public Result indexDocument(DocumentEntity document) throws IOException {
        try {
            // Build an index request
            IndexRequest<DocumentEntity> request = IndexRequest.of(builder -> builder
                    .index(ElasticSearchConfig.DOCUMENTS_INDEX_NAME)
                    .id(String.valueOf(document.getId()))
                    .document(document)
            );

            // Execute the request
            IndexResponse response = elasticsearchClient.index(request);

            // Log and return the result
            log.info("Document indexed with ID: {}, Result: {}", response.id(), response.result());
            return response.result();
        } catch (IOException e) {
            log.error("Error indexing document with ID: {}", document.getId(), e);
            throw e;
        }
    }

    @Override
    public Optional<DocumentEntity> getDocumentById(int id) {
        try {
            // Build a get request
            GetRequest request = GetRequest.of(builder -> builder
                    .index(ElasticSearchConfig.DOCUMENTS_INDEX_NAME)
                    .id(String.valueOf(id))
            );

            // Execute the request
            GetResponse<DocumentEntity> response = elasticsearchClient.get(request, DocumentEntity.class);

            // Return the document if it exists
            if (response.found()) {
                log.info("Document retrieved with ID: {}", id);
                return Optional.of(response.source());
            } else {
                log.warn("Document with ID: {} not found", id);
                return Optional.empty();
            }
        } catch (IOException e) {
            log.error("Error retrieving document with ID: {}", id, e);
            return Optional.empty();
        }
    }

    @Override
    public boolean deleteDocumentById(int id) {
        try {
            // Build a delete request
            DeleteRequest request = DeleteRequest.of(builder -> builder
                    .index(ElasticSearchConfig.DOCUMENTS_INDEX_NAME)
                    .id(String.valueOf(id))
            );

            // Execute the request
            DeleteResponse response = elasticsearchClient.delete(request);

            // Check the result
            if (response.result() == Result.Deleted) {
                log.info("Document deleted with ID: {}", id);
                return true;
            } else {
                log.warn("Document with ID: {} not found or could not be deleted", id);
                return false;
            }
        } catch (IOException e) {
            log.error("Error deleting document with ID: {}", id, e);
            return false;
        }
    }
}
