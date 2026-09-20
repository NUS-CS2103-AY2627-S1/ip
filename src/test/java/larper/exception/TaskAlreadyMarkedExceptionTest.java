package larper.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class TaskAlreadyMarkedExceptionTest {
    @Test
    public void getMessage_alreadyMarked_expectedMessage() {
        assertEquals(" This one is already done. No need to perform extra productivity.",
                new TaskAlreadyMarkedException().getMessage());
    }
}
