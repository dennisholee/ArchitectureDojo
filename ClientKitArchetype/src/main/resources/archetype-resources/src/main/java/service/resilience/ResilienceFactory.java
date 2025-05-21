package ${package}.service.resilience;

import ${package}.config.ResilienceConfiguration;
import ${package}.service.error.ErrorHandler;

/**
 * Factory interface for creating resilience components.
 * Adheres to Dependency Inversion Principle by allowing different
 * implementations of resilience pattern creation.
 */
public interface ResilienceFactory {
    /**
     * Creates a resilience handler for a service.
     *
     * @param serviceName Name of the service requiring resilience
     * @param config Resilience configuration
     * @param errorHandler Error handling strategy to use
     * @return Configured ResilienceHandler instance
     */
    ResilienceHandler createHandler(
        String serviceName,
        ResilienceConfiguration config,
        ErrorHandler errorHandler
    );
}
