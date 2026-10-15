package orangutan;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class OrangutanExceptionTest {
    @Test
    public void toString_exceptionWithMessage_returnsMessage() {
        OrangutanException exception = new OrangutanException("something went wrong");

        assertEquals("something went wrong", exception.toString());
    }
}
