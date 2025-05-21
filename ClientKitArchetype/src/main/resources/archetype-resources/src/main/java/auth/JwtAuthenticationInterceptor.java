package ${package}.auth;

import ${package}.exception.ClientKitException;
import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.client.ClientRequestFilter;
import java.util.Map;

/**
 * JWT authentication interceptor that automatically adds JWT tokens to request headers.
 * This interceptor is responsible for managing the JWT authentication flow in HTTP requests
 * by adding the "Authorization" header with a Bearer token.
 *
 * <p>The interceptor will:</p>
 * <ul>
 *   <li>Generate/retrieve JWT tokens using the configured authenticator</li>
 *   <li>Add tokens as Bearer authentication headers</li>
 *   <li>Handle token generation failures gracefully</li>
 * </ul>
 *
 * <p>Usage example:</p>
 * <pre>
 * Client client = ClientBuilder.newClient();
 * JwtAuthenticator authenticator = new JwtAuthenticator(secret, issuer, validity);
 * Map<String, Object> claims = new HashMap<>();
 * claims.put("userId", "123");
 *
 * client.register(new JwtAuthenticationInterceptor(authenticator, claims));
 * </pre>
 *
 * @see JwtAuthenticator
 * @see ClientRequestFilter
 */
public class JwtAuthenticationInterceptor implements ClientRequestFilter {
    private final JwtAuthenticator jwtAuthenticator;
    private final Map<String, Object> claims;

    /**
     * Creates a new JWT authentication interceptor.
     *
     * @param jwtAuthenticator The authenticator to use for token generation/management
     * @param claims Claims to include in the JWT token
     * @throws IllegalArgumentException if jwtAuthenticator is null
     */
    public JwtAuthenticationInterceptor(JwtAuthenticator jwtAuthenticator, Map<String, Object> claims) {
        if (jwtAuthenticator == null) {
            throw new IllegalArgumentException("JWT authenticator cannot be null");
        }
        this.jwtAuthenticator = jwtAuthenticator;
        this.claims = claims != null ? claims : Map.of();
    }

    /**
     * Filters client requests by adding JWT authentication.
     *
     * <p>This method will:</p>
     * <ul>
     *   <li>Get or generate a JWT token using the configured authenticator</li>
     *   <li>Add the token as a Bearer token in the Authorization header</li>
     *   <li>Allow the request to proceed without a token if token generation fails</li>
     * </ul>
     *
     * @param requestContext The request context to filter
     * @see ClientRequestContext
     */
    @Override
    public void filter(ClientRequestContext requestContext) {
        try {
            String token = jwtAuthenticator.getCurrentToken(claims);
            requestContext.getHeaders().add("Authorization", "Bearer " + token);
        } catch (ClientKitException e) {
            // If token generation fails, let the request proceed without the token
            // The server will handle the unauthorized request
        }
    }
}
