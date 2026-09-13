package odysseus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Tests task-list indexing and deletion behavior.
 */
class TaskListTest {
    @Test
    void deleteTask_removesRequestedTaskAndRenumbersList() throws OdysseusException {
        TaskList tasks = new TaskList();
        tasks.addTask(new Todo("first"));
        tasks.addTask(new Todo("second"));

        Task deleted = tasks.deleteTask(1);

        assertEquals("[T][ ] first", deleted.toString());
        assertEquals(1, tasks.getTaskCount());
        assertEquals("[T][ ] second", tasks.getTask(1).toString());
    }

    @Test
    void getTask_rejectsZeroAndPastEnd() {
        TaskList tasks = new TaskList();

        assertThrows(OdysseusException.class, () -> tasks.getTask(0));
        assertThrows(OdysseusException.class, () -> tasks.getTask(1));
    }

    @Test
    void addTasks_addsMultipleTasksInOrder() throws OdysseusException {
        TaskList tasks = new TaskList();

        tasks.addTasks(new Todo("first"), new Todo("second"));

        assertEquals(2, tasks.getTaskCount());
        assertEquals("[T][ ] first", tasks.getTask(1).toString());
        assertEquals("[T][ ] second", tasks.getTask(2).toString());
    }

    @Test
    void findTaskNumbers_matchesDescriptionsIgnoringCase() {
        TaskList tasks = new TaskList();
        tasks.addTask(new Todo("read Book"));
        tasks.addTask(new Todo("pay bills"));
        tasks.addTask(new Todo("return book"));

        assertEquals(List.of(1, 3), tasks.findTaskNumbers("BOOK"));
        assertEquals(List.of(), tasks.findTaskNumbers("meeting"));
    }
}
