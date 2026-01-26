package com.sandbox.config.advisor;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.CallAdvisor;
import org.springframework.ai.chat.client.advisor.api.CallAdvisorChain;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@RequiredArgsConstructor
public class ChatTokenLimitAdvisor implements CallAdvisor {
    private static final String NAME = "ChatTokenLimitAdvisor";

    private final int advisorOrder;
    private final int tokenLimit;
    private final AtomicInteger tokenSum = new AtomicInteger(0);


    @Override
    public ChatClientResponse adviseCall(ChatClientRequest chatClientRequest, CallAdvisorChain callAdvisorChain) {
        checkLimit();
        ChatClientResponse chatClientResponse = callAdvisorChain.nextCall(chatClientRequest);
        Optional.ofNullable(chatClientResponse.chatResponse()
                        .getMetadata()
                        .getUsage())
                .ifPresent(usage -> {
                    int requestTokens = usage.getTotalTokens();
                    int total = tokenSum.addAndGet(requestTokens);

                    log.debug("""
                                    Tokens used in this request: {};
                                    Total tokens used: {};
                                    Current token limit: {};""",
                            requestTokens, tokenSum, tokenLimit);
                });

        return chatClientResponse;
    }

    private void checkLimit() {
        int current = tokenSum.get();
        if (current >= tokenLimit) {
            throw new TokenLimitExceededException(current, tokenLimit);
        }
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public int getOrder() {
        return advisorOrder;
    }
}
