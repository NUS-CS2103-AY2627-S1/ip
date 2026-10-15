package jiji.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link Deadline}.
 */
public class DeadlineTest {

    @Test
    public void constructor_validDate_success() {
        Deadline deadline = new Deadline("return book", "2026-08-30");
        assertEquals("return book", deadline.getDescription());
        assertEquals(LocalDate.of(2026, 8, 30), deadline.getDeadlineDate());
        assertFalse(deadline.isDone());
        assertEquals("[D][ ] return book (by: Aug 30 2026)", deadline.toString());
        assertEquals("D | 0 | return book | 2026-08-30", deadline.toFileFormat());
    }

    @Test
    public void constructor_validDateTime_success() {
        Deadline deadline = new Deadline("submit project", "2026-08-30 1800");
        assertEquals("submit project", deadline.getDescription());
        assertEquals(LocalDate.of(2026, 8, 30), deadline.getDeadlineDate());
        assertEquals("[D][ ] submit project (by: Aug 30 2026, 6:00PM)", deadline.toString());
        assertEquals("D | 0 | submit project | 2026-08-30 1800", deadline.toFileFormat());
    }

    @Test
    public void constructor_rawStringDate_success() {
        Deadline deadline = new Deadline("do homework", "Sunday night");
        assertNull(deadline.getDeadlineDate());
        assertEquals("[D][ ] do homework (by: Sunday night)", deadline.toString());
        assertEquals("D | 0 | do homework | Sunday night", deadline.toFileFormat());
    }

    @Test
    public void markAndUnmark_updatesStatusAndFileFormat() {
        Deadline deadline = new Deadline("return book", "2026-08-30");
        deadline.markAsDone();
        assertTrue(deadline.isDone());
        assertEquals("[D][X] return book (by: Aug 30 2026)", deadline.toString());
        assertEquals("D | 1 | return book | 2026-08-30", deadline.toFileFormat());

        deadline.markAsNotDone();
        assertFalse(deadline.isDone());
        assertEquals("[D][ ] return book (by: Aug 30 2026)", deadline.toString());
        assertEquals("D | 0 | return book | 2026-08-30", deadline.toFileFormat());
    }

    @Test
    public void isOverdue_correctlyEvaluatesOverdueStatus() {
        Deadline deadline = new Deadline("submit tax", "2026-08-15");
        LocalDate today = LocalDate.of(2026, 8, 20);
        LocalDate beforeDeadline = LocalDate.of(2026, 8, 10);
        LocalDate exactDeadline = LocalDate.of(2026, 8, 15);

        assertTrue(deadline.isOverdue(today));
        assertFalse(deadline.isOverdue(beforeDeadline));
        assertFalse(deadline.isOverdue(exactDeadline));
        assertFalse(deadline.isOverdue(null));

        deadline.markAsDone();
        assertFalse(deadline.isOverdue(today));
    }

    @Test
    public void isOverdue_nonDateDeadline_returnsFalse() {
        Deadline deadline = new Deadline("read comic", "tomorrow");
        assertFalse(deadline.isOverdue(LocalDate.now()));
    }

    @Test
    public void constructor_nullOrBlankDescription_throwsAssertionError() {
        assertThrows(AssertionError.class, () -> new Deadline(null, "2026-08-30"));
        assertThrows(AssertionError.class, () -> new Deadline("   ", "2026-08-30"));
    }

    @Test
    public void constructor_nullOrBlankBy_throwsAssertionError() {
        assertThrows(AssertionError.class, () -> new Deadline("return book", null));
        assertThrows(AssertionError.class, () -> new Deadline("return book", "   "));
    }
}
