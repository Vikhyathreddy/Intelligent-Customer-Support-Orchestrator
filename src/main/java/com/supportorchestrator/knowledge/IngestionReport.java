package com.supportorchestrator.knowledge;

import java.util.List;

public record IngestionReport(int filesScanned, int ingested, int unchanged, int failed, int chunksCreated,
                              long durationMs, List<String> errors) {
}
