package com.airtasker.ratelimiter;

import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

public class TestRequestFactory {
    public static MockHttpServletRequestBuilder getWithHeader(String url, String apiKey) {
        return MockMvcRequestBuilders.get(url)
                .header("X-api-key", apiKey);
    }
}
