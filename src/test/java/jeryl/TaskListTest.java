package jeryl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import jeryl.task.Todo;

public class TaskListTest {
    @Test
    public void add_singleTask_increasesSize() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        assertEquals(1, tasks.size());
        assertEquals("read book", tasks.get(0).toString().substring("[T][ ] ".length()));
    }

    @Test
    public void delete_middleTask_removesOnlyThatTaskAndShiftsRest() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("a"));
        tasks.add(new Todo("b"));
        tasks.add(new Todo("c"));

        tasks.delete(1);

        assertEquals(2, tasks.size());
        assertEquals("[T][ ] a", tasks.get(0).toString());
        assertEquals("[T][ ] c", tasks.get(1).toString());
    }

    @Test
    public void delete_indexOutOfBounds_throwsIndexOutOfBoundsException() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("a"));
        assertThrows(IndexOutOfBoundsException.class, () -> tasks.delete(5));
    }

    @Test
    public void size_emptyTaskList_returnsZero() {
        assertEquals(0, new TaskList().size());
    }
}
