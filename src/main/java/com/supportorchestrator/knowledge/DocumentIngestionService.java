package com.supportorchestrator.knowledge;

import com.supportorchestrator.config.AppProperties;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentParser;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.parser.apache.tika.ApacheTikaDocumentParser;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static dev.langchain4j.store.embedding.filter.MetadataFilterBuilder.metadataKey;

/**
 * Loads documents (Markdown, text, HTML, PDF, Word, ...) from disk, splits them into overlapping chunks,
 * embeds them and stores them in pgvector. Work is done in batches and files are fingerprinted, so a
 * corpus of thousands of documents can be re-synced cheaply.
 */
@Service
public class DocumentIngestionService {

    private static final Logger log = LoggerFactory.getLogger(DocumentIngestionService.class);
    private static final Set<String> SUPPORTED_EXTENSIONS =
            Set.of("md", "txt", "html", "htm", "pdf", "docx", "doc", "pptx", "rtf", "adoc", "json", "csv");

    private final EmbeddingModel embeddingModel;
    private final EmbeddingStore<TextSegment> embeddingStore;
    private final IngestedDocumentRepository ingestedDocuments;
    private final AppProperties.Ingestion config;
    private final DocumentParser parser = new ApacheTikaDocumentParser();
    private final DocumentSplitter splitter;

    public DocumentIngestionService(EmbeddingModel embeddingModel, EmbeddingStore<TextSegment> embeddingStore,
                                    IngestedDocumentRepository ingestedDocuments, AppProperties props) {
        this.embeddingModel = embeddingModel;
        this.embeddingStore = embeddingStore;
        this.ingestedDocuments = ingestedDocuments;
        this.config = props.ingestion();
        this.splitter = DocumentSplitters.recursive(config.chunkSize(), config.chunkOverlap());
    }

    public IngestionReport ingestConfiguredDirectory() {
        return ingestDirectory(Path.of(config.docsPath()));
    }

    public synchronized IngestionReport ingestDirectory(Path root) {
        long start = System.currentTimeMillis();
        List<Path> files;
        try (Stream<Path> walk = Files.walk(root)) {
            files = walk.filter(Files::isRegularFile).filter(DocumentIngestionService::isSupported).sorted().toList();
        } catch (IOException e) {
            throw new IllegalStateException("Cannot read documents directory " + root.toAbsolutePath(), e);
        }

        int ingested = 0;
        int unchanged = 0;
        int chunks = 0;
        List<String> errors = new ArrayList<>();
        List<PendingDocument> batch = new ArrayList<>();

        for (Path file : files) {
            String source = root.relativize(file).toString().replace('\\', '/');
            try {
                String hash = sha256(file);
                boolean isUnchanged = ingestedDocuments.findById(source)
                        .map(existing -> existing.getContentHash().equals(hash))
                        .orElse(false);
                if (isUnchanged) {
                    unchanged++;
                    continue;
                }
                batch.add(new PendingDocument(source, hash, parse(file, source)));
            } catch (Exception e) {
                log.warn("Skipping {}: {}", source, e.getMessage());
                errors.add(source + ": " + e.getMessage());
            }
            if (batch.size() >= config.batchSize()) {
                chunks += flush(batch);
                ingested += batch.size();
                batch.clear();
            }
        }
        chunks += flush(batch);
        ingested += batch.size();

        IngestionReport report = new IngestionReport(files.size(), ingested, unchanged, errors.size(), chunks,
                System.currentTimeMillis() - start, errors);
        log.info("Ingestion finished: {}", report);
        return report;
    }

    private int flush(List<PendingDocument> batch) {
        if (batch.isEmpty()) {
            return 0;
        }
        int total = 0;
        for (PendingDocument pending : batch) {
            // Replace any previous version of this file so stale chunks never reach the model
            embeddingStore.removeAll(metadataKey(KnowledgeBaseRetriever.SOURCE_KEY).isEqualTo(pending.source()));
            List<TextSegment> segments = splitter.split(pending.document());
            if (!segments.isEmpty()) {
                List<Embedding> embeddings = embeddingModel.embedAll(segments).content();
                embeddingStore.addAll(embeddings, segments);
            }
            ingestedDocuments.save(new IngestedDocument(pending.source(), pending.hash(), segments.size()));
            total += segments.size();
        }
        log.info("Embedded batch of {} documents ({} chunks)", batch.size(), total);
        return total;
    }

    private Document parse(Path file, String source) throws IOException {
        Document parsed;
        try (InputStream in = Files.newInputStream(file)) {
            parsed = parser.parse(in);
        }
        parsed.metadata().put(KnowledgeBaseRetriever.SOURCE_KEY, source);
        return parsed;
    }

    static boolean isSupported(Path file) {
        String name = file.getFileName().toString();
        int dot = name.lastIndexOf('.');
        return dot > 0 && SUPPORTED_EXTENSIONS.contains(name.substring(dot + 1).toLowerCase());
    }

    private static String sha256(Path file) throws IOException {
        try {
            return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(Files.readAllBytes(file)));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }

    private record PendingDocument(String source, String hash, Document document) {
    }
}
