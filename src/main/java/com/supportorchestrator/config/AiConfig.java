package com.supportorchestrator.config;

import com.supportorchestrator.agent.SupportAgent;
import com.supportorchestrator.agent.TicketClassifier;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.memory.chat.ChatMemoryProvider;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.anthropic.AnthropicChatModel;
import dev.langchain4j.model.bedrock.BedrockChatModel;
import dev.langchain4j.model.bedrock.BedrockChatRequestParameters;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.embedding.onnx.allminilml6v2.AllMiniLmL6V2EmbeddingModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.pgvector.PgVectorEmbeddingStore;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import software.amazon.awssdk.regions.Region;

import javax.sql.DataSource;
import java.time.Duration;

@Configuration
public class AiConfig {

    private static final Logger log = LoggerFactory.getLogger(AiConfig.class);

    /**
     * Runs in-process (ONNX), so embedding thousands of documents costs nothing and needs no API key.
     */
    @Bean
    EmbeddingModel embeddingModel() {
        return new AllMiniLmL6V2EmbeddingModel();
    }

    @Bean
    EmbeddingStore<TextSegment> embeddingStore(DataSource dataSource, EmbeddingModel embeddingModel) {
        return PgVectorEmbeddingStore.datasourceBuilder()
                .datasource(dataSource)
                .table("kb_embeddings")
                .dimension(embeddingModel.dimension())
                // Table and HNSW index are created by Flyway (V1__init.sql)
                .createTable(false)
                .useIndex(false)
                .build();
    }

    @Bean
    ChatModel chatModel(AppProperties props) {
        AppProperties.Llm llm = props.llm();
        log.info("Using LLM provider {} with model {}", llm.provider(), llm.model());
        return switch (llm.provider()) {
            case BEDROCK -> bedrockChatModel(llm);
            case ANTHROPIC -> anthropicChatModel(llm);
        };
    }

    /**
     * Claude through Amazon Bedrock. Credentials come from the default AWS chain (environment variables,
     * {@code ~/.aws/credentials} from {@code aws configure}, SSO, or an instance role), never from this project.
     */
    private static ChatModel bedrockChatModel(AppProperties.Llm llm) {
        return BedrockChatModel.builder()
                .region(Region.of(llm.region()))
                .modelId(llm.model())
                .defaultRequestParameters(BedrockChatRequestParameters.builder()
                        .maxOutputTokens(llm.maxTokens())
                        .build())
                .timeout(Duration.ofSeconds(llm.timeoutSeconds()))
                .maxRetries(2)
                .logRequests(llm.logRequests())
                .logResponses(llm.logRequests())
                .build();
    }

    private static ChatModel anthropicChatModel(AppProperties.Llm llm) {
        return AnthropicChatModel.builder()
                .apiKey(llm.apiKey())
                .modelName(llm.model())
                .maxTokens(llm.maxTokens())
                .timeout(Duration.ofSeconds(llm.timeoutSeconds()))
                .cacheSystemMessages(true)
                .maxRetries(2)
                .logRequests(llm.logRequests())
                .logResponses(llm.logRequests())
                .build();
    }

    @Bean
    ChatMemoryProvider chatMemoryProvider(ChatMemoryStore store, AppProperties props) {
        return sessionId -> MessageWindowChatMemory.builder()
                .id(sessionId)
                .maxMessages(props.memory().maxMessages())
                .chatMemoryStore(store)
                .build();
    }

    @Bean
    SupportAgent supportAgent(ChatModel chatModel, ChatMemoryProvider chatMemoryProvider) {
        return AiServices.builder(SupportAgent.class)
                .chatModel(chatModel)
                .chatMemoryProvider(chatMemoryProvider)
                .build();
    }

    @Bean
    TicketClassifier ticketClassifier(ChatModel chatModel) {
        return AiServices.create(TicketClassifier.class, chatModel);
    }
}
