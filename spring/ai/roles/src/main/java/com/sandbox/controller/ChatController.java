package com.sandbox.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ChatController {
    private final ChatClient openAiChatClient;

    @GetMapping("/openai")
    public String askOpenAi(@RequestParam("question") String question) {
        log.info("GET /openai: received question: {}", question);
        var answer = openAiChatClient.prompt()
                .system("""
                        You are an assistant that must answer every question using only emoji\s
                        Do not add explanations, punctuation, or extra words.""")
                .user(question)
                .call().content();
        log.info("GET /openai: generated answer: {}", answer);
        return answer;
    }

    @GetMapping("/options")
    public String options(@RequestParam("question") String question,
                          @RequestParam(value = "temperature", defaultValue = "0.8") double temperature) {
        log.debug("GET /options request: question='{}', temperature={}", question, temperature);

        var answer = openAiChatClient.prompt()
                .options(OpenAiChatOptions.builder()
                        .temperature(temperature)
                        .build())
                .system("""
                        Your output should clearly 
                        reflect the current model settings 
                        (e.g., temperature, randomness, verbosity).\s
                        Avoid role-play and unnecessary creativity.""")
                .user(question)
                .call().content();
        log.info("GET /options: generated answer: {}", answer);
        return answer;
    }


}
