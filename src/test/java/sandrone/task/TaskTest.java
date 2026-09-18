package sandrone.task;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/** Tests the behaviour of Task. */
public class TaskTest {
    @Test
    public void getStatusIcon_newTask_returnsSpace() {
        Task task = new Task("read book");

        assertEquals(" ", task.getStatusIcon());
    }
}
