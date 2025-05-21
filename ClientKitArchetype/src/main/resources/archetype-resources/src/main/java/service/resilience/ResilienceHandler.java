package ${package}.service.resilience;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.retry.Retry;
import io.vavr.control.Try;
import ${package}.service.error.ErrorHandler;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

import java.util.function.Supplier;

/**
 * Handles resilience operations with circuit breaker and retry patterns.
 * Uses strategy pattern for error handling.
 */
@Slf4j
@Builder
public class ResilienceHandler {
    private final CircuitBreaker circuitBreaker;
    private final Retry retry;
    private final String serviceName;
    private final ErrorHandler errorHandler;

    public <T> T executeWithResilience(Supplier<T> operation, String operationName) {
        Supplier<T> decoratedSupplier = CircuitBreaker.decorateSupplier(circuitBreaker, operation);
        decoratedSupplier = Retry.decorateSupplier(retry, decoratedSupplier);

        return Try.ofSupplier(decoratedSupplier)
            .recover(throwable -> {
                errorHandler.handleError(throwable, operationName, serviceName);
                return null; // This line is never reached as handleError always throws
            })
            .get();
    }

    public CircuitBreaker.State getCircuitBreakerState() {
        return circuitBreaker.getState();
    }
}
