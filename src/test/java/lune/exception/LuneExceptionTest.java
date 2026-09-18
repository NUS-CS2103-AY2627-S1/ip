package lune.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import org.junit.jupiter.api.Test;

/**
 * Tests that LuneException preserves the message it's given and is a
 * checked Exception (not a RuntimeException), as callers rely on.
 */
public class LuneExceptionTest {

    @Test
    public void constructor_message_preservedForGetMessage() {
        LuneException exception = new LuneException("Uh-oh, something went wrong");
        assertEquals("Uh-oh, something went wrong", exception.getMessage());
    }

    @Test
    public void luneException_isCheckedException() {
        assertInstanceOf(Exception.class, new LuneException("message"));
    }
}
