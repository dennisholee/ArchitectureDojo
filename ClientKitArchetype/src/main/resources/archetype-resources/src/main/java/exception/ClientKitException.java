package ${package}.exception;

/**
 * Base exception class for API-related errors.
 * Provides structured error information including HTTP status codes
 * and standardized error codes for different failure scenarios.
 *
 * <p>Features:</p>
 * <ul>
 *   <li>HTTP status code mapping</li>
 *   <li>Standardized error codes</li>
 *   <li>Detailed error messages</li>
 *   <li>Original cause preservation</li>
 * </ul>
 *
 * <p>Common error codes:</p>
 * <ul>
 *   <li>{@code VALIDATION_ERROR} - Input validation failure (400)</li>
 *   <li>{@code TOKEN_EXPIRED} - JWT token expired (401)</li>
 *   <li>{@code INVALID_TOKEN} - Invalid JWT token (401)</li>
 *   <li>{@code SERVICE_UNAVAILABLE} - Service temporarily unavailable (503)</li>
 *   <li>{@code CIRCUIT_OPEN} - Circuit breaker is open (503)</li>
 * </ul>
 *
 * <p>Usage example:</p>
 * <pre>
 * try {
 *     apiClient.makeRequest();
 * } catch (ApiException e) {
 *     switch (e.getErrorCode()) {
 *         case "TOKEN_EXPIRED" -> refreshToken();
 *         case "SERVICE_UNAVAILABLE" -> retryLater();
 *         default -> handleError(e);
 *     }
 *
 *     log.error("API error: {} ({})", e.getMessage(), e.getStatusCode());
 * }
 * </pre>
 */
public class ClientKitException extends RuntimeException {
    private final int statusCode;
    private final String errorCode;

    /**
     * Creates a new Client Kit exception with a message and status code.
     *
     * @param message Detailed error message
     * @param statusCode HTTP status code
     * @param errorCode Standardized error code
     */
    public ClientKitException(String message, int statusCode, String errorCode) {
        super(message);
        this.statusCode = statusCode;
        this.errorCode = errorCode;
    }

    /**
     * Creates a new Client Kit exception with a message, cause, and status code.
     *
     * @param message Detailed error message
     * @param cause Original cause of the error
     * @param statusCode HTTP status code
     * @param errorCode Standardized error code
     */
    public ClientKitException(String message, Throwable cause, int statusCode, String errorCode) {
        super(message, cause);
        this.statusCode = statusCode;
        this.errorCode = errorCode;
    }

    /**
     * Gets the HTTP status code associated with this error.
     *
     * @return The HTTP status code
     */
    public int getStatusCode() {
        return statusCode;
    }

    /**
     * Gets the standardized error code for this error.
     * This code can be used for programmatic error handling.
     *
     * @return The error code string
     */
    public String getErrorCode() {
        return errorCode;
    }
}
