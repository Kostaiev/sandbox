package com.sandbox.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatClientConfig {

    @Bean("openai")
    public ChatClient openAi(OpenAiChatModel openAiChatModel) {
        ChatClient.Builder builder = ChatClient.builder(openAiChatModel);
        builder
                .defaultSystem("""
                Act as a professional chef. 
                Give clear, step-by-step recipes and kitchen techniques.""");
        return builder.build();
    }
}
