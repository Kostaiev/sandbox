# SimpleRagFactsApi

Learning Spring Boot app that builds a Retrieval-Augmented Generation (RAG) API with Spring AI, Qdrant, and OpenAI. The service answers fact questions by mixing a vector store, chat memory, caching, and an automatic translation + rewrite step so the model always receives a clean, helpful prompt.

---

## Tech stack
- Java 21
- Spring Boot 3.5.x
- Spring AI (chat client, RAG advisor, vector store, Tika reader)
- OpenAI GPT-4o mini
- Qdrant vector database (Docker Compose)
- Spring Cache + Scheduling + Retry
- JDBC Chat Memory Repository + H2 file DB
- Maven
- Lombok

---

## Modules / Setup

### 1) Document ingestion
`DataLoader` reads `data.txt` and `data.pdf` from `src/main/resources` right after the app starts. Files are parsed with `TikaDocumentReader`, split into 50-token chunks (min 100 chars, min 200 chars to embed, keep separators), and stored inside Qdrant. A retry wrapper keeps the ingestion alive if Qdrant is still booting.

### 2) Chat client configuration
`ChatClientConfig` wires:
- `MessageWindowChatMemory` (last 8 messages) with the JDBC repo backed by H2
- `RetrievalAugmentationAdvisor` that runs two query transformers: translate any request to English and rewrite it for better search terms
- `VectorStoreDocumentRetriever` (topK 5, similarity 0.2) to fetch context from Qdrant
- `SimpleLoggerAdvisor` for prompt logging
- `ChatClient` built on `OpenAiChatModel` so every call automatically adds memory + retrieved facts

### 3) REST API layer
`RagController` exposes `GET /api/facts?ask=`. Each call sets the `ChatMemory.CONVERSATION_ID` (based on the `SESSION_ID` cookie) and asks the chat client for an answer. Because the RAG advisor already supplied context, the controller code stays short and does not manage templates or manual searches.

### 4) Cache, scheduling, monitoring
`SimpleRagApplication` enables caching, retry, and scheduling. A scheduled task evicts the `facts` cache every minute so edits in the dataset appear soon. `monitoring.properties` is imported for the shared monitoring starter.

---

## Configuration

### OpenAI API key
```bash
export OPENAI_API_KEY=your_api_key_here
```

### Vector store (Qdrant)
- Compose file: `spring/ai/rag/compose.yml`
- Ports: 6333 HTTP, 6334 gRPC
- Collection: `sandbox`
- Start via Spring Boot Docker support or run manually: `docker compose -f spring/ai/rag/compose.yml up -d`

### Chat memory database (H2)
```
spring.datasource.url=jdbc:h2:file:./spring/ai/rag/data/chat_memory;AUTO_SERVER=true;DB_CLOSE_DELAY=-1
spring.ai.chat.memory.repository.jdbc.schema=classpath:schema-h2.sql
```
Keeps chat history between restarts.

### Data sources
```
path.data.txt.resource=classpath:data.txt
path.data.pdf.resource=classpath:data.pdf
```
Use these files to teach the model. Update the files and restart (or rerun `DataLoader`) to re-ingest.

### Logging & retry
`SimpleLoggerAdvisor` prints prompts/responses. Retry settings live under `dataloader.retry.*` so you can tune attempts, delay, and multiplier.

---

## Run locally
```bash
cd spring/ai/rag
mvn spring-boot:run
```
Spring Boot will launch Qdrant (if Docker is available), load both data sources, seed the vector store, and expose the API on `http://localhost:8080`.

---

## API
`GET /api/facts?ask=your-question`

- Cookie `SESSION_ID` (optional, defaults to `SESSION_ID`).
- Response: plain text answer based on retrieved facts.

---

## Example
```bash
 curl -G "http://localhost:8080/api/facts" \
   --data-urlencode "ask=Share two facts about sports" \
   -b "SESSION_ID=demo-01"
```
