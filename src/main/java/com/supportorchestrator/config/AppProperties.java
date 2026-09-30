package com.supportorchestrator.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * All tunables for the orchestrator, bound from the {@code app.*} namespace in application.yml.
 */
@ConfigurationProperties(prefix = "app")
public record AppProperties(Llm llm, Retrieval retrieval, Ingestion ingestion, Memory memory) {

    /**
     * @param provider which LLM service to call: {@code bedrock} (Claude via AWS, uses the standard AWS
     *                 credential chain) or {@code anthropic} (Claude via the Anthropic API, uses {@code apiKey})
     * @param model    model ID for the chosen provider; Bedrock IDs look like {@code anthropic.claude-...} or,
     *                 for cross-region inference profiles, {@code us.anthropic.claude-...}
     * @param region   AWS region for Bedrock; ignored by other providers
     */
    public record Llm(Provider provider, String apiKey, String model, String region, Integer maxTokens,
                      Integer timeoutSeconds, Boolean logRequests) {
    }

    public enum Provider {
        BEDROCK, ANTHROPIC
    }

    /**
     * @param maxResults    number of chunks handed to the model as context
     * @param minScore      cosine similarity below which a chunk is discarded as irrelevant
     * @param answerableScore best-chunk score required before the agent is allowed to answer on its own;
     *                      anything below is escalated to a human without spending an LLM call
     */
    public record Retrieval(Integer maxResults, Double minScore, Double answerableScore) {
    }

    /**
     * @param docsPath   directory scanned for knowledge-base documents
     * @param onStartup  ingest {@code docsPath} when the application boots
     * @param chunkSize  maximum characters per chunk
     * @param chunkOverlap characters shared between neighbouring chunks
     * @param batchSize  documents embedded per batch (bounds memory use on large corpora)
     */
    public record Ingestion(String docsPath, Boolean onStartup, Integer chunkSize, Integer chunkOverlap,
                            Integer batchSize) {
    }

    public record Memory(Integer maxMessages) {
    }
}
