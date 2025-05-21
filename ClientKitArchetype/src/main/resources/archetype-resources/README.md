# Client Kit SDK

A robust and flexible SDK for interacting with REST APIs, featuring built-in resilience patterns, configuration management, and comprehensive error handling.

## Features

- 🚀 Easy-to-use builder pattern for configuration
- 🔄 Built-in retry and circuit breaker patterns
- 🔐 Secure authentication handling (API Key and JWT)
- 📝 OpenAPI-generated client code
- ⚡ Async operation support
- 🛡️ Comprehensive error handling
- ✅ Bean validation support
- 📋 Detailed logging with SLF4J

## Project setup

Run the following mvn command to create a new project:

```bash
mvn archetype:generate \
    -DgroupId=io.forest.clientkit \
    -DartifactId=finance \
    -DarchetypeArtifactId=client-kit-archetype \
    -DarchetypeGroupId=io.forest \
    -DarchetypeVersion=1.0-SNAPSHOT
```

## Quick Start
### Basic Usage

```java
// Create a client kit instance with type-safe service
ClientKit<PetStoreService> clientKit = ClientKitBuilder
    .createForPetStore()
    .withBaseUrl("https://api.example.com")
```

## Configuration

The SDK supports externalized configuration through `application.yml`. You can override these settings through environment variables or system properties.

### Configuration Properties

```yaml
client:
  # Base API configuration
  api:
    baseUrl: https://api.example.com    # Required: Base URL for API calls
    connectTimeout: 30s                 # Connection timeout
    readTimeout: 30s                    # Read timeout
    maxRetries: 3                       # Maximum retry attempts (1-10)
    apiKey: ${CLIENT_API_KEY:}         # Optional API key

  # JWT Authentication
  jwt:
    secret: ${JWT_SECRET:}             # Required for JWT auth (min 32 chars)
    issuer: ${JWT_ISSUER:client-kit}   # JWT issuer identifier
    validitySeconds: 3600              # Token validity period (min 60s)

  # Resilience Patterns
  resilience:
    circuitBreaker:
      failureRateThreshold: 50         # Percentage of failures to open circuit
      waitDurationInOpenState: 30s     # How long to wait before half-open
      slidingWindowSize: 10            # Number of calls to track
      permittedNumberOfCallsInHalfOpenState: 5  # Calls allowed in half-open state
    retry:
      maxAttempts: 3                   # Maximum retry attempts
      initialInterval: 500ms           # Initial delay between retries
      multiplier: 2.0                  # Exponential backoff multiplier
      maxInterval: 5s                  # Maximum delay between retries
```

### Environment Variables

Key configuration properties can be overridden using environment variables:

```bash
# API Configuration
export CLIENT_API_KEY="your-api-key"
export CLIENT_BASE_URL="https://api.production.com"

# JWT Configuration
export JWT_SECRET="your-secret-key"
export JWT_ISSUER="your-app-name"
```

### Configuration Profiles

You can maintain different configurations for different environments:

```yaml
# application-dev.yml
client:
  api:
    baseUrl: http://localhost:8080
    connectTimeout: 10s
    maxRetries: 1

# application-prod.yml
client:
  api:
    baseUrl: https://api.production.com
    connectTimeout: 5s
    maxRetries: 3
```

### Programmatic Configuration

You can also configure the client programmatically, which will override the values from application.yml:

```java
ClientKit<PetStoreService> clientKit = ClientKitBuilder
    .createForPetStore()
    .withBaseUrl("https://api.custom.com")
    .withTimeouts(
        Duration.ofSeconds(5),
        Duration.ofSeconds(20)
    )
    .withJwtAuth(
        System.getenv("JWT_SECRET"),
        System.getenv("JWT_ISSUER")
    )
    .build();
```

### Configuration Validation

All configuration properties are validated on startup:

- Base URL must start with http:// or https://
- Timeouts must be positive durations
- JWT secret must be at least 32 characters
- MQ port must be between 1 and 65535
- Retry attempts must be between 1 and 10

If validation fails, you'll receive detailed error messages:

```
Configuration validation failed:
- client.api.baseUrl: must start with http:// or https://
- client.jwt.secret: length must be at least 32 characters
```

### Best Practices

1. **Environment-Specific Configuration**
   - Use different application-{env}.yml files for each environment
   - Never commit secrets to version control
   - Use environment variables for sensitive data

2. **Timeouts and Retries**
   - Set appropriate timeouts based on network conditions
   - Configure retries based on service reliability
   - Use circuit breakers for failing services

3. **Security**
   - Rotate JWT secrets regularly
   - Use strong secrets (min 32 characters)
   - Store credentials securely

4. **Monitoring**
   - Enable DEBUG logging for troubleshooting
   - Monitor circuit breaker states
   - Track retry attempts and failures

## Extending the SDK

The SDK is designed to be highly extensible through various extension points. Here's how you can extend different components:

### 1. Adding New Services

You can create new services that leverage the SDK's resilience patterns and validation:

