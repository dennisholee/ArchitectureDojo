package ${package}.exception;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ClientKitExceptionTest {
    private static final String TEST_ERROR_CODE = "TEST_ERROR";
    private static final String TEST_MESSAGE = "Test error message";
    private static final int TEST_STATUS = 400;
    private static final Throwable TEST_CAUSE = new RuntimeException("Test cause");

    @Test
    void constructor_WithErrorCode_ShouldSetCodeAndMessage() {
        ClientKitException exception = new ClientKitException(TEST_ERROR_CODE, TEST_STATUS, TEST_MESSAGE);

        assertEquals(TEST_MESSAGE, exception.getMessage());
        assertEquals(TEST_ERROR_CODE, exception.getErrorCode());
        assertEquals(TEST_STATUS, exception.getStatusCode());
        assertNull(exception.getCause());
    }

    @Test
    void constructor_WithFullDetails_ShouldSetAllFields() {
        ClientKitException exception = new ClientKitException(
            TEST_MESSAGE,
            TEST_CAUSE,
            TEST_STATUS,
            TEST_ERROR_CODE

        );

        assertEquals(TEST_MESSAGE, exception.getMessage());
        assertEquals(TEST_ERROR_CODE, exception.getErrorCode());
        assertEquals(TEST_STATUS, exception.getStatusCode());
        assertEquals(TEST_CAUSE, exception.getCause());
    }
}
