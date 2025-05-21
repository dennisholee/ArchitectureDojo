package ${package}.auth;

import ${package}.exception.ClientKitException;
import ${package}.utils.TestUtils;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class JwtAuthenticatorTest {
    private JwtAuthenticator jwtAuthenticator;

    @BeforeEach
    void setUp() {
        jwtAuthenticator = new JwtAuthenticator(
            TestUtils.TEST_JWT_SECRET,
            TestUtils.TEST_JWT_ISSUER,
            TestUtils.DEFAULT_JWT_EXPIRY);
    }

    @Test
    void generateToken_ShouldCreateValidToken() {
        // Arrange
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", "123");
        claims.put("role", "admin");

        // Act
        String token = jwtAuthenticator.generateToken(claims);

        // Assert
        assertNotNull(token);
        Claims validatedClaims = jwtAuthenticator.validateToken(token);
        assertEquals("123", validatedClaims.get("userId"));
        assertEquals("admin", validatedClaims.get("role"));
        assertEquals(TestUtils.TEST_JWT_ISSUER, validatedClaims.getIssuer());
    }

    @Test
    void validateToken_WithExpiredToken_ShouldThrowException() {
        // Arrange
        JwtAuthenticator shortLivedAuth = TestUtils.createShortLivedAuthenticator();
        String token = shortLivedAuth.generateToken(new HashMap<>());

        // Act & Assert
        TestUtils.waitFor(TestUtils.TOKEN_EXPIRY_WAIT);
        ClientKitException exception = assertThrows(ClientKitException.class, () ->
            shortLivedAuth.validateToken(token)
        );
        assertEquals("TOKEN_EXPIRED", exception.getErrorCode());
        assertEquals(401, exception.getStatusCode());
    }

    @Test
    void validateToken_WithInvalidToken_ShouldThrowException() {
        // Act & Assert
        ClientKitException exception = assertThrows(ClientKitException.class, () ->
            jwtAuthenticator.validateToken("invalid.token.here")
        );
        assertEquals("INVALID_TOKEN", exception.getErrorCode());
        assertEquals(401, exception.getStatusCode());
    }

    @Test
    void getCurrentToken_ShouldReturnCachedToken() {
        // Arrange
        Map<String, Object> claims = new HashMap<>();
        claims.put("test", "value");

        // Act
        String token1 = jwtAuthenticator.getCurrentToken(claims);
        String token2 = jwtAuthenticator.getCurrentToken(claims);

        // Assert
        assertNotNull(token1);
        assertEquals(token1, token2); // Should return same cached token
    }

    @Test
    void invalidateToken_ShouldClearCurrentToken() {
        // Arrange
        Map<String, Object> claims = new HashMap<>();
        String token = jwtAuthenticator.getCurrentToken(claims);
        assertNotNull(token);

        // Act
        jwtAuthenticator.invalidateToken();

        // Assert
        assertTrue(jwtAuthenticator.isTokenExpired());
        String newToken = jwtAuthenticator.getCurrentToken(claims);
        assertNotEquals(token, newToken);
    }
}
