package ${package}.service.error;

/**
 * Strategy interface for API error handling.
 * Allows for extensible error handling patterns.
 */
public interface ErrorHandler {
    /**
     * Handles an error that occurred during API operation.
     *
     * @param throwable The error that occurred
     * @param operationName Name of the operation that failed
     * @param serviceName Name of the service
     * @throws ApiException with appropriate error details
     */
    void handleError(Throwable throwable, String operationName, String serviceName);
}
