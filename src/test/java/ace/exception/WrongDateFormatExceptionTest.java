package ace.exception;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class WrongDateFormatExceptionTest {

    @Test
    public void getMessage_includesGivenInputAndExpectedFormat() {
        WrongDateFormatException exception = new WrongDateFormatException("not-a-date");

        String message = exception.getMessage();

        assertTrue(message.contains("not-a-date"));
        assertTrue(message.contains("d/M/yyyy"));
    }
}
