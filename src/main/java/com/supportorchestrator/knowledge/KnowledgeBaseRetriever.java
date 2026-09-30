package com.supportorchestrator.knowledge;

import com.supportorchestrator.config.AppProperties;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingSearchRequest;
import dev.langchain4j.store.embedding.EmbeddingStore;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Semantic search over the knowledge base stored in pgvector.
 */
@Component
public class KnowledgeBaseRetriever {

    static final String SOURCE_KEY = "source";

    private final EmbeddingModel embeddingModel;
    private final EmbeddingStore<TextSegment> embeddingStore;
    private final AppProperties.Retrieval config;

    public KnowledgeBaseRetriever(EmbeddingModel embeddingModel, EmbeddingStore<TextSegment> embeddingStore,
                                  AppProperties props) {
        this.embeddingModel = embeddingModel;
        this.embeddingStore = embeddingStore;
        this.config = props.retrieval();
    }

    /** Returns the most relevant chunks, best first. */
    public List<RetrievedChunk> retrieve(String query) {
        Embedding queryEmbedding = embeddingModel.embed(query).content();
        EmbeddingSearchRequest request = EmbeddingSearchRequest.builder()
                .queryEmbedding(queryEmbedding)
                .maxResults(config.maxResults())
                .minScore(config.minScore())
                .build();
        return embeddingStore.search(request).matches().stream()
                .map(match -> new RetrievedChunk(
                        match.embedded().text(),
                        match.embedded().metadata().getString(SOURCE_KEY),
                        match.score()))
                .toList();
    }

    /** Formats chunks as the context block that is injected into the agent prompt. */
    public static String toPromptContext(List<RetrievedChunk> chunks) {
        return chunks.stream()
                .map(chunk -> "[" + chunk.source() + "]\n" + chunk.text())
                .collect(Collectors.joining("\n\n---\n\n"));
    }
}
