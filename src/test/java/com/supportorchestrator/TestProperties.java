package com.supportorchestrator;

import com.supportorchestrator.config.AppProperties;

public final class TestProperties {

    private TestProperties() {
    }

    public static AppProperties defaults() {
        return new AppProperties(
                new AppProperties.Llm(AppProperties.Provider.ANTHROPIC, "test-key", "test-model", "us-east-1", 1000, 30, false),
                new AppProperties.Retrieval(5, 0.55, 0.65),
                new AppProperties.Ingestion("sample-docs", false, 800, 100, 100),
                new AppProperties.Memory(10));
    }
}
