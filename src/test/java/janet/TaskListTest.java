package janet;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class TaskListTest {

    @Test
    public void listTasks_emptyList_noTasks() {
        assertEquals("No tasks listed!", new TaskList().listTasks().message());
    }

    @Test
    public void deleteTask_emptyList_exceptionThrown() {
        try {
            new TaskList().deleteTask(0);
        } catch (JanetException e) {
            assertEquals("Deletion out of index!", e.getMessage());
        }
    }

    @Test
    public void findTasks_emptyList_noTasks() {
        assertEquals("No tasks found!", new TaskList().findTasks("").message());
    }
}
