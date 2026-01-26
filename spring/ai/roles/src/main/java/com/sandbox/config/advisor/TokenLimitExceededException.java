package com.sandbox.config.advisor;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.TOO_MANY_REQUESTS)
public class TokenLimitExceededException extends RuntimeException {
    public TokenLimitExceededException(int used, int limit) {
        super("Token limit exceeded: used=%d, limit=%d".formatted(used, limit));;
    }
}
