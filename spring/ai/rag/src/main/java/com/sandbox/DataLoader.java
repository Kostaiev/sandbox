package com.sandbox;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TextSplitter;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
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
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataLoader {
    private final VectorStore vectorStore;

    @Value("${path.data.txt.resource}")
    private Resource dataTxt;

    @Value("${path.data.pdf.resource}")
    private Resource dataPdf;


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
                new InputStreamReader(dataTxt.getInputStream(), StandardCharsets.UTF_8))) {
            TikaDocumentReader tikaPdfDocumentReader = new TikaDocumentReader(dataPdf);
            List<Document> pdfDocs = tikaPdfDocumentReader.get();
            TextSplitter textSplitter =
                    TokenTextSplitter.builder()
                            .withChunkSize(50)
                            .withMinChunkSizeChars(100)
                            .withMinChunkLengthToEmbed(200)
                            .withKeepSeparator(true)
                            .build();
            List<Document> pdf = textSplitter.split(pdfDocs);
            vectorStore.add(pdf);

            TikaDocumentReader tikaTxtDocumentReader = new TikaDocumentReader(dataTxt);
            List<Document> txtDocs = tikaTxtDocumentReader.get();
            List<Document> txt = textSplitter.split(txtDocs);
            vectorStore.add(txt);

            log.info("DataLoader: loaded {} documents", pdf.size() + txt.size());
        }

    }
}
