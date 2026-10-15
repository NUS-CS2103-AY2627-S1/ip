package tuesday.task;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalTime;

import org.junit.jupiter.api.Test;

/**
 * Tests task description comparison behavior.
 */
public class TaskTest {
    @Test
    public void hasSameDescription_caseAndWhitespaceDifferences_returnsTrue() {
        Task firstTask = new Todo("  Read    Book  ");
        Task secondTask = new Todo("read book");

        assertTrue(firstTask.hasSameDescription(secondTask));
    }

    @Test
    public void hasSameDescription_differentTaskDetails_returnsTrue() {
        Task todo = new Todo("submit report");
        Task deadline = new Deadline("SUBMIT REPORT", LocalDate.of(2026, 9, 20), null);
        Task event = new Event("Submit   Report",
                new Event.DateTimeValue(null, LocalTime.of(9, 0)),
                new Event.DateTimeValue(null, LocalTime.of(10, 0)));
        deadline.mark();

        assertTrue(todo.hasSameDescription(deadline));
        assertTrue(todo.hasSameDescription(event));
    }

    @Test
    public void hasSameDescription_differentDescriptions_returnsFalse() {
        Task firstTask = new Todo("submit report");
        Task secondTask = new Todo("submit final report");

        assertFalse(firstTask.hasSameDescription(secondTask));
        assertFalse(firstTask.hasSameDescription(null));
    }
}
