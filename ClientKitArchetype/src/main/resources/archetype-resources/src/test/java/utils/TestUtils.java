package ${package}.utils;

import ${package}.config.ClientConfiguration;
import ${package}.auth.JwtAuthenticator;
import ${package}.config.ResilienceConfiguration;
import ${package}.exception.ClientKitException;
import ${package}.petstore.model.Pet;
import ${package}.petstore.model.PetRequest;
import ${package}.petstore.model.PetsResponse;
import ${package}.validation.Validator;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.retry.RetryConfig;
import jakarta.validation.constraints.NotNull;
import java.time.Duration;
import java.util.Arrays;

/**
 * Utility class for common test operations.
 */
public class TestUtils {
    public static final String TEST_BASE_URL = "https://test-api.example.com";
    public static final String TEST_JWT_SECRET = "test-secret-key-for-jwt";
    public static final String TEST_JWT_ISSUER = "test-issuer";

    // Default resilience settings
    public static final int DEFAULT_CIRCUIT_BREAKER_FAILURE_THRESHOLD = 2;
    public static final int DEFAULT_CIRCUIT_BREAKER_RESET_TIMEOUT = 1000;
    public static final int DEFAULT_RETRY_MAX_ATTEMPTS = 2;
    public static final int DEFAULT_RETRY_BACKOFF_PERIOD = 100;

    // Default resilience test configurations
    public static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(1);
    public static final Duration DEFAULT_RETRY_INTERVAL = Duration.ofMillis(100);
    public static final int DEFAULT_MAX_RETRIES = 3;

    // Common test timeouts
    public static final int DEFAULT_JWT_EXPIRY = 3600;
    public static final int SHORT_LIVED_TOKEN_EXPIRY = 1;
    public static final int TOKEN_EXPIRY_WAIT = 1100; // Wait time for token expiry tests

    /**
     * Creates a default test configuration.
     */
    public static ClientConfiguration createTestConfig() {
        return ClientConfiguration.builder()
                .baseUrl(TEST_BASE_URL)
                .jwtSecret(TEST_JWT_SECRET)
                .jwtIssuer(TEST_JWT_ISSUER)
                .build();
    }

    /**
     * Creates a test configuration with default resilience settings.
     */
    public static ClientConfiguration createTestConfigWithResilience() {
        return ClientConfiguration.builder()
                .baseUrl(TEST_BASE_URL)
                .jwtSecret(TEST_JWT_SECRET)
                .jwtIssuer(TEST_JWT_ISSUER)
                .circuitBreakerFailureThreshold(DEFAULT_CIRCUIT_BREAKER_FAILURE_THRESHOLD)
                .circuitBreakerResetTimeout(DEFAULT_CIRCUIT_BREAKER_RESET_TIMEOUT)
                .retryMaxAttempts(DEFAULT_RETRY_MAX_ATTEMPTS)
                .retryBackoffPeriod(DEFAULT_RETRY_BACKOFF_PERIOD)
                .build();
    }

    /**
     * Creates a test retry configuration
     */
    public static RetryConfig createTestRetryConfig() {
        return RetryConfig.custom()
            .maxAttempts(DEFAULT_MAX_RETRIES)
            .waitDuration(DEFAULT_RETRY_INTERVAL)
            .build();
    }

    /**
     * Creates a test circuit breaker configuration
     */
    public static CircuitBreakerConfig createTestCircuitBreakerConfig() {
        return CircuitBreakerConfig.custom()
            .slidingWindowSize(DEFAULT_CIRCUIT_BREAKER_FAILURE_THRESHOLD)
            .waitDurationInOpenState(Duration.ofMillis(DEFAULT_CIRCUIT_BREAKER_RESET_TIMEOUT))
            .build();
    }

    /**
     * Creates test resilience configuration with both retry and circuit breaker
     */
    public static ResilienceConfiguration createTestResilienceConfig() {
        ClientConfiguration clientConfig = createTestConfigWithResilience();
        return new ResilienceConfiguration(clientConfig);
    }

    /**
     * Creates a short-lived JWT authenticator for testing expiry scenarios
     */
    public static JwtAuthenticator createShortLivedAuthenticator() {
        return new JwtAuthenticator(TEST_JWT_SECRET, TEST_JWT_ISSUER, SHORT_LIVED_TOKEN_EXPIRY);
    }

    /**
     * Safely waits for a specific duration in milliseconds.
     * Uses Thread.sleep but handles InterruptedException.
     */
    public static void waitFor(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Test interrupted while waiting", e);
        }
    }

    /**
     * Creates a sample pet for testing
     */
    public static Pet createTestPet() {
        return new Pet()
            .id(1L)
            .name("TestPet");
    }

    /**
     * Creates a sample pet request for testing
     */
    public static PetRequest createTestPetRequest() {
        return new PetRequest()
            .name("TestPet");
    }

    /**
     * Creates a sample pets response for testing
     */
    public static PetsResponse createTestPetsResponse() {
        return new PetsResponse()
            .pets(Arrays.asList(createTestPet()));
    }

    /**
     * Test model class for validation tests
     */
    public static class TestValidationModel {
        @NotNull
        private String requiredField;

        public TestValidationModel(String requiredField) {
            this.requiredField = requiredField;
        }

        public String getRequiredField() {
            return requiredField;
        }
    }

    /**
     * Creates a test validator for validation tests
     */
    public static class TestValidator implements Validator<TestValidationModel> {
        @Override
        public void validate(TestValidationModel object, String context) {
            if (object.requiredField != null && object.requiredField.length() < 3) {
                throw new ClientKitException("Field must be at least 3 characters", 400, "INVALID_LENGTH");
            }
        }

        @Override
        public Class<TestValidationModel> getTargetClass() {
            return TestValidationModel.class;
        }
    }

    /**
     * Creates a test model instance with a valid value
     */
    public static TestValidationModel createValidTestModel() {
        return new TestValidationModel("valid-value");
    }

    /**
     * Creates a test model instance with an invalid value
     */
    public static TestValidationModel createInvalidTestModel() {
        return new TestValidationModel(null);
    }
}
