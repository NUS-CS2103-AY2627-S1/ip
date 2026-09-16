package panda.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class ErrorTypeTest {

    @Test
    void getMessage_errorWithoutArguments_returnsConfiguredMessage() {
        assertEquals("OOPS!!! An identical task is already in the list.", ErrorType.DUPLICATE_TASK.getMessage());
    }

    @Test
    void getMessage_errorWithArguments_formatsArguments() {
        PandaException exception = new PandaException(ErrorType.MISSING_TASK_NUMBER, "delete");

        assertEquals("OOPS!!! Please specify a task number to delete.", exception.getMessage());
        assertEquals(ErrorType.MISSING_TASK_NUMBER, exception.getErrorType());
    }
}