```java
@Slf4j
public class OrderService extends ResilientService {
    private final OrderApi orderApi;

    public OrderService(
            ClientConfiguration config,
            Client client,
            ResilienceFactory resilienceFactory) {
        super(config, client, "order-service");
        this.orderApi = new OrderApi(client);
        this.orderApi.getApiClient().setBasePath(config.getBaseUrl());
    }

    public Order createOrder(OrderRequest request) {
        ValidationUtils.validate(request, "OrderRequest");
        return executeWithResilience(() -> {
            Order created = orderApi.createOrder(request);
            ValidationUtils.validate(created, "Order");
            return created;
        }, "createOrder");
    }
}
```

### 2. Custom Validation Rules

Create custom validators by implementing the `Validator` interface:

```java
@Slf4j
public class OrderValidator implements Validator<OrderRequest> {
    @Override
    public void validate(OrderRequest order, String context) {
        if (order.getItems().isEmpty()) {
            throw new ApiException(
                "Order must contain at least one item",
                400, "VALIDATION_ERROR"
            );
        }
    }

    @Override
    public boolean canHandle(Class<?> type) {
        return OrderRequest.class.isAssignableFrom(type);
    }
}

// Register the validator
ValidationUtils.registerValidator(new OrderValidator());
```

### 3. Custom Error Handlers

Implement custom error handling strategies:

```java
@Slf4j
public class OrderErrorHandler implements ErrorHandler {
    @Override
    public void handleError(Throwable throwable, String operationName, String serviceName) {
        if (throwable instanceof OrderNotFoundException) {
            throw new ApiException(
                "Order not found: " + throwable.getMessage(),
                404, "ORDER_NOT_FOUND"
            );
        }
        // Handle other specific error cases
    }
}

// Use the custom error handler
ResilienceHandler handler = resilienceFactory.createHandler(
    "order-service",
    resilienceConfig,
    new OrderErrorHandler()
);
```

### 4. Custom Resilience Patterns

Create custom resilience factories for specific needs:

```java
public class CustomResilienceFactory implements ResilienceFactory {
    @Override
    public ResilienceHandler createHandler(
            String serviceName,
            ResilienceConfiguration config,
            ErrorHandler errorHandler) {
        
        // Custom circuit breaker configuration
        CircuitBreakerConfig cbConfig = CircuitBreakerConfig.custom()
            .slidingWindowSize(20)
            .failureRateThreshold(30.0f)
            .build();
            
        CircuitBreaker circuitBreaker = CircuitBreaker.of(
            serviceName + "-cb",
            cbConfig
        );

        // Custom retry configuration
        RetryConfig retryConfig = RetryConfig.custom()
            .maxAttempts(5)
            .waitDuration(Duration.ofMillis(1000))
            .build();
            
        Retry retry = Retry.of(serviceName + "-retry", retryConfig);

        return ResilienceHandler.builder()
            .circuitBreaker(circuitBreaker)
            .retry(retry)
            .serviceName(serviceName)
            .errorHandler(errorHandler)
            .build();
    }
}
```

### 5. JWT Authentication Extensions

Customize JWT handling:

```java
public class CustomJwtAuthenticator extends JwtAuthenticator {
    public CustomJwtAuthenticator(String secret, String issuer, long validitySeconds) {
        super(secret, issuer, validitySeconds);
    }

    @Override
    public String generateToken(Map<String, Object> claims) {
        // Add custom claims
        claims.put("timestamp", Instant.now().toString());
        return super.generateToken(claims);
    }
}
```

### Best Practices for Extensions

1. **Follow SOLID Principles**
   - Create focused interfaces for new functionality
   - Extend base classes for common behavior
   - Use dependency injection for components

2. **Error Handling**
   - Extend `ApiException` for specific error cases
   - Provide detailed error messages
   - Use appropriate HTTP status codes

3. **Validation**
   - Create specific validators for complex rules
   - Combine multiple validators when needed
   - Add validation annotations to DTOs

4. **Testing**
   - Create unit tests for new components
   - Add integration tests for complex flows
   - Test error scenarios thoroughly

5. **Documentation**
   - Document public APIs
   - Include usage examples
   - Document error cases and handling

6. **Logging**
   - Use appropriate log levels
   - Include context in log messages
   - Add MDC for request tracing

