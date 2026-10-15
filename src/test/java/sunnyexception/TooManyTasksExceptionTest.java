package sunnyexception;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class TooManyTasksExceptionTest {

    @Test
    public void exceptionStoresMessage() {
        TooManyTasksException exception = new TooManyTasksException("Your taskboard can only hold so many!");

        assertEquals("Your taskboard can only hold so many!", exception.getMessage());
    }
}
