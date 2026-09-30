CREATE EXTENSION IF NOT EXISTS vector;

CREATE TABLE support_ticket (
    id                  BIGSERIAL PRIMARY KEY,
    session_id          VARCHAR(128) NOT NULL,
    customer_id         VARCHAR(128),
    question            TEXT         NOT NULL,
    answer              TEXT,
    category            VARCHAR(32),
    urgency             VARCHAR(16),
    summary             VARCHAR(1000),
    status              VARCHAR(32)  NOT NULL,
    escalation_reason   VARCHAR(64),
    top_retrieval_score DOUBLE PRECISION,
    sources             VARCHAR(2000),
    latency_ms          BIGINT,
    resolution          TEXT,
    created_at          TIMESTAMP WITH TIME ZONE NOT NULL,
    resolved_at         TIMESTAMP WITH TIME ZONE
);

CREATE INDEX idx_ticket_status_created ON support_ticket (status, created_at DESC);
CREATE INDEX idx_ticket_session_created ON support_ticket (session_id, created_at DESC);

CREATE TABLE chat_memory (
    session_id    VARCHAR(128) PRIMARY KEY,
    messages_json TEXT NOT NULL,
    updated_at    TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE TABLE ingested_document (
    source       VARCHAR(1000) PRIMARY KEY,
    content_hash VARCHAR(64) NOT NULL,
    chunk_count  INT         NOT NULL,
    ingested_at  TIMESTAMP WITH TIME ZONE NOT NULL
);

-- Knowledge-base chunks. Column layout matches LangChain4j's PgVectorEmbeddingStore; 384 = all-MiniLM-L6-v2.
-- HNSW (not IVFFlat) because it needs no training data, so it stays accurate while documents are added
-- incrementally.
CREATE TABLE kb_embeddings (
    embedding_id UUID PRIMARY KEY,
    embedding    vector(384),
    text         TEXT,
    metadata     JSON
);

CREATE INDEX kb_embeddings_hnsw_index ON kb_embeddings USING hnsw (embedding vector_cosine_ops);
CREATE INDEX kb_embeddings_source_index ON kb_embeddings ((metadata ->> 'source'));
