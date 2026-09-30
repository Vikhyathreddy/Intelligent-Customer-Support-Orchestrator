package com.supportorchestrator.knowledge;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface IngestedDocumentRepository extends JpaRepository<IngestedDocument, String> {

    @Query("select coalesce(sum(d.chunkCount), 0) from IngestedDocument d")
    long totalChunks();
}
