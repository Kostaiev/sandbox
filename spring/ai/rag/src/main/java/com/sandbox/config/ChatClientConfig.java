package com.sandbox.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.memory.repository.jdbc.JdbcChatMemoryRepository;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.rag.preretrieval.query.transformation.RewriteQueryTransformer;
import org.springframework.ai.rag.preretrieval.query.transformation.TranslationQueryTransformer;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class ChatClientConfig {

    @Bean
    public ChatMemory chatMemorySetMax(JdbcChatMemoryRepository jdbcChatMemoryRepository) {
        return MessageWindowChatMemory.builder()
                .chatMemoryRepository(jdbcChatMemoryRepository)
                .maxMessages(8)
                .build();
    }

    @Bean
    public RetrievalAugmentationAdvisor retrievalAugmentationAdvisor(VectorStore vectorStore,
                                                                     ChatClient.Builder chatClientBuilder) {
        return RetrievalAugmentationAdvisor.builder()
                .queryTransformers(TranslationQueryTransformer.builder()
                                .chatClientBuilder(chatClientBuilder.clone())
                                .targetLanguage("english").build(),
                        RewriteQueryTransformer.builder()
                                .chatClientBuilder(chatClientBuilder.clone())
                                .build())
                .documentRetriever(VectorStoreDocumentRetriever.builder().vectorStore(vectorStore)
                        .topK(5).similarityThreshold(0.2).build())
                .build();
    }

    @Bean
    public ChatClient openAi(OpenAiChatModel openAiChatModel, ChatMemory chatMemory,
                             RetrievalAugmentationAdvisor retrievalAugmentationAdvisor) {
        Advisor memoryAdvisor = MessageChatMemoryAdvisor
                .builder(chatMemory)
                .build();
        var loggerAdvisor = new SimpleLoggerAdvisor();

        return ChatClient
                .builder(openAiChatModel)
                .defaultAdvisors(
                        List.of(loggerAdvisor, memoryAdvisor, retrievalAugmentationAdvisor)
                ).build();
    }
}
