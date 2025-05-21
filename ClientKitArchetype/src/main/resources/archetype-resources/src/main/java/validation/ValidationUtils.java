package ${package}.validation;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.ValidatorFactory;
import lombok.extern.slf4j.Slf4j;
import ${package}.exception.ClientKitException;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Utility class for validation operations.
 * Supports multiple validators and provides default bean validation.
 */
@Slf4j
public class ValidationUtils {
    private static final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private static final jakarta.validation.Validator beanValidator = factory.getValidator();
    private static final List<Validator<?>> validators = new ArrayList<>();

    /**
     * Registers a custom validator.
     *
     * @param validator The validator to register
     */
    public static void registerValidator(Validator<?> validator) {
        validators.add(validator);
    }

    /**
     * Validates an object using registered validators or default bean validation.
     *
     * @param object  Object to validate
     * @param context Context description for error messages
     */
    public static void validate(final Object object,
                              final String context) {
        if (object == null) {
            throw new ClientKitException("Validation failed: object is null",
                                       400,
                                       "VALIDATION_ERROR");
        }

        // Try custom validators first
        boolean handled = false;
        for (Validator<?> validator : validators) {
            if (validator.canHandle(object.getClass())) {
                try {
                    @SuppressWarnings("unchecked")
                    Validator<Object> typedValidator = (Validator<Object>) validator;
                    typedValidator.validate(object, context);
                    handled = true;
                    break;
                } catch (ClassCastException e) {
                    log.warn("Validator {} failed to cast object of type {}",
                            validator.getClass().getSimpleName(),
                            object.getClass().getSimpleName());
                }
            }
        }

        // If no custom validator handled it, use bean validation
        if (!handled) {
            Set<ConstraintViolation<Object>> violations = beanValidator.validate(object);
            if (!violations.isEmpty()) {
                String message = violations.stream()
                        .map(v -> String.format("%s %s",
                                              v.getPropertyPath(),
                                              v.getMessage()))
                        .collect(Collectors.joining(", "));
                throw new ClientKitException(String.format("Validation failed for %s: %s",
                                                         context != null ? context : "object",
                                                         message),
                                           400,
                                           "VALIDATION_ERROR");
            }
        }
    }

    private static void performBeanValidation(Object object, String context) {
        Set<ConstraintViolation<Object>> violations = beanValidator.validate(object);
        if (!violations.isEmpty()) {
            String errorMessage = formatViolations(violations, context);
            log.error("Validation failed: {}", errorMessage);
            throw new ClientKitException(errorMessage, 400, "VALIDATION_ERROR");
        }
    }

    private static String formatViolations(Set<ConstraintViolation<Object>> violations, String context) {
        String details = violations.stream()
            .map(v -> v.getPropertyPath() + ": " + v.getMessage())
            .collect(Collectors.joining(", "));
        return String.format("Validation failed for %s: %s", context, details);
    }
}
