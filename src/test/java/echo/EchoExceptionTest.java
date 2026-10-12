package echo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

/** Tests the creation and behavior of {@link EchoException}. */
public class EchoExceptionTest {

    @Test
    public void constructor_setsMessageCorrectly() {
        String message = "Custom error message";
        EchoException exception = new EchoException(message);

        assertEquals(message, exception.getMessage());
        assertInstanceOf(Exception.class, exception);
    }

    @Test
    public void throwEchoException_isCaughtAsExpected() {
        EchoException exception = assertThrows(EchoException.class, () -> {
            throw new EchoException("Something went wrong");
        });

        assertEquals("Something went wrong", exception.getMessage());
    }
}
