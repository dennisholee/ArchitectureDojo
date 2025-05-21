package ${package}.service.error;

import ${package}.exception.ClientKitException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import jakarta.ws.rs.ProcessingException;

/**
 * Default implementation of error handling strategy.
 * Handles common API errors with appropriate error codes and messages.
 */
@Slf4j
@RequiredArgsConstructor
public class DefaultErrorHandler implements ErrorHandler {
    private final CircuitBreaker circuitBreaker;

    @Override
    public void handleError(Throwable throwable, String operationName, String serviceName) {
        if (throwable instanceof ProcessingException) {
            handleNetworkError(throwable, operationName, serviceName);
        } else if (circuitBreaker.getState() == CircuitBreaker.State.OPEN) {
            handleCircuitBreakerOpen(throwable, serviceName);
        } else {
            handleUnexpectedError(throwable, operationName, serviceName);
        }
    }

    private void handleNetworkError(Throwable throwable, String operationName, String serviceName) {
        log.error("Network or timeout error in {}: {}", serviceName, throwable.getMessage());
        throw new ClientKitException(
            String.format("Network or timeout error during %s", operationName),
            throwable, 503, "SERVICE_UNAVAILABLE"
        );
    }

    private void handleCircuitBreakerOpen(Throwable throwable, String serviceName) {
        log.warn("Circuit breaker is OPEN for {}", serviceName);
        throw new ClientKitException(
            "Service is currently unavailable",
            throwable, 503, "CIRCUIT_OPEN"
        );
    }

    private void handleUnexpectedError(Throwable throwable, String operationName, String serviceName) {
        log.error("Unexpected error in {} during {}: {}",
            serviceName, operationName, throwable.getMessage());
        throw new ClientKitException(
            String.format("Error during %s", operationName),
            throwable, 500, "INTERNAL_ERROR"
        );
    }
}
