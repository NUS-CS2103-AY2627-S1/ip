package kaykay.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

/** Tests task-list ordering, mutation, and user-facing task-number validation. */
class TaskListTest {

    /** Checks adding, inserting, retrieving, and removing tasks. */
    @Test
    void taskList_mutations_preserveExpectedOrderAndSize() {
        Task first = new Todo("first");
        Task second = new Todo("second");
        Task inserted = new Todo("inserted");
        TaskList tasks = new TaskList(first, second);

        assertEquals(2, tasks.size());
        assertSame(first, tasks.getTask(0));
        tasks.add(inserted);
        tasks.add(1, new Todo("middle"));
        assertEquals(4, tasks.size());
        assertSame(second, tasks.getTask(2));
        assertSame(inserted, tasks.getTask(3));

        assertSame(first, tasks.remove(0));
        tasks.remove(inserted);
        assertEquals(2, tasks.size());
        assertEquals("middle", tasks.getTask(0).getDescription());
        assertSame(second, tasks.getTask(1));
    }

    /** Checks valid, invalid, non-numeric, and null task numbers. */
    @Test
    void isValidTaskNumber_taskNumber_isAcceptedOnlyWhenInRange() {
        TaskList tasks = new TaskList(new Todo("first"), new Todo("second"));

        assertTrue(tasks.isValidTaskNumber("1"));
        assertTrue(tasks.isValidTaskNumber("2"));
        assertFalse(tasks.isValidTaskNumber("0"));
        assertFalse(tasks.isValidTaskNumber("3"));
        assertFalse(tasks.isValidTaskNumber("one"));
        assertFalse(tasks.isValidTaskNumber(null));
    }

    /** Checks case-insensitive keyword matching and preservation of task order. */
    @Test
    void findTasks_keyword_returnsMatchingTasksInOrder() {
        Task first = new Todo("Read the BOOK");
        Task second = new Todo("buy milk");
        Task third = new Todo("return book");
        TaskList tasks = new TaskList(first, second, third);

        List<Task> matchingTasks = tasks.findTasks("book");

        assertEquals(List.of(first, third), matchingTasks);
        assertEquals(List.of(), tasks.findTasks("missing"));
    }
}
