package charliek.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.Test;

/**
 * Tests the user-facing exception messages and cause preservation.
 */
class ExceptionTest {
    /**
     * Verifies that the base exception constructors retain messages and causes.
     */
    @Test
    void charlieKException_constructors_preserveMessageAndCause() {
        RuntimeException cause = new RuntimeException("cause");

        CharlieKException withoutCause = new CharlieKException("message");
        CharlieKException withCause = new CharlieKException("message", cause);

        assertEquals("message", withoutCause.getMessage());
        assertEquals("message", withCause.getMessage());
        assertSame(cause, withCause.getCause());
    }

    /**
     * Verifies that parameter and task validation exceptions explain their failures.
     */
    @Test
    void validationExceptions_messages_describeExpectedInput() {
        assertEquals("The parameter /by was specified more than once. Use: deadline <description> /by <date/time>",
                new DuplicateParameterException("/by", "deadline <description> /by <date/time>").getMessage());
        assertEquals("That task is already on the list. Bring a new winner to the board.",
                new DuplicateTaskException().getMessage());
        assertEquals("The parameter is empty! Enter the required parameters and let's get this done!",
                new EmptyParameterException().getMessage());
        assertEquals("The description is empty! Bring the energy and enter it, or I will carry the flame!",
                new EmptyTaskDescriptionException().getMessage());
        assertEquals("That command format is not winning. Use 'help' to check the playbook.",
                new InvalidCommandFormatException().getMessage());
        assertEquals("That command format is not winning. Use: todo <description>",
                new InvalidCommandFormatException("todo <description>").getMessage());
        assertEquals("I couldn't understand that date/time! Try 2/12/2019 or 2/12/2019 6pm. Keep it winning.",
                new InvalidDateTimeException().getMessage());
        assertEquals("An event must end after it starts. Keep the timeline winning.",
                new InvalidEventRangeException().getMessage());
        assertEquals("That command is not winning, but I know how to carry the flame!",
                new UnknownCommandException().getMessage());
    }

    /**
     * Verifies that storage exceptions expose both the friendly message and original cause.
     */
    @Test
    void taskStorageException_constructor_preservesCause() {
        StorageFailureCause cause = new StorageFailureCause();
        TaskStorageException exception = new TaskStorageException("storage failed", cause);

        assertEquals("storage failed", exception.getMessage());
        assertSame(cause, exception.getCause());
    }

    /**
     * A small throwable used without depending on a particular file-system failure.
     */
    private static final class StorageFailureCause extends RuntimeException {
    }
}
