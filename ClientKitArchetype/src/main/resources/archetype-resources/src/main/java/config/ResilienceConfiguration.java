package ${package}.config;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.core.IntervalFunction;
import io.github.resilience4j.retry.RetryConfig;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.concurrent.TimeoutException;

/**
 * Configuration class for resilience patterns including circuit breakers and retries.
 * Provides a centralized configuration for all resilience-related settings with
 * sensible defaults and validation.
 *
 * <p>Features:</p>
 * <ul>
 *   <li>Circuit breaker configuration with sliding window</li>
 *   <li>Retry configuration with exponential backoff</li>
 *   <li>Timeout handling</li>
 *   <li>Failure rate thresholds</li>
 * </ul>
 *
 * <p>Usage example:</p>
 * <pre>
 * ClientConfiguration clientConfig = ClientConfiguration.builder()
 *     .baseUrl("https://api.example.com")
 *     .maxRetries(3)
 *     .build();
 *
 * ResilienceConfiguration resilienceConfig = new ResilienceConfiguration(clientConfig);
 * CircuitBreaker circuitBreaker = CircuitBreaker.of("my-service",
 *     resilienceConfig.getCircuitBreakerConfig());
 * </pre>
 *
 * <p>Default settings:</p>
 * <ul>
 *   <li>Circuit Breaker:
 *     <ul>
 *       <li>50% failure rate threshold</li>
 *       <li>30 second wait duration in open state</li>
 *       <li>10 calls sliding window size</li>
 *       <li>5 permitted calls in half-open state</li>
 *     </ul>
 *   </li>
 *   <li>Retry:
 *     <ul>
 *       <li>Exponential backoff starting at 500ms</li>
 *       <li>Maximum of 3 attempts (configurable)</li>
 *       <li>Retries on TimeoutException and RuntimeException</li>
 *     </ul>
 *   </li>
 * </ul>
 *
 * @see CircuitBreakerConfig
 * @see RetryConfig
 * @see ClientConfiguration
 */
@Slf4j
@Getter
public class ResilienceConfiguration {
    private final RetryConfig retryConfig;
    private final CircuitBreakerConfig circuitBreakerConfig;

    /**
     * Creates a new resilience configuration based on client settings.
     *
     * @param config The client configuration containing retry settings
     * @throws IllegalArgumentException if configuration is invalid
     */
    public ResilienceConfiguration(ClientConfiguration config) {
        log.debug("Initializing resilience configuration with {} max retries", config.getMaxRetries());

        this.retryConfig = RetryConfig.custom()
            .maxAttempts(config.getMaxRetries())
            .waitDuration(Duration.ofMillis(500))
            .intervalFunction(IntervalFunction.ofExponentialBackoff())
            .retryExceptions(TimeoutException.class, RuntimeException.class)
            .build();

        this.circuitBreakerConfig = CircuitBreakerConfig.custom()
            .slidingWindowType(CircuitBreakerConfig.SlidingWindowType.COUNT_BASED)
            .slidingWindowSize(10)
            .failureRateThreshold(50.0f)
            .waitDurationInOpenState(Duration.ofSeconds(30))
            .permittedNumberOfCallsInHalfOpenState(5)
            .build();

        log.debug("Resilience configuration initialized with circuit breaker and retry patterns");
    }

    /**
     * Gets the retry configuration with exponential backoff settings.
     *
     * @return The configured RetryConfig instance
     */
    public RetryConfig getRetryConfig() {
        return retryConfig;
    }

    /**
     * Gets the circuit breaker configuration with failure detection settings.
     *
     * @return The configured CircuitBreakerConfig instance
     */
    public CircuitBreakerConfig getCircuitBreakerConfig() {
        return circuitBreakerConfig;
    }
}
