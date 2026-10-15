package sunnyexception;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class TaskOutOfBoundsExceptionTest {

    @Test
    public void exceptionStoresMessage() {
        TaskOutOfBoundsException exception = new TaskOutOfBoundsException("There's no task there!");

        assertEquals("There's no task there!", exception.getMessage());
    }
}
