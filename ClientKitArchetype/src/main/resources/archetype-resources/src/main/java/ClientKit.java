package ${package};

import java.io.Closeable;

/**
 * Main interface for the Client Kit SDK.
 * Provides access to all available services and clients.
 *
 * @param <T> The type of service this client kit provides
 */
public interface ClientKit<T> extends Closeable {
    /**
     * Get the service instance.
     *
     * @return Service instance of type T
     */
    T getService();

    /**
     * Checks if the SDK is properly initialized and ready to use.
     *
     * @return true if the SDK is ready, false otherwise
     */
    boolean isReady();

    /**
     * Shuts down all services and releases resources.
     */
    @Override
    void close();
}
