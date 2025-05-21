package ${package}.validation;

import jakarta.validation.Valid;
import ${package}.exception.ClientKitException;

/**
 * Strategy interface for validation operations.
 * Allows for different validation implementations while maintaining consistency.
 *
 * @param <T> The type of object to validate
 */
public interface Validator<T> {
    /**
     * Validates an object according to its constraints.
     *
     * @param object   Object to validate
     * @param context  Context description for error messages
     * @throws ClientKitException if validation fails
     */
    void validate(@Valid T object,
                 String context);

    /**
     * Checks if this validator can handle the given type.
     *
     * @param type  The type to check
     * @return true if this validator can handle the type
     */
    default boolean canHandle(Class<?> type) {
        return getTargetClass().isAssignableFrom(type);
    }

    /**
     * Gets the target class this validator handles.
     *
     * @return The class this validator is designed to validate
     */
    Class<T> getTargetClass();
}
