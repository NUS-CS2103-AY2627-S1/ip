package sunnyexception;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class UnrecognisedTaskExceptionTest {

    @Test
    public void exceptionStoresMessage() {
        UnrecognisedTaskException exception = new UnrecognisedTaskException("Is that a joke? What does that mean?");

        assertEquals("Is that a joke? What does that mean?", exception.getMessage());
    }
}
