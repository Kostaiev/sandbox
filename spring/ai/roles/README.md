# SimpleMessageRolesApplication

Learning Spring Boot application for experimenting with AI providers using Spring AI.

The goal of this project is to try set message roles in LLM to:
- OpenAI (cloud)

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
`SimpleMessageRolesApplication`
---

## Supported AI providers

### OpenAI

Uses `spring-ai-starter-model-openai`.

Required environment variable:

```bash
export OPENAI_API_KEY=your_api_key_here
```
Running locally:

`mvn spring-boot:run`

Test with cURL:

```bash
curl -G "http://localhost:8080/api/openai" \
  --data-urlencode "question=What do you do?"
```