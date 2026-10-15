package minerva.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

public class TaskListTest {

    @Test
    public void delete_validIndex_success() {
        TaskList tasks = new TaskList();
        ToDo todo = new ToDo("buy milk");
        tasks.add(todo);

        Task removed = tasks.delete(0);
        assertEquals(todo, removed);
        assertEquals(0, tasks.getSize());
    }

    @Test
    public void delete_outOfBoundsIndex_exceptionThrown() {
        TaskList tasks = new TaskList();
        assertThrows(IndexOutOfBoundsException.class, () -> tasks.delete(0));
    }
}
