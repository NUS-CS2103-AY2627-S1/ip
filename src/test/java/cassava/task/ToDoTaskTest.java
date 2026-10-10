package cassava.task;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class ToDoTaskTest {

    @Test
    public void testGetTaskTypeToken() {
        ToDoTask task = new ToDoTask("water the plants");
        assertEquals("T", task.getTaskTypeToken());
    }

    @Test
    public void testConstructor_plain() {
        ToDoTask task = new ToDoTask("water the plants");
        assertEquals("[T] [ ] water the plants", task.toString());
    }

    @Test
    public void testConstructor_fullArgs() {
        ToDoTask task = new ToDoTask("water the plants", true, Task.TaskPriority.MEDIUM);
        assertEquals("[T] [X] water the plants [medium]", task.toString());
    }

    @Test
    public void testToDataString() {
        ToDoTask task = new ToDoTask("water the plants", false, Task.TaskPriority.UNKNOWN);
        assertEquals("T | 0 | water the plants | unknown", task.toDataString());
    }

}
