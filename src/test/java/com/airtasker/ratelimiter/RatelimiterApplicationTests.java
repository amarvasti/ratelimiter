package com.airtasker.ratelimiter;

import com.airtasker.ratelimiter.controller.RateLimiterController;
import com.airtasker.ratelimiter.model.RemainingQuota;
import com.airtasker.ratelimiter.redis.TestRedisConfiguration;
import com.airtasker.ratelimiter.service.impl.SlidingWindowRateLimitStrategy;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;


import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.hamcrest.CoreMatchers.containsString;

@SpringBootTest(classes = TestRedisConfiguration.class)
@AutoConfigureMockMvc
class RatelimiterApplicationTests {

	@Autowired
	RateLimiterController rateLimiterController;

	@Autowired
    SlidingWindowRateLimitStrategy slidingWindowRateLimitStrategy;

	@Autowired
	private MockMvc mockMvc;

	@Test
	void testApiRateLimiter_allow() {
		RemainingQuota remainingQuota = slidingWindowRateLimitStrategy.getRemainingQuota("USER1");
		assertThat(remainingQuota).isNotNull();
		assertThat(remainingQuota.getRemainingAttempts()).isBetween(1, 99);
		assertThat(remainingQuota.getRemainingSeconds()).isBetween(1, 3600);
	}

	@Test
	void testApiRateLimiter_limitExceeded() {
		RemainingQuota remainingQuota = null;
		for (int i = 0; i < 101; i++) {
			remainingQuota = slidingWindowRateLimitStrategy.getRemainingQuota("USER2");
		}
		assertThat(remainingQuota).isNotNull();
		assertThat(remainingQuota.getRemainingAttempts()).isEqualTo(0);
		assertThat(remainingQuota.getRemainingSeconds()).isBetween(1, 3600);
	}

	@Test
	public void testController_shouldAllow() throws Exception {
		this.mockMvc.perform(TestRequestFactory.getWithHeader("/ratelimit", "USER3"))
				.andExpect(status().isOk())
				.andExpect(header().exists("X-Rate-Limit-Remaining"))
				.andExpect(content().string(containsString(RateLimiterController.ALLOWED)));
	}

	@Test
	public void testController_shouldReturnTooManyRequests() throws Exception {
		ResultActions resultActions = null;
		for (int i = 0; i < 101; i++) {
			resultActions = this.mockMvc.perform(TestRequestFactory.getWithHeader("/ratelimit", "USER4"));
		}
		resultActions.andExpect(status().isTooManyRequests())
				.andExpect(header().exists("X-Rate-Limit-Retry-After-Seconds"))
				.andExpect(content().string(containsString("Rate limit exceeded")));
	}
}
