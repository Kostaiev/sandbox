package com.sandbox.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.memory.repository.jdbc.JdbcChatMemoryRepository;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class ChatClientConfig {

    @Bean
    public ChatMemory chatMemorySetMax(JdbcChatMemoryRepository jdbcChatMemoryRepository) {
        return MessageWindowChatMemory.builder()
                .chatMemoryRepository(jdbcChatMemoryRepository)
                .maxMessages(4)
                .build();
    }

    @Bean("openai")
    public ChatClient openAi(OpenAiChatModel openAiChatModel, ChatMemory chatMemory) {
        Advisor memoryAdvisor = MessageChatMemoryAdvisor
                .builder(chatMemory)
                .build();
        var loggerAdvisor = new SimpleLoggerAdvisor();

        return ChatClient
                .builder(openAiChatModel)
                .defaultAdvisors(
                        List.of(loggerAdvisor, memoryAdvisor)
                ).build();
    }
}
