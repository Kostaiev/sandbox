package com.sandbox.config;

import com.sandbox.config.advisor.ChatTokenLimitAdvisor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class ChatClientConfig {

    @Bean("openai")
    public ChatClient openAi(OpenAiChatModel openAiChatModel) {
        ChatClient.Builder builder = ChatClient.builder(openAiChatModel);
        ChatOptions options = ChatOptions.builder()
                .model("gpt-4o-mini")
                .maxTokens(100)
                .temperature(0.8)
                .build();
        builder
                .defaultOptions(options)
                .defaultAdvisors(
                        List.of(new SimpleLoggerAdvisor(),
                        new ChatTokenLimitAdvisor(1,10000)))
                .defaultSystem("""
                Act as a professional chef. 
                Give clear, step-by-step recipes and kitchen techniques.""")
                .defaultUser("How can I help you?");
        return builder.build();
    }
}
