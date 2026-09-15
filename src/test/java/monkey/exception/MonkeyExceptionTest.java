package monkey.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MonkeyExceptionTest {
    @Test
    void constructor_messageProvided_exposesMessage() {
        assertEquals("invalid command", new MonkeyException("invalid command").getMessage());
    }
}
