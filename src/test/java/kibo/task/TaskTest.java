package kibo.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

import org.junit.jupiter.api.Test;

/**
 * Tests the assumptions enforced by task assertions.
 */
public class TaskTest {

    @Test
    void markAndUnmark_repeatedOperations_preservesDescriptionAndStatus() {
        for (Task task : List.of(new Task("task"), new Todo("task"),
                new Deadline("task", LocalDate.of(2024, 2, 29)), new Event("task", "start", "end"))) {
            assertFalse(task.isDone());
            assertEquals(" ", task.getStatusIcon());
            task.markAsDone();
            task.markAsDone();
            assertTrue(task.isDone());
            assertEquals("X", task.getStatusIcon());
            assertTrue(task.toString().contains("[X] task"));
            task.markAsNotDone();
            task.markAsNotDone();
            assertFalse(task.isDone());
            assertEquals("task", task.getDescription());
            assertTrue(task.toString().contains("[ ] task"));
        }
    }

    @Test
    void isScheduledOn_nullDate_throwsAssertionErrorForAllTypes() {
        for (Task task : List.of(new Todo("task"), new Deadline("task", LocalDate.of(2024, 2, 29)),
                new Event("task", "2024-02-29", "evening"))) {
            assertThrows(AssertionError.class, () -> task.isScheduledOn(null));
        }
    }

    @Test
    void eventConstructor_nullStartOrEnd_throwsAssertionError() {
        assertThrows(AssertionError.class, () -> new Event("task", null, "end"));
        assertThrows(AssertionError.class, () -> new Event("task", "start", null));
    }

    @Test
    void deadlineDisplay_nonEnglishLocale_keepsEnglishMonth() {
        Locale original = Locale.getDefault();
        try {
            Locale.setDefault(Locale.CHINESE);
            assertEquals("[D][ ] task (by: Feb 29 2024)",
                    new Deadline("task", LocalDate.of(2024, 2, 29)).toString());
        } finally {
            Locale.setDefault(original);
        }
    }

    @Test
    void constructor_nullDescription_throwsAssertionError() {
        assertThrows(AssertionError.class, () -> new Todo(null));
    }

    @Test
    void constructor_blankDescription_throwsAssertionError() {
        assertThrows(AssertionError.class, () -> new Todo("   "));
    }

    @Test
    void deadlineConstructor_nullDate_throwsAssertionError() {
        assertThrows(AssertionError.class, () -> new Deadline("return book", null));
    }

    @Test
    void eventConstructor_blankStartOrEnd_throwsAssertionError() {
        assertThrows(AssertionError.class, () -> new Event("meeting", "", "4pm"));
        assertThrows(AssertionError.class, () -> new Event("meeting", "2pm", " "));
    }

    @Test
    void isScheduledOn_taskTypes_returnsWhetherTaskBelongsToDate() {
        LocalDate date = LocalDate.of(2019, 12, 2);

        assertFalse(new Todo("read book").isScheduledOn(date));
        assertTrue(new Deadline("submit report", date).isScheduledOn(date));
        assertTrue(new Event("dated meeting", "2019-12-02 2pm", "4pm")
                .isScheduledOn(date));
        assertFalse(new Event("legacy meeting", "Mon 2pm", "4pm")
                .isScheduledOn(date));
    }
}
