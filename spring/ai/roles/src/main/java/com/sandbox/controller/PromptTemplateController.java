package com.sandbox.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PromptTemplateController {
    private final ChatClient openAiChatClient;

    @Value("classpath:/promptTemplates/userPromptTemplate.st")
    private Resource promptTemplate;
    @Value("classpath:/promptTemplates/systemHrPromptTemplate.st")
    private Resource promptStuffingSystemTemplate;

    @GetMapping("/HR/message")
    public String messageResponseHelper(@RequestParam("userName") String userName,
                                        @RequestParam("message") String message) {
        log.info("GET /HR/message: received message: {}, from: {}", message, userName);
        var answer = openAiChatClient.prompt()
                .system(promptStuffingSystemTemplate)
                .user(prompt -> prompt
                        .text(promptTemplate)
                        .param("userName", userName)
                        .param("message", message))
                .call().content();
        log.info("GET /HR/message: generated answer: {}", answer);
        return answer;
    }
}
