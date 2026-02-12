package com.sandbox.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.ai.chat.memory.ChatMemory.CONVERSATION_ID;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class RagController {
    private final ChatClient chatClient;
    private final VectorStore vectorStore;

    @Value("classpath:/templates/systemFactsPromptTemplate.st")
    private Resource systemFactsPromptTemplate;

    @Cacheable(cacheNames = "facts", key = "#sessionId + '::' + #question")
    @GetMapping("/facts")
    public ResponseEntity<String> messageResponseHelper(@RequestParam("ask") String question,
                                                        @CookieValue(value = "SESSION_ID",defaultValue = "SESSION_ID") String sessionId){
        var searchRequest = SearchRequest.builder()
                .query(question)
                .topK(4) // top 1 - 5 similar results to return
                .similarityThreshold(0.8) //Similarity threshold score to filter the search response by.
                .build();

        List<Document> listDocuments = vectorStore.similaritySearch(searchRequest);

        String context = listDocuments.stream()
                .map(Document::getText)
                .collect(Collectors.joining(System.lineSeparator()));

        String answer = chatClient.prompt()
                .system(promptSystemSpec -> promptSystemSpec.text(systemFactsPromptTemplate)
                        .param("data", context)
                        .param("question", question))
                .advisors(a -> a.param(CONVERSATION_ID, "sessionId"))
                .user(question)
                .call().content();
        return ResponseEntity.ok(answer);
    }
}
