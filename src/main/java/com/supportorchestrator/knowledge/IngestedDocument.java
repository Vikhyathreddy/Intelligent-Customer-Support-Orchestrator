package com.supportorchestrator.knowledge;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;

/**
 * Tracks which files are already embedded, so re-running ingestion only processes new or changed files.
 */
@Entity
@Table(name = "ingested_document")
public class IngestedDocument {

    @Id
    private String source;
    private String contentHash;
    private int chunkCount;
    private Instant ingestedAt;

    protected IngestedDocument() {
    }

    public IngestedDocument(String source, String contentHash, int chunkCount) {
        this.source = source;
        this.contentHash = contentHash;
        this.chunkCount = chunkCount;
        this.ingestedAt = Instant.now();
    }

    public String getSource() {
        return source;
    }

    public String getContentHash() {
        return contentHash;
    }

    public int getChunkCount() {
        return chunkCount;
    }

    public Instant getIngestedAt() {
        return ingestedAt;
    }
}
