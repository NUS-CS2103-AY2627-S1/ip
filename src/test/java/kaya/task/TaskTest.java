package kaya.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.ResourceLock;

/**
 * Checks task display, completion status, and independent copies of every task type.
 */
public class TaskTest {
    @Test
    public void completion_repeatedMarkAndUnmark_keepsStatusAndDisplayConsistent() {
        Task task = new Todo("read book");
        assertFalse(task.isDone());
        assertEquals("[T][ ] read book", task.toString());

        task.markAsDone();
        task.markAsDone();
        assertTrue(task.isDone());
        assertEquals("X", task.getStatusIcon());
        assertEquals("[T][X] read book", task.toString());

        task.markAsNotDone();
        task.markAsNotDone();
        assertFalse(task.isDone());
        assertEquals(" ", task.getStatusIcon());
    }

    @Test
    public void copy_everyTaskType_preservesDetailsWithoutSharingCompletionStatus() {
        List<Task> tasks = List.of(new Task("base"), new Todo("read book"),
                new Deadline("report", LocalDate.of(2026, 9, 20)),
                new Event("meeting", LocalDate.of(2026, 9, 20), LocalDate.of(2026, 9, 21)));
        for (Task original : tasks) {
            Task incompleteCopy = original.copy();
            assertNotSame(original, incompleteCopy);
            assertEquals(original.getClass(), incompleteCopy.getClass());
            assertEquals(original.toString(), incompleteCopy.toString());
            incompleteCopy.markAsDone();
            assertFalse(original.isDone());

            original.markAsDone();
            Task completedCopy = original.copy();
            assertTrue(completedCopy.isDone());
            assertEquals(original.toString(), completedCopy.toString());
            completedCopy.markAsNotDone();
            assertTrue(original.isDone());
        }
    }

    @Test
    @ResourceLock("default-locale")
    public void toString_chineseFormatLocale_keepsEnglishDatesAndUnicodeDescriptions() {
        Locale previous = Locale.getDefault(Locale.Category.FORMAT);
        try {
            Locale.setDefault(Locale.Category.FORMAT, Locale.CHINA);
            Task deadline = new Deadline("交报告", LocalDate.of(2026, 9, 20));
            Task event = new Event("讨论", LocalDate.of(2026, 9, 20), LocalDate.of(2026, 9, 21));

            assertEquals("[D][ ] 交报告 (by: Sep 20 2026)", deadline.toString());
            assertEquals("[E][ ] 讨论 (from: Sep 20 2026 to: Sep 21 2026)", event.toString());
        } finally {
            Locale.setDefault(Locale.Category.FORMAT, previous);
        }
    }
}
