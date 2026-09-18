package martin.exception;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

/**
 * Tests the application's exception hierarchy.
 */
public class MartinExceptionTest {

    @Test
    public void illegalCommandException_extendsMartinException() {
        assertInstanceOf(MartinException.class, new IllegalCommandException("Invalid command."));
    }

    @Test
    public void storageException_preservesCause() {
        Throwable cause = new RuntimeException("disk failure");

        StorageException exception = assertThrows(StorageException.class, () -> {
            throw new StorageException("Unable to access storage.", cause);
        });

        assertSame(cause, exception.getCause());
        assertInstanceOf(MartinException.class, exception);
    }
}
