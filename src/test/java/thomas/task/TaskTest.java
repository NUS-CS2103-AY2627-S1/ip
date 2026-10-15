package thomas.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

class TaskTest {
    @Test
    void newTask_startsIncompleteAndStoresDescription() {
        Task task = new Task("write tests");

        assertEquals("write tests", task.getDescription());
        assertFalse(task.isDone());
        assertEquals(" ", task.getStatusIcon());
        assertFalse(task.occursOn(LocalDate.of(2026, 8, 26)));
        assertEquals("N | write tests", task.toFileFormat());
        assertEquals("[ ] write tests", task.toString());
    }

    @Test
    void markAndUnmark_changeStatusAndFormatting() {
        Task task = new Task("write tests");

        task.markAsDone();
        assertTrue(task.isDone());
        assertEquals("X", task.getStatusIcon());
        assertEquals("Y | write tests", task.toFileFormat());
        assertEquals("[X] write tests", task.toString());

        task.markAsNotDone();
        assertFalse(task.isDone());
    }
}
