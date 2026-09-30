package com.supportorchestrator.knowledge;

import com.supportorchestrator.TestProperties;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.embedding.onnx.allminilml6v2.AllMiniLmL6V2EmbeddingModel;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Runs the real embedding model over the bundled sample docs (in-memory store instead of pgvector) to check
 * that retrieval finds the right document and that the score thresholds separate relevant from irrelevant.
 */
class KnowledgeBaseRetrieverTest {

    private static KnowledgeBaseRetriever retriever;

    @BeforeAll
    static void indexSampleDocs() throws IOException {
        EmbeddingModel model = new AllMiniLmL6V2EmbeddingModel();
        InMemoryEmbeddingStore<TextSegment> store = new InMemoryEmbeddingStore<>();
        var splitter = DocumentSplitters.recursive(800, 100);
        try (Stream<Path> files = Files.list(Path.of("sample-docs"))) {
            for (Path file : files.filter(f -> f.toString().endsWith(".md")).toList()) {
                Document doc = Document.from(Files.readString(file),
                        Metadata.from(KnowledgeBaseRetriever.SOURCE_KEY, file.getFileName().toString()));
                List<TextSegment> segments = splitter.split(doc);
                store.addAll(model.embedAll(segments).content(), segments);
            }
        }
        retriever = new KnowledgeBaseRetriever(model, store, TestProperties.defaults());
    }

    @Test
    void findsTheDocumentThatAnswersTheQuestion() {
        assertTopSource("I get error 1603 when installing on Windows", "installation-guide.md");
        assertTopSource("How do I get a refund on my annual plan?", "billing-faq.md");
        assertTopSource("I forgot my password", "account-and-security.md");
        assertTopSource("My files are not syncing", "sync-troubleshooting.md");
        assertTopSource("What is the API rate limit?", "api-reference.md");
    }

    @Test
    void unrelatedQuestionsDoNotReachTheAnswerableThreshold() {
        List<RetrievedChunk> chunks = retriever.retrieve("What's a good recipe for banana bread?");
        double best = chunks.stream().mapToDouble(RetrievedChunk::score).max().orElse(0);
        assertThat(best).isLessThan(TestProperties.defaults().retrieval().answerableScore());
    }

    @Test
    void formatsContextWithSourceLabels() {
        String context = KnowledgeBaseRetriever.toPromptContext(List.of(
                new RetrievedChunk("first", "a.md", 0.9), new RetrievedChunk("second", "b.md", 0.8)));
        assertThat(context).isEqualTo("[a.md]\nfirst\n\n---\n\n[b.md]\nsecond");
    }

    private static void assertTopSource(String query, String expectedSource) {
        List<RetrievedChunk> chunks = retriever.retrieve(query);
        assertThat(chunks).as("results for '%s'", query).isNotEmpty();
        assertThat(chunks.getFirst().source()).as("top source for '%s'", query).isEqualTo(expectedSource);
        assertThat(chunks.getFirst().score()).as("score for '%s'", query)
                .isGreaterThanOrEqualTo(TestProperties.defaults().retrieval().answerableScore());
    }
}
