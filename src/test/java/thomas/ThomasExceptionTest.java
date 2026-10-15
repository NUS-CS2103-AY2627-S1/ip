package thomas;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;

/**
 * Tests ThomasException.
 * There is one thing to pin: the message given is the message got back
 * unchanged, since every complaint the user reads travels through it and the
 * read loop prints exactly what it carries. That it is checked rather than
 * unchecked is confirmed too, because that is what makes the compiler refuse
 * code that throws one without handling it.
 */
public class ThomasExceptionTest {

    @Test
    public void getMessage_anyMessage_isReturnedUnchanged() {
        assertEquals("Bust my buffers!", new ThomasException("Bust my buffers!").getMessage());
    }

    @Test
    public void getMessage_multiLineMessage_keepsTheNewline() {
        // The duplicate refusal quotes the existing task on a second line, and
        // the console prints that line with its own indent only if it arrives.
        assertEquals("first\n   second", new ThomasException("first\n   second").getMessage());
    }

    @Test
    public void constructor_anyMessage_isACheckedException() {
        // Not a RuntimeException: a checked one cannot be thrown past a
        // caller without being declared, which is what keeps every user
        // mistake reported rather than escaping as a stack trace.
        // Through a Throwable reference: the compiler refuses the test on the
        // exact type, since it already knows the two are unrelated.
        Throwable thrown = new ThomasException("x");
        assertFalse(thrown instanceof RuntimeException);
    }
}
