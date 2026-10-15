package chudgpt.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.Test;

/** Tests that domain exceptions retain their messages and optional causes. */
public class ChudExceptionTest {
    @Test
    public void constructor_messageOnly_retainsMessageWithoutCause() {
        ChudException exception = new ChudException("message");

        assertEquals("message", exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    public void constructor_messageAndCause_retainsBothValues() {
        IllegalStateException cause = new IllegalStateException("cause");

        ChudException exception = new ChudException("message", cause);

        assertEquals("message", exception.getMessage());
        assertSame(cause, exception.getCause());
    }
}
