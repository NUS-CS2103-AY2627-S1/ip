package nutrisoy.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class TaskListTest {
    @Test
    public void testAddAndRemoveTask() {
        TaskList list = new TaskList();
        assertEquals(0, list.size());

        Todo todo = new Todo("read book");
        list.add(todo);
        assertEquals(1, list.size());
        assertEquals(todo, list.get(0));

        Task removed = list.remove(0);
        assertEquals(todo, removed);
        assertEquals(0, list.size());
    }

    @Test
    public void testIsEmpty() {
        TaskList list = new TaskList();
        assertTrue(list.isEmpty());
    }
}
