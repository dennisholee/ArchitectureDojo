package ${package};

import ${package}.config.ClientConfiguration;
import ${package}.auth.JwtAuthenticator;
import ${package}.auth.JwtAuthenticationInterceptor;
import lombok.extern.slf4j.Slf4j;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiFunction;

/**
 * Default implementation of the ClientKit interface.
 *
 * @param <T> The type of service this client kit provides
 */
@Slf4j
class DefaultClientKit<T> implements ClientKit<T> {
    private final T service;
    private final AtomicBoolean isInitialized = new AtomicBoolean(false);
    private final JwtAuthenticator jwtAuthenticator;
    private final Client httpClient;

    DefaultClientKit(ClientConfiguration config, BiFunction<ClientConfiguration, Client, T> serviceFactory) {
        log.info("Initializing DefaultClientKit with service factory");
        this.jwtAuthenticator = initializeJwtAuthenticator(config);
        this.httpClient = initializeHttpClient(config);
        this.service = initializeService(config, serviceFactory);
        this.isInitialized.set(true);
        log.info("DefaultClientKit initialization completed");
    }

    private T initializeService(ClientConfiguration config, BiFunction<ClientConfiguration, Client, T> serviceFactory) {
        log.debug("Creating service instance using factory");
        try {
            return serviceFactory.apply(config, httpClient);
        } catch (Exception e) {
            log.error("Failed to create service instance: {}", e.getMessage());
            throw new IllegalStateException("Service initialization failed", e);
        }
    }

    private JwtAuthenticator initializeJwtAuthenticator(ClientConfiguration config) {
        if (config.getJwtSecret() != null) {
            log.debug("Initializing JWT authenticator");
            return new JwtAuthenticator(
                config.getJwtSecret(),
                config.getJwtIssuer(),
                config.getJwtValiditySeconds()
            );
        }
        log.debug("JWT authentication not configured");
        return null;
    }

    private Client initializeHttpClient(ClientConfiguration config) {
        log.debug("Initializing HTTP client with timeouts: connect={}, read={}",
            config.getConnectTimeout(), config.getReadTimeout());

        ClientBuilder builder = ClientBuilder.newBuilder()
            .connectTimeout(config.getConnectTimeout().getSeconds(), TimeUnit.SECONDS)
            .readTimeout(config.getReadTimeout().getSeconds(), TimeUnit.SECONDS);

        Client client = builder.build();

        if (jwtAuthenticator != null) {
            log.debug("Registering JWT authentication interceptor");
            client.register(new JwtAuthenticationInterceptor(
                jwtAuthenticator,
                config.getJwtClaims()
            ));
        }

        return client;
    }

    @Override
    public T getService() {
        checkInitialization();
        return service;
    }

    @Override
    public boolean isReady() {
        return isInitialized.get();
    }

    @Override
    public void close() {
        log.info("Closing DefaultClientKit resources");
        if (service instanceof AutoCloseable) {
            try {
                ((AutoCloseable) service).close();
            } catch (Exception e) {
                log.error("Error closing service: {}", e.getMessage());
                throw new RuntimeException("Failed to close service", e);
            }
        }

        if (httpClient != null) {
            httpClient.close();
        }
        isInitialized.set(false);
        log.info("DefaultClientKit resources closed");
    }

    private void checkInitialization() {
        if (!isReady()) {
            throw new IllegalStateException("ClientKit is not properly initialized");
        }
    }
}
