package com.sandbox.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.web.bind.annotation.*;


@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ChatController {
    private final ChatClient openAiChatClient;


    @GetMapping("/memory")
    public String saveInMemory(@RequestParam("question") String question,
                               @RequestHeader(value = "SESSION_ID", defaultValue = "123") String session) {
        log.info("GET /memory: received question");
        var answer = openAiChatClient
                .prompt(question)
                .advisors(
                        advisorSpec -> advisorSpec
                                .param(ChatMemory.CONVERSATION_ID, session)
                )
                .call()
                .content();
        log.info("GET /memory: generated answer");
        return answer;
    }
}
