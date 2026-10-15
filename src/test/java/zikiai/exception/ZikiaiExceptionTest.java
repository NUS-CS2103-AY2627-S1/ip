package zikiai.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.io.IOException;

import org.junit.jupiter.api.Test;

/**
 * Tests preservation of user-facing messages and lower-level causes.
 */
class ZikiaiExceptionTest {

    @Test
    void constructors_messageAndCause_valuesPreserved() {
        IOException cause = new IOException("disk unavailable");
        ZikiaiException messageOnly = new ZikiaiException("Invalid command.");
        ZikiaiException withCause = new ZikiaiException("Unable to save.", cause);

        assertEquals("Invalid command.", messageOnly.getMessage());
        assertEquals("Unable to save.", withCause.getMessage());
        assertSame(cause, withCause.getCause());
    }
}
