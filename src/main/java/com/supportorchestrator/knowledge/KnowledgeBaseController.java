package com.supportorchestrator.knowledge;

import com.supportorchestrator.config.AppProperties;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/knowledge-base")
public class KnowledgeBaseController {

    private final DocumentIngestionService ingestionService;
    private final KnowledgeBaseRetriever retriever;
    private final IngestedDocumentRepository ingestedDocuments;
    private final Path docsPath;

    public KnowledgeBaseController(DocumentIngestionService ingestionService, KnowledgeBaseRetriever retriever,
                                   IngestedDocumentRepository ingestedDocuments, AppProperties props) {
        this.ingestionService = ingestionService;
        this.retriever = retriever;
        this.ingestedDocuments = ingestedDocuments;
        this.docsPath = Path.of(props.ingestion().docsPath());
    }

    /** Re-syncs the documents directory: new and changed files are embedded, unchanged ones are skipped. */
    @PostMapping("/sync")
    public IngestionReport sync() {
        return ingestionService.ingestConfiguredDirectory();
    }

    /** Adds one or more files to the documents directory and embeds them. */
    @PostMapping("/documents")
    public IngestionReport upload(@RequestParam("files") List<MultipartFile> files) throws IOException {
        Path uploads = docsPath.resolve("uploads");
        Files.createDirectories(uploads);
        for (MultipartFile file : files) {
            String name = Path.of(String.valueOf(file.getOriginalFilename())).getFileName().toString();
            Path target = uploads.resolve(name);
            if (!DocumentIngestionService.isSupported(target)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unsupported file type: " + name);
            }
            file.transferTo(target);
        }
        return ingestionService.ingestConfiguredDirectory();
    }

    /** Debug endpoint: shows what the agent would see as context for a query. */
    @GetMapping("/search")
    public List<RetrievedChunk> search(@RequestParam("q") String query) {
        return retriever.retrieve(query);
    }

    @GetMapping("/stats")
    public Map<String, Long> stats() {
        return Map.of("documents", ingestedDocuments.count(), "chunks", ingestedDocuments.totalChunks());
    }
}
