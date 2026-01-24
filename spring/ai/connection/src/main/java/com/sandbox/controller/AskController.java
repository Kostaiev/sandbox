package com.sandbox.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@RestController
@RequestMapping("/api/ask")
public class AskController {
    private final ChatClient openAiChatClient;
    private final ChatClient ollamaChatClient;
    private final ChatClient bedrockChatClient;

    public AskController(@Qualifier("openai") ChatClient openAiChatClient,
                         @Qualifier("ollama") ChatClient ollamaChatClient,
                         @Qualifier("bedrock") ChatClient bedrockChatClient) {
        this.openAiChatClient = openAiChatClient;
        this.ollamaChatClient = ollamaChatClient;
        this.bedrockChatClient = bedrockChatClient;
    }

    @GetMapping("/openai")
    public String askOpenAi(@RequestParam("question") String question) {
        log.info("GET /ask/openai: received question: {}", question);
        var answer = openAiChatClient.prompt(question)
                .call().content();
        log.info("GET /ask/openai: generated answer: {}", answer);
        return answer;
    }

    @GetMapping("/ollama")
    public String askOllama(@RequestParam("question") String question) {
        log.info("GET /ask/ollama: received question: {}", question);
        var answer = ollamaChatClient.prompt(question)
                .call().content();
        log.info("GET /ask/ollama: generated answer: {}", answer);
        return answer;
    }

    @GetMapping("/bedrock")
    public String askBedrock(@RequestParam("question") String question) {
        log.info("GET /ask/bedrock: received question: {}", question);
        var answer = bedrockChatClient.prompt(question)
                .call().content();
        log.info("GET /ask/bedrock: generated answer: {}", answer);
        return answer;
    }
}
