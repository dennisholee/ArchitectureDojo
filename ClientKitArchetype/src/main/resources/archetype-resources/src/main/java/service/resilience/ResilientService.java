package ${package}.service.resilience;

import ${package}.config.ClientConfiguration;
import ${package}.config.ResilienceConfiguration;
import ${package}.exception.ClientKitException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.retry.Retry;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Callable;
import java.util.function.Supplier;

/**
 * Base class for services with resilience patterns (circuit breaker, retry).
 */
@Slf4j
public abstract class ResilientService implements AutoCloseable {
    private final CircuitBreaker circuitBreaker;
    private final Retry retry;
    private final ResilienceConfiguration resilienceConfig;

    protected ResilientService(ClientConfiguration config) {
        this.resilienceConfig = new ResilienceConfiguration(config);
        this.circuitBreaker = CircuitBreaker.of("service-cb", resilienceConfig.getCircuitBreakerConfig());
        this.retry = Retry.of("service-retry", resilienceConfig.getRetryConfig());
    }

    /**
     * Executes an operation with circuit breaker and retry patterns.
     *
     * @param operation The operation to execute
     * @param <T> The return type of the operation
     * @return The result of the operation
     * @throws ClientKitException if the operation fails
     */
    protected <T> T executeWithResilience(Callable<T> operation) {
        Supplier<T> decoratedSupplier = CircuitBreaker
            .decorateSupplier(circuitBreaker, () -> {
                try {
                    return operation.call();
                } catch (Exception e) {
                    throw translateException(e);
                }
            });

        decoratedSupplier = Retry
            .decorateSupplier(retry, decoratedSupplier);

        try {
            return decoratedSupplier.get();
        } catch (Exception e) {
            throw translateException(e);
        }
    }

    /**
     * Gets the current state of the circuit breaker.
     *
     * @return The circuit breaker state
     */
    public CircuitBreaker.State getCircuitBreakerState() {
        return circuitBreaker.getState();
    }

    /**
     * Translates various exceptions into ClientKitException.
     */
    protected ClientKitException translateException(Throwable e) {
        if (e instanceof ClientKitException) {
            return (ClientKitException) e;
        }

        if (e instanceof IllegalStateException && e.getMessage().contains("CircuitBreaker")) {
            return new ClientKitException("Service is temporarily unavailable", e, 503, "CIRCUIT_OPEN");
        }

        log.error("Service operation failed", e);
        return new ClientKitException(e.getMessage(), e, 500, "SERVICE_ERROR");
    }

    @Override
    public void close() {
        // Cleanup if needed
    }
}
