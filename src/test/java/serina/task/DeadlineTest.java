package serina.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

/** Tests deadline date behavior, formatting, and copying. */
public class DeadlineTest {
    private static final LocalDate DATE = LocalDate.of(2028, 2, 29);

    @Test
    public void deadline_datesAndFormats_returnExpectedValues() {
        Deadline deadline = new Deadline("submit report", DATE);

        assertEquals(TaskType.DEADLINE, deadline.getTaskType());
        assertTrue(deadline.isOccurringOn(DATE));
        assertFalse(deadline.isOccurringOn(DATE.minusDays(1)));
        assertEquals("[D][ ] submit report (by: Feb 29 2028)", deadline.toString());
        assertEquals("D | 0 | submit report | 2028-02-29", deadline.toFileString());
    }

    @Test
    public void copy_markingCopy_doesNotChangeOriginal() {
        Deadline original = new Deadline("submit report", DATE, TaskStatus.DONE);
        Task copy = original.copy();

        copy.markAsNotDone();

        assertTrue(original.isDone());
        assertFalse(copy.isDone());
    }
}
