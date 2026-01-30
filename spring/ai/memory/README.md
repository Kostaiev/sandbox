# SimpleChatMemoryAiApi

Learning Spring Boot application for experimenting with **chat memory** in Spring AI using a JDBC-backed repository.

The goal of this project is to:
- store conversation context in a database (H2)
- replay a **window of last N messages** into the prompt (MessageWindowChatMemory)
- control memory scope via `ChatMemory.CONVERSATION_ID` & (SESSION_ID header)

via a simple REST API.

---

## Tech stack
- Java 21
- Spring Boot 3.5.x
- Spring AI
- OpenAI (cloud)
- Spring Web (REST)
- JDBC Chat Memory Repository
- H2 (file-based)
- Lombok
- Maven

---

## Modules / Setup

### 1) Chat memory configuration
`ChatClientConfig` configures memory as:

- `MessageWindowChatMemory` with `maxMessages(4)`
- `JdbcChatMemoryRepository` as persistent storage
- `MessageChatMemoryAdvisor` attaches memory to each prompt call
- `SimpleLoggerAdvisor` logs prompt/advisor activity (useful for learning)

So each request will include a conversation context window of the last **4 messages** for the given conversation id.

Main class:
`SimpleAiChatMemoryApplication`
---
## Configuration

### OpenAI API key

Uses `spring-ai-starter-model-openai`.

Set the required env variable:

```bash
export OPENAI_API_KEY=your_api_key_here
```

### Database (H2 file)

Uses `H2(runtime) & spring-ai-starter-model-chat-memory-repository-jdbc`.

This project uses H2 in file mode:
```properties
spring.datasource.url=jdbc:h2:file:./spring/ai/memory/data/chat_memory;AUTO_SERVER=true;DB_CLOSE_DELAY=-1
spring.datasource.username=sa
spring.datasource.password=
spring.h2.console.enabled=true
```
This keeps chat memory between restarts because data is stored on disk.

### API
GET /api/memory

Sends a user question to OpenAI and stores the messages into chat memory.
Conversation is selected by SESSION_ID header.

Request:

`Query param: question`

`Header: SESSION_ID (default "123" if not provided)`

### Examples:

Running locally:

`mvn spring-boot:run`


```bash
curl -G "http://localhost:8080/api/memory" \
  -H "SESSION_ID: abc" \
  --data-urlencode "question=Hi! My name is 010101. Remember it."
```
```bash
curl -G "http://localhost:8080/api/memory" \
-H "SESSION_ID: abc" \
--data-urlencode "question=What is my name?"
```