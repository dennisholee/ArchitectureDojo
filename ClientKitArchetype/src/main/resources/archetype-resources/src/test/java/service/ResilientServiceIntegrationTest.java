package ${package}.service;

import ${package}.config.ClientConfiguration;
import ${package}.service.resilience.ResilientService;
import ${package}.exception.ClientKitException;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

import jakarta.ws.rs.ProcessingException;
import java.util.concurrent.Callable;
import static org.junit.jupiter.api.Assertions.*;

class ResilientServiceIntegrationTest {
    private ListAppender<ILoggingEvent> logAppender;
    private Logger serviceLogger;
    private TestResilientService service;

    static class TestResilientService extends ResilientService {
        private int callCount = 0;

        TestResilientService(ClientConfiguration config) {
            super(config);
        }

        public String executeTest(boolean shouldFail) {
            return executeWithResilience(() -> {
                callCount++;
                if (shouldFail) {
                    throw new ProcessingException("Test failure");
                }
                return "Success";
            });
        }

        public int getCallCount() {
            return callCount;
        }
    }

    @BeforeEach
    void setUp() {
        serviceLogger = (Logger) LoggerFactory.getLogger(ResilientService.class);
        logAppender = new ListAppender<>();
        logAppender.start();
        serviceLogger.addAppender(logAppender);

        service = new TestResilientService(
            ClientConfiguration.builder()
                .baseUrl("https://test.com")
                .retryMaxAttempts(2)
                .retryBackoffPeriod(100)
                .circuitBreakerFailureThreshold(3)
                .circuitBreakerResetTimeout(1000)
                .build()
        );
    }

    @Test
    void shouldRetryOnFailure() {
        // Act & Assert
        assertThrows(ClientKitException.class, () -> service.executeTest(true));
        assertEquals(3, service.getCallCount()); // Initial + 2 retries
    }

    @Test
    void shouldOpenCircuitAfterFailures() {
        // Arrange - Force circuit to open
        for (int i = 0; i < 3; i++) {
            assertThrows(ClientKitException.class, () -> service.executeTest(true));
        }

        // Act & Assert - Should fail fast with circuit open
        ClientKitException exception = assertThrows(ClientKitException.class,
            () -> service.executeTest(true));
        assertEquals("CIRCUIT_OPEN", exception.getErrorCode());
        assertEquals(CircuitBreaker.State.OPEN, service.getCircuitBreakerState());
    }

    @Test
    void shouldSucceedAfterRetry() {
        // Arrange
        TestResilientService serviceWithMockFailure = new TestResilientService(
            ClientConfiguration.builder()
                .baseUrl("https://test.com")
                .retryMaxAttempts(2)
                .build()
        ) {
            private int attempts = 0;

            @Override
            public String executeTest(boolean shouldFail) {
                return executeWithResilience(() -> {
                    attempts++;
                    if (attempts == 1) {
                        throw new ProcessingException("First attempt fails");
                    }
                    return "Success";
                });
            }
        };

        // Act
        String result = serviceWithMockFailure.executeTest(false);

        // Assert
        assertEquals("Success", result);
    }
}
