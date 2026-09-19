package leo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

public class LeoExceptionTest {
    @Test
    public void getMessage_returnsMessagePassedToConstructor() {
        LeoException exception = new LeoException("something went wrong");

        assertEquals("something went wrong", exception.getMessage());
    }

    @Test
    public void isCheckedException_mustBeDeclaredOrCaught() {
        // LeoException is a checked exception (extends Exception, not
        // RuntimeException) - this documents that contract: code that
        // throws it must declare or catch it, exactly like the
        // assertThrows call below has to.
        LeoException thrown = assertThrows(LeoException.class, () -> {
            throw new LeoException("boom");
        });

        assertEquals("boom", thrown.getMessage());
    }
}
