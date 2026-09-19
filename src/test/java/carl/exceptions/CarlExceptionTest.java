package carl.exceptions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.Test;

public class CarlExceptionTest {
    @Test
    public void exceptionConstructors_preserveMessagesAndCauses() {
        RuntimeException cause = new RuntimeException("disk error");
        CarlException base = new CarlException("base");
        CarlStorageException storage = new CarlStorageException("storage", cause);

        assertEquals("base", base.getMessage());
        assertEquals("storage", storage.getMessage());
        assertSame(cause, storage.getCause());
    }

    @Test
    public void specializedExceptions_returnUserFacingMessages() {
        assertEquals("Command error: use this format", new CarlCommandException("use this format").getMessage());
        assertEquals("That command didn’t land. Try `help` for the available controls.",
                new CarlUnknownCommandException().getMessage());
        assertEquals("That task number does not exist. Use `list` to check task numbers.",
                new CarlUnknownTaskException().getMessage());
    }
}
