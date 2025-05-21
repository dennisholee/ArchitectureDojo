package ${package}.service.resilience;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.retry.Retry;
import ${package}.config.ResilienceConfiguration;
import ${package}.service.error.DefaultErrorHandler;
import ${package}.service.error.ErrorHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * Default implementation of ResilienceFactory.
 * Creates resilience components with standard configuration and error handling.
 */
@Slf4j
public class DefaultResilienceFactory implements ResilienceFactory {

    @Override
    public ResilienceHandler createHandler(
            String serviceName,
            ResilienceConfiguration config,
            ErrorHandler errorHandler) {

        log.debug("Creating resilience handler for service: {}", serviceName);

        CircuitBreaker circuitBreaker = CircuitBreaker.of(
            serviceName + "-cb",
            config.getCircuitBreakerConfig()
        );

        Retry retry = Retry.of(
            serviceName + "-retry",
            config.getRetryConfig()
        );

        // If no error handler provided, create default one
        ErrorHandler handler = errorHandler != null ?
            errorHandler : new DefaultErrorHandler(circuitBreaker);

        return ResilienceHandler.builder()
            .circuitBreaker(circuitBreaker)
            .retry(retry)
            .serviceName(serviceName)
            .errorHandler(handler)
            .build();
    }
}
