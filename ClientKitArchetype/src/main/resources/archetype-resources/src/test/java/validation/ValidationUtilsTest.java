package ${package}.validation;

import ${package}.exception.ClientKitException;
import ${package}.utils.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ValidationUtilsTest {

    @Test
    void validate_WithValidObject_ShouldPass() {
        // Arrange
        TestUtils.TestValidationModel validObject = TestUtils.createValidTestModel();

        // Act & Assert
        assertDoesNotThrow(() -> ValidationUtils.validate(validObject, "testObject"));
    }

    @Test
    void validate_WithNullRequiredField_ShouldThrow() {
        // Arrange
        TestUtils.TestValidationModel invalidObject = TestUtils.createInvalidTestModel();

        // Act & Assert
        ClientKitException exception = assertThrows(ClientKitException.class,
            () -> ValidationUtils.validate(invalidObject, "testObject"));
        assertEquals("VALIDATION_ERROR", exception.getErrorCode());
        assertEquals(400, exception.getStatusCode());
    }

    @Test
    void validate_WithCustomValidator_ShouldApplyBoth() {
        // Arrange
        ValidationUtils.registerValidator(new TestUtils.TestValidator());
        TestUtils.TestValidationModel shortFieldObject = new TestUtils.TestValidationModel("ab");

        // Act & Assert
        ClientKitException exception = assertThrows(ClientKitException.class,
            () -> ValidationUtils.validate(shortFieldObject, "testObject"));
        assertEquals("INVALID_LENGTH", exception.getErrorCode());
        assertEquals(400, exception.getStatusCode());
    }

    @Test
    void validate_WithMultipleValidations_ShouldCollectAllErrors() {
        // Arrange
        TestUtils.TestValidationModel invalidObject = new TestUtils.TestValidationModel("");

        // Act & Assert
        ClientKitException exception = assertThrows(ClientKitException.class,
            () -> ValidationUtils.validate(invalidObject, "testObject"));
        assertTrue(exception.getMessage().contains("must not be blank"));
    }
}
