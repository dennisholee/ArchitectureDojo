package ${package}.config;

import ${package}.utils.TestUtils;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.retry.RetryConfig;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeoutException;
import static org.junit.jupiter.api.Assertions.*;

class ResilienceConfigurationTest {

    @Test
    void constructor_ShouldCreateValidRetryConfig() {
        // Act
        ResilienceConfiguration config = TestUtils.createTestResilienceConfig();
        RetryConfig retryConfig = config.getRetryConfig();

        // Assert
        assertNotNull(retryConfig);
        assertEquals(TestUtils.DEFAULT_MAX_RETRIES, retryConfig.getMaxAttempts());
        assertEquals(TestUtils.DEFAULT_RETRY_INTERVAL, retryConfig.getIntervalFunction().apply(1));
        assertTrue(retryConfig.getExceptionPredicate().test(new TimeoutException()));
    }

    @Test
    void constructor_ShouldCreateValidCircuitBreakerConfig() {
        // Act
        ResilienceConfiguration config = TestUtils.createTestResilienceConfig();
        CircuitBreakerConfig cbConfig = config.getCircuitBreakerConfig();

        // Assert
        assertNotNull(cbConfig);
        assertEquals(TestUtils.DEFAULT_CIRCUIT_BREAKER_FAILURE_THRESHOLD,
                    cbConfig.getSlidingWindowSize());
        assertEquals(TestUtils.DEFAULT_CIRCUIT_BREAKER_RESET_TIMEOUT,
                    cbConfig.getMaxWaitDurationInHalfOpenState().toMillis());
    }

    @Test
    void customConfiguration_ShouldOverrideDefaults() {
        // Arrange
        ClientConfiguration clientConfig = ClientConfiguration.builder()
            .baseUrl(TestUtils.TEST_BASE_URL)
            .circuitBreakerFailureThreshold(5)
            .circuitBreakerResetTimeout(2000)
            .retryMaxAttempts(4)
            .retryBackoffPeriod(200)
            .build();

        // Act
        ResilienceConfiguration config = new ResilienceConfiguration(clientConfig);

        // Assert
        assertEquals(5, config.getCircuitBreakerConfig().getSlidingWindowSize());
        assertEquals(2000, config.getCircuitBreakerConfig().getWaitDurationInOpenState().toMillis());
        assertEquals(4, config.getRetryConfig().getMaxAttempts());
        assertEquals(200, config.getRetryConfig().getIntervalFunction().apply(1).toMillis());
    }
}
