package com.sandbox.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AskController {
    private final ChatClient chatClient;

//    public AskController(ChatClient.Builder builder) {
//        this.chatClient = builder.build();
//    }


    @GetMapping("/ask")
    public String askOpenAi(@RequestParam("question") String question) {
        log.info("GET /ask receive question: {}", question);
        var answer = chatClient.prompt(question)
                .call().content();
        log.info("GET /ask receive answer: {}", answer);
        return answer;
    }
}
