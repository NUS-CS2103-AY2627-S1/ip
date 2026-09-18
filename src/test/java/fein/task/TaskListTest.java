package fein.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import fein.FeinException;

/** Tests task deletion and task-number validation. */
class TaskListTest {
    /** Verifies that deleting a task shifts later tasks into the correct positions. */
    @Test
    void deleteRemovesCorrectTaskAndShiftsRemainingTasks() throws FeinException {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("first"));
        tasks.add(new Todo("second"));
        tasks.add(new Todo("third"));

        Task deleted = tasks.delete(2);

        assertEquals("second", deleted.getDescription());
        assertEquals(2, tasks.size());
        assertEquals("first", tasks.get(0).getDescription());
        assertEquals("third", tasks.get(1).getDescription());
    }

    /** Verifies that zero and out-of-range task numbers are rejected. */
    @Test
    void deleteInvalidTaskNumberThrowsException() throws FeinException {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("first"));

        assertThrows(FeinException.class, () -> tasks.delete(0));
        assertThrows(FeinException.class, () -> tasks.delete(2));
    }

    /** Verifies that tasks with matching type and details cannot be added more than once. */
    @Test
    void addRejectsDuplicateTasks() throws FeinException {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("Buy milk"));
        tasks.add(new Deadline("Submit report", "Friday"));
        tasks.add(new Event("Team meeting", "2pm", "4pm"));

        assertThrows(FeinException.class, () -> tasks.add(new Todo("buy milk")));
        assertThrows(FeinException.class, () -> tasks.add(new Deadline("submit report", "friday")));
        assertThrows(FeinException.class, () -> tasks.add(new Event("team meeting", "2PM", "4PM")));
        assertEquals(3, tasks.size());
    }

    /** Verifies that find matches descriptions case-insensitively without reordering tasks. */
    @Test
    void findReturnsMatchingTasksInOriginalOrder() throws FeinException {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("Read book"));
        tasks.add(new Todo("Attend class"));
        tasks.add(new Todo("Return BOOK"));

        assertEquals("Read book", tasks.find("book").get(0).getDescription());
        assertEquals("Return BOOK", tasks.find("book").get(1).getDescription());
        assertEquals(2, tasks.find("book").size());
    }

}
