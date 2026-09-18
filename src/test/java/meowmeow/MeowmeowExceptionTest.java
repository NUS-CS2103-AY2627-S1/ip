package meowmeow;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * Tests for {@link MeowmeowException}'s varargs constructor - specifically
 * that it joins its lines with {@code "\n"}, the separator the UI later
 * splits on when boxing an error for display.
 */
public class MeowmeowExceptionTest {

    @Test
    public void constructor_singleLine_messageUnchanged() {
        assertEquals(" Meow? I don't understand that.",
                new MeowmeowException(" Meow? I don't understand that.").getMessage());
    }

    @Test
    public void constructor_multipleLines_joinedWithNewline() {
        assertEquals("first line\nsecond line",
                new MeowmeowException("first line", "second line").getMessage());
    }
}