For more examples and detailed documentation, check our [Wiki](https://github.com/architecturedojo/client-kit/wiki).

### Creating Custom Services

You can create your own services that leverage the SDK's resilience patterns and validation:

```java
// 1. Create your service model
@Data
@Builder
public class UserProfile {
    @NotBlank
    private String userId;
    
    @Email
    private String email;
    
    @Size(max = 100)
    private String displayName;
}

// 2. Create your service interface
public interface UserService {
    UserProfile getUserProfile(String userId);
    void updateUserProfile(UserProfile profile);
}

// 3. Implement your service with resilience patterns
@Slf4j
public class ResilientUserService extends ResilientService implements UserService {
    private final String apiPath;
    private final Client httpClient;

    public ResilientUserService(ClientConfiguration config, Client client) {
        super(config, client, "user-service");
        this.apiPath = config.getBaseUrl() + "/users";
        this.httpClient = client;
    }

    @Override
    public UserProfile getUserProfile(@NotBlank String userId) {
        return executeWithResilience(() -> {
            try {
                Response response = httpClient
                    .target(apiPath)
                    .path(userId)
                    .request(MediaType.APPLICATION_JSON)
                    .get();

                if (response.getStatus() == 404) {
                    throw new ApiException("User not found", 404, "USER_NOT_FOUND");
                }

                return response.readEntity(UserProfile.class);
            } catch (Exception e) {
                throw new ApiException("Failed to get user profile", e, 500, "USER_SERVICE_ERROR");
            }
        }, "getUserProfile");
    }

    @Override
    public void updateUserProfile(@Valid UserProfile profile) {
        executeWithResilience(() -> {
            try {
                Response response = httpClient
                    .target(apiPath)
                    .path(profile.getUserId())
                    .request(MediaType.APPLICATION_JSON)
                    .put(Entity.json(profile));

                if (response.getStatus() != 200) {
                    throw new ApiException("Failed to update profile", 
                        response.getStatus(), "UPDATE_FAILED");
                }
                return null;
            } catch (Exception e) {
                throw new ApiException("Failed to update user profile", 
                    e, 500, "USER_SERVICE_ERROR");
            }
        }, "updateUserProfile");
    }
}

// 4. Use your custom service
ClientKit<UserService> userKit = ClientKitBuilder
    .create((config, client) -> new ResilientUserService(config, client))
    .withBaseUrl("https://api.example.com")
    .withJwtAuth("your-secret", "your-issuer")
    .build();

try (userKit) {
    UserService userService = userKit.getService();
    UserProfile profile = userService.getUserProfile("123");
    log.info("Found user: {}", profile.getDisplayName());
}
```

## Troubleshooting

### Common Issues and Solutions

1. **Circuit Breaker is Open**
   ```java
   catch (ApiException e) {
       if ("CIRCUIT_OPEN".equals(e.getErrorCode())) {
           // Circuit breaker will remain open for the configured duration
           // Default: 30 seconds
           log.warn("Circuit breaker is open. Retry after: {} seconds", 
               circuitBreaker.getMetrics().getFailureRate());
       }
   }
   ```
   Solution: 
   - Check service health
   - Increase timeouts if needed
   - Adjust circuit breaker thresholds

2. **Validation Errors**
   ```java
   // Problem: Invalid input
   service.createPet("", "tag");  // Empty name
   
   // Solution: Validate input before calling
   if (StringUtils.isBlank(name)) {
       throw new IllegalArgumentException("Pet name cannot be blank");
   }
   ```

3. **JWT Token Expired**
   ```java
   catch (ApiException e) {
       if ("TOKEN_EXPIRED".equals(e.getErrorCode())) {
           // Token will be automatically refreshed on next request
           // Or manually invalidate:
           clientKit.getAuthenticator().invalidateToken();
       }
   }
   ```

4. **Connection Timeouts**
   ```java
   // Increase timeouts for slow connections
   ClientKit<PetStoreService> clientKit = ClientKitBuilder
       .createForPetStore()
       .withTimeouts(
           Duration.ofSeconds(10),  // Connect timeout
           Duration.ofSeconds(60)   // Read timeout
       )
       .build();
   ```

### Debug Logging

Enable debug logging to troubleshoot issues:

```xml
<configuration>
    <!-- Enable for detailed HTTP client logs -->
    <logger name="org.glassfish.jersey.client" level="DEBUG"/>
    
    <!-- Enable for resilience pattern logs -->
    <logger name="${package}.service" level="DEBUG"/>
    
    <!-- Enable for authentication logs -->
    <logger name="${package}.auth" level="DEBUG"/>
</configuration>
```

### Performance Optimization

1. **Connection Pooling**
   ```java
   ClientKit<PetStoreService> clientKit = ClientKitBuilder
       .createForPetStore()
       .configure(config -> {
           config.setMaxConnections(20);
           config.setKeepAliveTimeout(Duration.ofMinutes(5));
       })
       .build();
   ```

2. **Caching**
   ```java
   // Implement caching in your custom service
   public class CachedUserService extends ResilientUserService {
       private final Cache<String, UserProfile> cache;
       
       @Override
       public UserProfile getUserProfile(String userId) {
           return cache.get(userId, key -> super.getUserProfile(key));
       }
   }
   ```

### Health Checks

Monitor your service's health:

```java
try (ClientKit<PetStoreService> clientKit = getClientKit()) {
    PetStoreService service = clientKit.getService();
    
    // Check circuit breaker state
    CircuitBreaker.State state = service.getCircuitBreakerState();
    if (state == CircuitBreaker.State.OPEN) {
        log.warn("Service circuit breaker is open");
    }
    
    // Check service health
    try {
        service.listPets(1);
        log.info("Service is healthy");
    } catch (ApiException e) {
        log.error("Service health check failed: {}", e.getMessage());
    }
}
```