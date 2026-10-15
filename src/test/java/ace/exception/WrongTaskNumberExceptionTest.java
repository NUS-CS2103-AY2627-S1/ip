package ace.exception;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class WrongTaskNumberExceptionTest {

    @Test
    public void getMessage_includesGivenTaskNumberAndListHint() {
        WrongTaskNumberException exception = new WrongTaskNumberException(99);

        String message = exception.getMessage();

        assertTrue(message.contains("99"));
        assertTrue(message.contains("'list'"));
    }
}
