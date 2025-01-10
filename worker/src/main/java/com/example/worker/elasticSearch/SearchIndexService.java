package com.example.worker.elasticSearch;

import co.elastic.clients.elasticsearch._types.Result;
import com.example.worker.elasticSearch.entities.DocumentEntity;

import java.io.IOException;
import java.util.Optional;

public interface SearchIndexService {
    Result indexDocument(DocumentEntity document) throws IOException;

    Optional<DocumentEntity> getDocumentById(int id);

    boolean deleteDocumentById(int id);
}
