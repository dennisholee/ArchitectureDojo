package ${package}.auth;

import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.client.ClientResponseContext;
import jakarta.ws.rs.core.MultivaluedMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.Mockito.*;

class JwtAuthenticationInterceptorTest {

    @Mock
    private ClientRequestContext requestContext;

    @Mock
    private ClientResponseContext responseContext;

    @Mock
    private MultivaluedMap<String, Object> headers;

    private JwtAuthenticator jwtAuthenticator;
    private JwtAuthenticationInterceptor interceptor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        jwtAuthenticator = new JwtAuthenticator("test-secret", "test-issuer", 3600);
        interceptor = new JwtAuthenticationInterceptor(jwtAuthenticator, Map.of());
        when(requestContext.getHeaders()).thenReturn(headers);
    }

    @Test
    void filter_ShouldAddAuthorizationHeader() throws Exception {
        // Act
        interceptor.filter(requestContext);

        // Assert
        verify(headers).add(eq("Authorization"), matches("Bearer .*"));
    }

    @Test
    void filter_WithExistingToken_ShouldReuseToken() throws Exception {
        // Arrange
        String existingToken = jwtAuthenticator.generateToken(new HashMap<>());

        // Act
        interceptor.filter(requestContext);
        String firstToken = captureAuthorizationHeader();

        interceptor.filter(requestContext);
        String secondToken = captureAuthorizationHeader();

        // Assert - should reuse the same token
        assertEquals(firstToken, secondToken);
    }

    @Test
    void filter_Response_WithUnauthorized_ShouldInvalidateToken() throws Exception {

        // Act
        interceptor.filter(requestContext);

        // Arrange - next request should use a new token
        interceptor.filter(requestContext);
        String firstToken = captureAuthorizationHeader();

        interceptor.filter(requestContext);
        String secondToken = captureAuthorizationHeader();

        assertNotEquals(firstToken, secondToken);
    }

    private String captureAuthorizationHeader() {
        ArgumentCaptor<String> tokenCaptor = ArgumentCaptor.forClass(String.class);
        verify(headers, atLeastOnce()).add(eq("Authorization"), tokenCaptor.capture());
        return tokenCaptor.getValue();
    }
}
