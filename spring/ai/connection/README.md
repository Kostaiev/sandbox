# SimpleAiConnectionApplication

Learning Spring Boot application for experimenting with AI providers using Spring AI.

The goal of this project is to try connecting to:
- OpenAI (cloud)
- Ollama (docker)
- AWS Bedrock (cloud)

via a simple REST API.

---

## Tech stack
- Java 21
- Spring Boot 3.5.x
- Spring AI
- Spring Web (REST)
- Lombok
- Maven

---

## Modules / Setup
This is a sandbox-style Spring Boot app focused on **learning and experimentation**, not production readiness.

Main class:
`SimpleAiConnectionApplication`
---

## Supported AI providers

### OpenAI
Uses `spring-ai-starter-model-openai`.

Required environment variable:
```bash
export OPENAI_API_KEY=your_api_key_here
```
### Ollama
[ollama/ollama](https://github.com/ollama/ollama?tab=readme-ov-file)

Uses `spring-ai-starter-model-ollama`

Required environment variable:
```bash
export OLLAMA_MODEL=your_model_here

export OLLAMA_BASE_URL=your_url_here
```
Running locally:
`mvn spring-boot:run`

Test with cURL:
```bash
curl -G "http://localhost:8080/api/ask/openai" \
  --data-urlencode "question=Who are you?"
```
```bash
curl -G "http://localhost:8080/api/ask/ollama" \
  --data-urlencode "question=Who are you?"
```