package com.supportorchestrator.knowledge;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "app.ingestion.on-startup", havingValue = "true")
class StartupIngestion {

    private final DocumentIngestionService ingestionService;

    StartupIngestion(DocumentIngestionService ingestionService) {
        this.ingestionService = ingestionService;
    }

    @EventListener(ApplicationReadyEvent.class)
    void ingest() {
        ingestionService.ingestConfiguredDirectory();
    }
}
