package com.sandbox.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.ai.chat.memory.ChatMemory.CONVERSATION_ID;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class RagController {
    private final ChatClient chatClient;


    @Cacheable(cacheNames = "facts", key = "#sessionId + '::' + #question")
    @GetMapping("/facts")
    public ResponseEntity<String> messageResponseHelper(@RequestParam("ask") String question,
                                                        @CookieValue(value = "SESSION_ID", defaultValue = "SESSION_ID") String sessionId) {
        String answer = chatClient.prompt()
                .advisors(a -> a.param(CONVERSATION_ID, "sessionId"))
                .user(question)
                .call().content();
        return ResponseEntity.ok(answer);
    }
}
