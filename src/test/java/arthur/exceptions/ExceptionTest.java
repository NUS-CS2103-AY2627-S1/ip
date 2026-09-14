package arthur.exceptions;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;

import org.junit.jupiter.api.Test;

// AI-assisted: Tests user-facing messages produced by exception constructors.
public class ExceptionTest {
    @Test
    public void constructors_createExpectedMessages() {
        assertEquals("An invalid argument was passed to the command: value",
                new InvalidArgumentException("value").getMessage());
        assertEquals("An invalid argument was passed to the command: value (reason)",
                new InvalidArgumentException("value", "reason").getMessage());
        assertEquals("An error occurred while executing the following command: command",
                new InvalidCommandException("command").getMessage());
        assertEquals("Unable to parse datetime: value", new InvalidDateTimeException("value").getMessage());
        assertEquals("Missing argument from todo: /by", new MissingArgumentException("todo", "/by").getMessage());
        assertEquals("The called command does not exist: unknown",
                new UnknownCommandException("unknown").getMessage());
        assertEquals("Error while saving state: disk full",
                new StorageException(new IOException("disk full")).getMessage());
    }
}
