package com.airtasker.ratelimiter.controller;

import com.airtasker.ratelimiter.exception.RateLimitExceededException;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;


@ControllerAdvice
public class RateLimitExceededAdvice implements ResponseBodyAdvice<Object> {
    private int retryAfterInSeconds;

    @ResponseBody
    @ExceptionHandler(RateLimitExceededException.class)
    @ResponseStatus(HttpStatus.TOO_MANY_REQUESTS)
    String rateLimitExceededHandler(RateLimitExceededException ex) {
        this.retryAfterInSeconds = ex.getRetryAfterInSeconds();
        return ex.getMessage();
    }

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  ServerHttpRequest request, ServerHttpResponse response) {
        response.getHeaders().add("X-Rate-Limit-Retry-After-Seconds", String.valueOf(retryAfterInSeconds));
        return body;
    }
}
