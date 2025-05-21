package ${package}.config;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Getter
@Builder
@AllArgsConstructor
@Validated
@ConfigurationProperties(prefix = "client")
public class ClientConfiguration {
    @NotBlank(message = "Base URL cannot be blank")
    @Pattern(regexp = "^https?://.*", message = "Base URL must start with http:// or https://")
    private final String baseUrl;

    @NotNull(message = "Connect timeout must be specified")
    private final Duration connectTimeout;

    @NotNull(message = "Read timeout must be specified")
    private final Duration readTimeout;

    @Min(value = 1, message = "Max retries must be at least 1")
    @Max(value = 10, message = "Max retries cannot exceed 10")
    private final int maxRetries;

    private final String apiKey;

    // JWT Configuration
    @Size(min = 32, message = "JWT secret must be at least 32 characters long")
    private final String jwtSecret;

    @NotBlank(message = "JWT issuer must be specified when using JWT authentication")
    private final String jwtIssuer;

    @Min(value = 60, message = "JWT validity must be at least 60 seconds")
    private final long jwtValiditySeconds;

    private final Map<String, Object> jwtClaims = new HashMap<>();

    // Resilience Configuration


    public Map<String, Object> getJwtClaims() {
        return Collections.unmodifiableMap(jwtClaims);
    }
}
