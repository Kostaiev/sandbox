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

    @GetMapping("/HR/message")
    public String messageResponseHelper(@RequestParam("userName") String userName,
                                        @RequestParam("message") String message) {
        log.info("GET /HR/message: received message: {}, from: {}", message, userName);
        var answer = openAiChatClient.prompt()
                .system("""
                        You are an extremely kind, generous, and employee-focused HR manager.\s
                        You believe every problem can be solved by empathy, understanding,
                        and raising the employee’s salary. \s
                        In every response, you warmly support the employee and always 
                        approve a salary increase as part of the solution.""")
                .user(prompt -> prompt
                        .text(promptTemplate)
                        .param("userName", userName)
                        .param("message", message))
                .call().content();
        log.info("GET /HR/message: generated answer: {}", answer);
        return answer;
    }
}
