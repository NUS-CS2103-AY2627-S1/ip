package alpha.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;

/** Tests task-list mutation, one-based indexing, and encapsulation. */
class TaskListTest {
    /** Verifies task mutations, one-based indexing, and invalid-number handling. */
    @Test
    void managesTasksUsingOneBasedNumbers() {
        TaskList tasks = new TaskList();
        Task todo = tasks.addTask("read book");
        Task deadline = tasks.addTask(new Deadline("return book", LocalDateTime.of(2019, 12, 2, 18, 0)));

        assertEquals(2, tasks.size());
        assertSame(todo, tasks.getTask(1));
        assertSame(deadline, tasks.getTask(2));
        assertTrue(tasks.getTask(0) == null);
        assertTrue(tasks.getTask(3) == null);

        tasks.markDone(2);
        assertTrue(deadline.isDone());
        tasks.markUndone(2);
        assertFalse(deadline.isDone());

        assertSame(todo, tasks.deleteTask(1));
        assertEquals(1, tasks.size());
        assertTrue(tasks.deleteTask(0) == null);
        assertTrue(tasks.deleteTask(2) == null);
    }

    /** Verifies callers cannot mutate the task list through its read-only view. */
    @Test
    void exposesTasksAsReadOnlyCollection() {
        TaskList tasks = new TaskList();
        tasks.addTask("read book");

        assertThrows(UnsupportedOperationException.class,
                () -> tasks.getTasks().add(new Todo("write notes")));
    }

    /** Verifies case-insensitive keyword matching is limited to task descriptions. */
    @Test
    void findsTasksByDescription() {
        TaskList tasks = new TaskList();
        Task book = tasks.addTask("Read a BOOK");
        tasks.addTask(new Deadline("submit report", LocalDateTime.of(2019, 12, 2, 18, 0)));

        List<Task> matches = tasks.findTasks("book");

        assertEquals(1, matches.size());
        assertSame(book, matches.get(0));
        assertTrue(tasks.findTasks("missing").isEmpty());
    }

    /** Verifies that dated tasks are ordered chronologically and todos remain at the end. */
    @Test
    void sortsTasksByDateTime_todosHaveNoDate_sortedAfterDatedTasks() {
        TaskList tasks = new TaskList();
        tasks.addTask("buy groceries");
        tasks.addTask(new Deadline("submit report", LocalDateTime.of(2019, 12, 2, 18, 0)));
        tasks.addTask(new Event("project meeting", LocalDateTime.of(2019, 10, 15, 14, 0),
                LocalDateTime.of(2019, 10, 15, 16, 0)));

        tasks.sortByDateTime();

        assertEquals("project meeting", tasks.getTask(1).getDescription());
        assertEquals("submit report", tasks.getTask(2).getDescription());
        assertEquals("buy groceries", tasks.getTask(3).getDescription());
    }
}
