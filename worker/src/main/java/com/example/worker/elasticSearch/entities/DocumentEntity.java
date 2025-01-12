package com.example.worker.elasticSearch.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import org.openapitools.jackson.nullable.JsonNullable;
import org.openapitools.jackson.nullable.JsonNullableModule;
import java.time.LocalDateTime;

@Entity
public class DocumentEntity {
    @Id
    private Long id;
    private JsonNullable<String> name = JsonNullable.undefined();

    private JsonNullable<String> content = JsonNullable.undefined();


    public DocumentEntity id(Long id) {
        this.id = id;
        return this;
    }

    public DocumentEntity name(String name) {
        this.name = JsonNullable.of(name);
        return this;
    }

    public DocumentEntity content(String content) {
        this.content = JsonNullable.of(content);
        return this;
    }


    @Schema(name = "id", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @JsonProperty("id")
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }


    @Schema(name = "name", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @JsonProperty("name")
    public JsonNullable<String> getName() {
        return name;
    }
    public void setName(JsonNullable<String> name) {
        this.name = name;
    }

    @Schema(name = "content", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @JsonProperty("content")
    public JsonNullable<String> getContent() {
        return content;
    }

    public void setContent(JsonNullable<String> content) {
        this.content = content;
    }
}

