package sunnyexception;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

public class TaskEmptyDescExceptionTest {
    List<String> temp = List.of("This ", " can't be empty!");

    @Test
    public void exceptionStoresMessage() {
        TaskEmptyDescException exception = new TaskEmptyDescException(temp, "ToDo");

        assertEquals("This ToDo can't be empty!", exception.getMessage());
    }
}
