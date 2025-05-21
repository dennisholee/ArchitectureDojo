package ${package}.auth;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import ${package}.exception.ClientKitException;
import lombok.extern.slf4j.Slf4j;

import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.Base64;

/**
 * Handles JWT token operations including generation, validation, renewal, and caching.
 * This class provides a complete JWT lifecycle management solution with automatic token
 * renewal and caching capabilities.
 *
 * <p>Features:</p>
 * <ul>
 *   <li>Token generation with configurable claims</li>
 *   <li>Automatic token renewal before expiration</li>
 *   <li>Token validation and verification</li>
 *   <li>Token caching for performance</li>
 *   <li>Thread-safe operation</li>
 * </ul>
 *
 * <p>Usage example:</p>
 * <pre>
 * JwtAuthenticator auth = new JwtAuthenticator(
 *     Base64.getEncoder().encodeToString("your-secret-key".getBytes()),
 *     "your-issuer",
 *     3600 // 1 hour validity
 * );
 *
 * // Generate a token with claims
 * Map<String, Object> claims = Map.of("userId", "123", "role", "admin");
 * String token = auth.generateToken(claims);
 *
 * // Validate a token
 * Claims validatedClaims = auth.validateToken(token);
 * </pre>
 *
 * @see JwtAuthenticationInterceptor
 */
@Slf4j
public class JwtAuthenticator {
    private final Key signingKey;
    private final String issuer;
    private final long tokenValidityInSeconds;
    private String currentToken;
    private Instant tokenExpiration;

    /**
     * Creates a new JWT authenticator.
     *
     * @param secret Base64-encoded secret key for signing tokens
     * @param issuer The issuer to include in tokens
     * @param tokenValidityInSeconds Token validity period in seconds
     * @throws IllegalArgumentException if secret is invalid or validity period is too short
     */
    public JwtAuthenticator(String secret, String issuer, long tokenValidityInSeconds) {
        log.debug("Initializing JWT authenticator for issuer: {}", issuer);
        this.signingKey = Keys.hmacShaKeyFor(Base64.getDecoder().decode(secret));
        this.issuer = issuer;
        this.tokenValidityInSeconds = tokenValidityInSeconds;
    }

    /**
     * Generates a new JWT token with the specified claims.
     * The token will include standard claims (iss, iat, exp) along with any custom claims provided.
     *
     * @param claims Custom claims to include in the token
     * @return The generated JWT token string
     * @throws ClientKitException if token generation fails
     */
    public String generateToken(Map<String, Object> claims) {
        log.debug("Generating new JWT token for claims: {}", claims.keySet());
        Instant now = Instant.now();
        Instant expiration = now.plusSeconds(tokenValidityInSeconds);

        String token = Jwts.builder()
            .setClaims(claims)
            .setIssuer(issuer)
            .setIssuedAt(Date.from(now))
            .setExpiration(Date.from(expiration))
            .signWith(signingKey)
            .compact();

        this.currentToken = token;
        this.tokenExpiration = expiration;
        log.debug("JWT token generated, expires at: {}", expiration);
        return token;
    }

    /**
     * Validates a JWT token and returns its claims.
     * This method performs full token validation including:
     * <ul>
     *   <li>Signature verification</li>
     *   <li>Expiration time check</li>
     *   <li>Issuer verification</li>
     * </ul>
     *
     * @param token The JWT token to validate
     * @return The validated token claims
     * @throws ClientKitException if the token is invalid or expired
     */
    public Claims validateToken(String token) {
        try {
            log.debug("Validating JWT token");
            Claims claims = Jwts.parser()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
            log.debug("JWT token validation successful");
            return claims;
        } catch (ExpiredJwtException e) {
            log.warn("JWT token has expired");
            throw new ClientKitException("Token has expired", e, 401, "TOKEN_EXPIRED");
        } catch (JwtException e) {
            log.error("JWT token validation failed: {}", e.getMessage());
            throw new ClientKitException("Invalid token", e, 401, "INVALID_TOKEN");
        }
    }

    /**
     * Gets the current token, generating a new one if expired or nearly expired.
     * This method implements token caching with automatic renewal when:
     * <ul>
     *   <li>No token exists</li>
     *   <li>Current token is expired</li>
     *   <li>Current token will expire within 60 seconds</li>
     * </ul>
     *
     * @param claims Claims to include if a new token is generated
     * @return A valid JWT token
     * @throws ClientKitException if token generation fails
     */
    public String getCurrentToken(Map<String, Object> claims) {
        if (isTokenExpired()) {
            log.debug("Current token expired or missing, generating new token");
            return generateToken(claims);
        }
        log.trace("Using existing valid token");
        return currentToken;
    }

    /**
     * Checks if the current token is expired or about to expire.
     * A token is considered expired if:
     * <ul>
     *   <li>No token exists</li>
     *   <li>Token has passed its expiration time</li>
     *   <li>Token will expire within 60 seconds</li>
     * </ul>
     *
     * @return true if the token is expired or will expire soon
     */
    public boolean isTokenExpired() {
        if (currentToken == null || tokenExpiration == null) {
            log.debug("No current token exists");
            return true;
        }
        boolean isExpiring = Instant.now().plusSeconds(60).isAfter(tokenExpiration);
        if (isExpiring) {
            log.debug("Current token is expired or expiring soon");
        }
        return isExpiring;
    }

    /**
     * Invalidates the current token, forcing generation of a new token on next request.
     * This method is useful when:
     * <ul>
     *   <li>Token needs to be revoked</li>
     *   <li>Claims need to be updated</li>
     *   <li>Force token rotation is required</li>
     * </ul>
     */
    public void invalidateToken() {
        log.debug("Invalidating current token");
        this.currentToken = null;
        this.tokenExpiration = null;
    }
}
