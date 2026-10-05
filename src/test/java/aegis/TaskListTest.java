package aegis;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import aegis.task.Todo;

public class TaskListTest {

    @Test
    public void add_multipleTasks_sizeIncreases() {
        TaskList list = new TaskList();
        list.add(new Todo("a", false));
        list.add(new Todo("b", false));
        list.add(new Todo("c", false));
        assertEquals(3, list.size());
    }

    @Test
    public void mark_changesStatus() {
        TaskList list = new TaskList();
        list.add(new Todo("a", false));
        list.mark(0);
        assertEquals("X", list.get(0).getStatusIcon());
    }

    @Test
    public void unmark_revertsStatus() {
        TaskList list = new TaskList();
        list.add(new Todo("a", true));
        list.unmark(0);
        assertEquals(" ", list.get(0).getStatusIcon());
    }
}
