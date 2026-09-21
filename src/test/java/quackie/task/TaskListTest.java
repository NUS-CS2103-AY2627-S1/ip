package quackie.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/** Tests adding, retrieving, deleting, and updating tasks in a task list. */
class TaskListTest {
    /** Verifies list insertion, retrieval, completion changes, and deletion. */
    @Test
    void managesTasksInOrder() {
        TaskList tasks = new TaskList();
        Task first = new ToDo("first");
        Task second = new ToDo("second");

        assertTrue(tasks.add(first));
        assertTrue(tasks.add(second));
        assertEquals(2, tasks.size());
        assertEquals(first, tasks.get(0));
        tasks.markAsDone(1);
        assertTrue(tasks.get(1).isDone());
        Task replacement = tasks.update(1, new Deadline("updated", "Friday"));
        assertEquals(replacement, tasks.get(1));
        assertTrue(replacement.isDone());
        tasks.markAsUndone(1);
        assertFalse(tasks.get(1).isDone());
        assertEquals(first, tasks.delete(0));
        assertEquals(replacement, tasks.get(0));
        assertEquals(1, tasks.size());
    }

    /** Verifies that the fixed capacity is enforced and indexes are checked. */
    @Test
    void enforcesCapacityAndValidIndexes() {
        TaskList tasks = new TaskList();
        for (int i = 0; i < 100; i++) {
            assertTrue(tasks.add(new ToDo("task " + i)));
        }

        assertFalse(tasks.add(new ToDo("too many")));
        assertThrows(AssertionError.class, () -> tasks.add(null));
        assertThrows(AssertionError.class, () -> tasks.contains(null));
        assertThrows(AssertionError.class, () -> tasks.update(0, null));
        assertThrows(IndexOutOfBoundsException.class, () -> tasks.get(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> tasks.markAsDone(100));
        assertThrows(IndexOutOfBoundsException.class, () -> tasks.markAsUndone(100));
        assertThrows(IndexOutOfBoundsException.class, () -> tasks.delete(100));
    }

    /** Verifies that task descriptions are searched case-insensitively. */
    @Test
    void findsMatchingDescriptionsIgnoringCase() {
        TaskList tasks = new TaskList();
        tasks.add(new ToDo("Read Book"));
        tasks.add(new ToDo("return book"));
        tasks.add(new ToDo("buy bread"));

        TaskList matches = tasks.find("BOOK");

        assertEquals(2, matches.size());
        assertEquals("Read Book", matches.get(0).getDescription());
        assertEquals("return book", matches.get(1).getDescription());
        assertEquals(0, tasks.find("groceries").size());
    }

    /** Verifies duplicate detection by task type and task-specific details. */
    @Test
    void detectsEquivalentTasks() {
        TaskList tasks = new TaskList();
        tasks.add(new ToDo("Read Book"));
        tasks.add(new Deadline("submit report", "Friday"));

        assertTrue(tasks.contains(new ToDo("read book")));
        assertFalse(tasks.contains(new Deadline("read book", "Friday")));
        assertTrue(tasks.contains(new Deadline("SUBMIT REPORT", "friday")));
        assertFalse(tasks.containsExcept(new ToDo("read book"), 0));
    }
}
