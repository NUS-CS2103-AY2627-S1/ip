package crystal.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * Tests Crystal's user-facing exception formatting.
 */
public class CrystalExceptionTest {
    /** Verifies that the internal message receives Crystal's user-facing error prefix. */
    @Test
    public void getUserMessage_messageProvided_addsCrystalErrorPrefix() {
        CrystalException exception = new CrystalException("That task number does not exist!");

        assertEquals("That task number does not exist!", exception.getMessage());
        assertEquals("Crystal: Oopsies!!! That task number does not exist!",
                exception.getUserMessage());
    }
}
