package ${package}.service;

import ${package}.config.ClientConfiguration;
import ${package}.exception.ClientKitException;
import ${package}.petstore.api.DefaultApi;
import ${package}.petstore.ApiClient;
import ${package}.petstore.model.Pet;
import ${package}.petstore.model.PetRequest;
import ${package}.petstore.model.PetsResponse;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import jakarta.ws.rs.ProcessingException;

import java.net.SocketTimeoutException;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PetStoreServiceTest {

    @Mock
    private DefaultApi defaultApi;

    @Mock
    private ApiClient apiClient;

    @Mock
    private ClientConfiguration config;

    @BeforeEach
    void setUp() {
        when(defaultApi.getApiClient()).thenReturn(apiClient);
    }

    @Test
    void listPets_Success() {
        // Arrange
        Pet pet1 = new Pet().id(1L).name("Dog").tag("tag1");
        Pet pet2 = new Pet().id(2L).name("Cat").tag("tag2");
        PetsResponse response = new PetsResponse().pets(Arrays.asList(pet1, pet2));

        when(defaultApi.listPets(eq(10))).thenReturn(response);

        // Act
        PetStoreService petStoreService = new PetStoreService(defaultApi, config);
        List<Pet> pets = petStoreService.listPets(10);

        // Assert
        assertNotNull(pets);
        assertEquals(2, pets.size());
        assertEquals("Dog", pets.get(0).getName());
        assertEquals("Cat", pets.get(1).getName());

        verify(defaultApi).listPets(eq(10));

    }

    @Test
    void createPet_Success() {
        // Arrange
        String petName = "Fluffy";
        String petTag = "cute";
        Pet expectedPet = new Pet().id(1L).name(petName).tag(petTag);

        when(defaultApi.createPet(any(PetRequest.class))).thenReturn(expectedPet);

        // Act
        PetStoreService petStoreService = new PetStoreService(defaultApi, config);
        Pet createdPet = petStoreService.createPet(petName, petTag);

        // Assert
        assertNotNull(createdPet);
        assertEquals(petName, createdPet.getName());
        assertEquals(petTag, createdPet.getTag());

        verify(defaultApi).createPet(any(PetRequest.class));
    }

    @Test
    void serviceConfiguration_ShouldSetApiKey() {
        // Arrange
        String expectedApiKey = "test-api-key";
        ClientConfiguration config = ClientConfiguration.builder()
            .baseUrl("https://api.petstore.example.com/v1")
            .apiKey(expectedApiKey)
            .build();

        // Act
        PetStoreService service = new PetStoreService(defaultApi, config);
        // Assert
        verify(apiClient).setApiKey(eq(expectedApiKey));
        verify(apiClient).setBasePath(eq(config.getBaseUrl()));

    }

    @Test
    void listPets_WhenApiThrowsException_ShouldWrapInClientKitException() {
        // Arrange
        when(defaultApi.listPets(any())).thenThrow(new RuntimeException("API Error"));

        // Act & Assert
        PetStoreService petStoreService = new PetStoreService(defaultApi, config);
        ClientKitException exception = assertThrows(ClientKitException.class, () ->
            petStoreService.listPets(10)
        );

        assertEquals("Failed to list pets", exception.getMessage());
        assertEquals(500, exception.getStatusCode());
        assertEquals("PETSTORE_ERROR", exception.getErrorCode());
    }

    @Test
    void listPets_WhenNetworkTimeout_ShouldReturnServiceUnavailable() {
        // Arrange
        when(defaultApi.listPets(any()))
            .thenThrow(new ProcessingException(new SocketTimeoutException("Read timed out")));

        // Act & Assert
        PetStoreService petStoreService = new PetStoreService(defaultApi, config);
        ClientKitException exception = assertThrows(ClientKitException.class, () ->
            petStoreService.listPets(10)
        );

        assertEquals(503, exception.getStatusCode());
        assertEquals("SERVICE_UNAVAILABLE", exception.getErrorCode());
        assertTrue(exception.getMessage().contains("Network or timeout error"));
    }

    @Test
    void createPet_WithRetryableError_ShouldEventuallySucceed() {
        // Arrange
        String petName = "Fluffy";
        String petTag = "cute";
        Pet expectedPet = new Pet().id(1L).name(petName).tag(petTag);

        // First call fails, second succeeds
        when(defaultApi.createPet(any(PetRequest.class)))
            .thenThrow(new ProcessingException("Temporary error"))
            .thenReturn(expectedPet);

        // Act
        PetStoreService petStoreService = new PetStoreService(defaultApi, ClientConfiguration.builder()
            .baseUrl("https://api.example.com")
            .maxRetries(3)
            .build());

        Pet createdPet = petStoreService.createPet(petName, petTag);

        // Assert
        assertNotNull(createdPet);
        assertEquals(petName, createdPet.getName());
        assertEquals(petTag, createdPet.getTag());

        // Verify the API was called twice (one failure, one success)
        verify(defaultApi, times(2)).createPet(any(PetRequest.class));

    }

    @Test
    void listPets_WithCircuitBreakerOpen_ShouldFailFast() {
        // Arrange
        ClientConfiguration config = ClientConfiguration.builder()
            .baseUrl("https://api.example.com")
            .maxRetries(3)
            .build();

        when(defaultApi.listPets(any()))
            .thenThrow(new ProcessingException("Service unavailable"));

        // Act & Assert
        PetStoreService petStoreService = new PetStoreService(defaultApi, config);
        // Call multiple times to trigger circuit breaker
        for (int i = 0; i < 10; i++) {
            assertThrows(ClientKitException.class, () ->
                petStoreService.listPets(10)
            );
        }

        // Verify the last error is a circuit breaker error
        ClientKitException exception = assertThrows(ClientKitException.class, () ->
            petStoreService.listPets(10)
        );

        assertEquals("CIRCUIT_OPEN", exception.getErrorCode());
        assertEquals(503, exception.getStatusCode());
    }

    @Test
    void circuitBreaker_ShouldOpenAfterFailures() {
        // Arrange
        ClientConfiguration config = ClientConfiguration.builder()
            .baseUrl("https://api.example.com")
            .maxRetries(3)
            .build();

        when(defaultApi.listPets(any()))
            .thenThrow(new ProcessingException("Service unavailable"));

        // Act & Assert
        PetStoreService petStoreService = new PetStoreService(defaultApi, config);
        // Call multiple times to trigger circuit breaker
        for (int i = 0; i < 10; i++) {
            assertThrows(ClientKitException.class, () -> petStoreService.listPets(10));
        }

        // Verify circuit breaker state
        assertEquals(CircuitBreaker.State.OPEN, petStoreService.getCircuitBreakerState());

        // Verify the last error indicates circuit breaker is open
        ClientKitException exception = assertThrows(ClientKitException.class,
            () -> petStoreService.listPets(10)
        );
        assertEquals("CIRCUIT_OPEN", exception.getErrorCode());
        assertEquals(503, exception.getStatusCode());
    }

    @Test
    void retry_ShouldAttemptMultipleTimes() {
        // Arrange
        ClientConfiguration config = ClientConfiguration.builder()
            .baseUrl("https://api.example.com")
            .maxRetries(3)
            .build();

        when(defaultApi.listPets(any()))
            .thenThrow(new ProcessingException("Temporary error"))
            .thenThrow(new ProcessingException("Temporary error"))
            .thenReturn(new PetsResponse().pets(Collections.emptyList()));

        // Act
        PetStoreService petStoreService = new PetStoreService(defaultApi, config);
        List<Pet> result = petStoreService.listPets(10);

        // Assert
        assertNotNull(result);
        verify(defaultApi, times(3)).listPets(any()); // Verify called 3 times
    }

    @Test
    void service_WithJwtAuth_ShouldAddAuthorizationHeader() {
        // Arrange
        String testToken = "test-jwt-token";
        ClientConfiguration config = ClientConfiguration.builder()
            .baseUrl("https://api.example.com")
            .withJwtAuth("secret", "test-issuer")
            .build();

        when(httpClient.register(any(JwtAuthenticationInterceptor.class)))
            .thenReturn(httpClient);

        // Act
        PetStoreService petStoreService = new PetStoreService(defaultApi, httpClient);
        // Verify JWT interceptor was registered
        verify(httpClient).register(any(JwtAuthenticationInterceptor.class));
    }

    @Test
    void timeoutConfiguration_ShouldBeRespected() {
        // Arrange
        Duration connectTimeout = Duration.ofSeconds(5);
        Duration readTimeout = Duration.ofSeconds(10);

        ClientConfiguration config = ClientConfiguration.builder()
            .baseUrl("https://api.example.com")
            .withTimeouts(connectTimeout, readTimeout)
            .build();

        when(defaultApi.listPets(any()))
            .thenThrow(new ProcessingException(new SocketTimeoutException()));

        // Act & Assert
        PetStoreService petStoreService = new PetStoreService(defaultApi, config);
        ClientKitException exception = assertThrows(ClientKitException.class,
            () -> petStoreService.listPets(10)
        );

        assertEquals("SERVICE_UNAVAILABLE", exception.getErrorCode());
        assertEquals(503, exception.getStatusCode());
    }
}
