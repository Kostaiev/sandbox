package com.sandbox;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.Resource;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.IntStream;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataLoader {
    private final VectorStore vectorStore;

    @Value("${path.data.txt.resource}")
    private Resource data;
    private AtomicLong lineNo = new AtomicLong(0);

    @EventListener(ApplicationReadyEvent.class)
    @Retryable(
            retryFor = Exception.class,
            maxAttemptsExpression = "${dataloader.retry.max-attempts:1}",
            backoff = @Backoff(
                    delayExpression = "${dataloader.retry.delay-ms:2000}",
                    multiplierExpression = "${dataloader.retry.multiplier:2}"
            )
    )
    public void loadDataIntoVectorStore() throws IOException {
        log.info("DataLoader: loading documents into VectorStore...");
        try (var buffer = new BufferedReader(
                new InputStreamReader(data.getInputStream(), StandardCharsets.UTF_8))) {

            List<Document> docs = buffer.lines()
                    .flatMap(line -> {
                        long ln = lineNo.incrementAndGet();
                        String[] sentences = line.split("(?<=[.!?])\\s+");

                        return IntStream.range(0, sentences.length)
                                .mapToObj(i -> {
                                    String text = sentences[i].trim();
                                    if (text.isBlank()) return null;

                                    Map<String, Object> meta = new HashMap<>();
                                    meta.put("source", data.getFilename());
                                    meta.put("line", ln);
                                    meta.put("sentenceIndex", i);
                                    meta.put("chunkId", data.getFilename() + ":" + ln + ":" + i);

                                    return new Document(text, meta);
                                })
                                .filter(Objects::nonNull);
                    })
                    .toList();
            vectorStore.add(docs);
            log.info("DataLoader: loaded {} documents", docs.size());
        }

    }
}
