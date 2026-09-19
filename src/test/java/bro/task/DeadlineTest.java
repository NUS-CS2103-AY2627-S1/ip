package bro.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import bro.exception.BroException;

/**
 * Unit tests for {@link Deadline}.
 */
public class DeadlineTest {

    @Test
    public void constructor_validDate_success() throws BroException {
        Deadline deadline = new Deadline("submit assignment", "2026-08-28");
        assertNotNull(deadline);
        assertEquals("submit assignment", deadline.getDescription());
        assertEquals("submit assignment", deadline.getTask());
        assertFalse(deadline.isDone());
        assertNotNull(deadline.getDeadline());
    }

    @Test
    public void constructor_validDateTime_success() throws BroException {
        Deadline deadline = new Deadline("submit quiz", "28/8/2026 1800");
        assertNotNull(deadline);
        assertEquals("submit quiz", deadline.getDescription());
        assertEquals("submit quiz", deadline.getTask());
        assertFalse(deadline.isDone());
        assertTrue(deadline.getDeadline().hasTime());
    }

    @Test
    public void constructor_rawStringFallback_success() throws BroException {
        Deadline deadline = new Deadline("dinner reservation", "tonight");
        assertNotNull(deadline);
        assertEquals("tonight", deadline.getDeadline().getRawString());
    }

    @Test
    public void constructor_emptyDate_exceptionThrown() {
        assertThrows(BroException.class, () -> new Deadline("return book", ""));
        assertThrows(BroException.class, () -> new Deadline("return book", "   "));
        assertThrows(BroException.class, () -> new Deadline("return book", null));
    }

    @Test
    public void isOnDate_matchingDate_returnsTrue() throws BroException {
        Deadline deadline = new Deadline("submit report", "2026-09-15");
        LocalDate targetDate = LocalDate.of(2026, 9, 15);
        assertTrue(deadline.isOnDate(targetDate));
    }

    @Test
    public void isOnDate_differentDate_returnsFalse() throws BroException {
        Deadline deadline = new Deadline("submit report", "2026-09-15");
        LocalDate differentDate = LocalDate.of(2026, 9, 16);
        assertFalse(deadline.isOnDate(differentDate));
    }

    @Test
    public void isOnDate_rawStringDate_returnsFalse() throws BroException {
        Deadline deadline = new Deadline("submit report", "next week");
        LocalDate targetDate = LocalDate.of(2026, 9, 15);
        assertFalse(deadline.isOnDate(targetDate));
    }

    @Test
    public void toString_uncompletedDeadline_formattedStringReturned() throws BroException {
        Deadline deadline = new Deadline("submit report", "2026-09-15");
        assertEquals("[D][ ] submit report (by: Sep 15 2026)", deadline.toString());
    }

    @Test
    public void toString_completedDeadlineWithTime_formattedStringReturned() throws BroException {
        Deadline deadline = new Deadline("submit quiz", "2026-09-15 1800");
        deadline.markDone();
        assertEquals("[D][X] submit quiz (by: Sep 15 2026, 6:00PM)", deadline.toString());
    }

    @Test
    public void toFileFormat_uncompletedDeadline_formattedStringReturned() throws BroException {
        Deadline deadline = new Deadline("submit report", "2026-09-15");
        assertEquals("D | 0 | submit report | 2026-09-15", deadline.toFileFormat());
    }

    @Test
    public void toFileFormat_completedDeadlineWithTime_formattedStringReturned() throws BroException {
        Deadline deadline = new Deadline("submit quiz", "2026-09-15 1800");
        deadline.markDone();
        assertEquals("D | 1 | submit quiz | 2026-09-15 1800", deadline.toFileFormat());
    }

    @Test
    public void markDoneAndUnmarkDone_togglesCompletionStatus() throws BroException {
        Deadline deadline = new Deadline("read chapter", "2026-08-28");
        assertFalse(deadline.isDone());

        deadline.markDone();
        assertTrue(deadline.isDone());
        assertEquals("D | 1 | read chapter | 2026-08-28", deadline.toFileFormat());

        deadline.unmarkDone();
        assertFalse(deadline.isDone());
        assertEquals("D | 0 | read chapter | 2026-08-28", deadline.toFileFormat());
    }

    @Test
    public void containsKeywords_matchingKeywords_returnsTrue() throws BroException {
        Deadline deadline = new Deadline("Submit assignment for CS2103T", "2026-08-28");
        assertTrue(deadline.containsKeywords("submit"));
        assertTrue(deadline.containsKeywords("CS2103T"));
        assertTrue(deadline.containsKeywords("ASSIGNMENT"));
        assertFalse(deadline.containsKeywords("meeting"));
    }

    @Test
    public void setDescription_validDescription_descriptionUpdated() throws BroException {
        Deadline deadline = new Deadline("submit report", "2026-09-15");
        deadline.setDescription("submit final draft");
        assertEquals("submit final draft", deadline.getDescription());
        assertEquals("[D][ ] submit final draft (by: Sep 15 2026)", deadline.toString());
        assertEquals("D | 0 | submit final draft | 2026-09-15", deadline.toFileFormat());
    }

    @Test
    public void setDeadline_string_deadlineUpdated() throws BroException {
        Deadline deadline = new Deadline("submit report", "2026-09-15");
        deadline.setDeadline("2026-10-20 1800");
        assertEquals("[D][ ] submit report (by: Oct 20 2026, 6:00PM)", deadline.toString());
        assertEquals("D | 0 | submit report | 2026-10-20 1800", deadline.toFileFormat());
        assertTrue(deadline.isOnDate(LocalDate.of(2026, 10, 20)));
        assertFalse(deadline.isOnDate(LocalDate.of(2026, 9, 15)));
    }

    @Test
    public void setDeadline_taskDateTime_deadlineUpdated() throws BroException {
        Deadline deadline = new Deadline("submit report", "2026-09-15");
        TaskDateTime newDt = TaskDateTime.parse("2026-12-01");
        deadline.setDeadline(newDt);
        assertEquals("[D][ ] submit report (by: Dec 01 2026)", deadline.toString());
        assertTrue(deadline.isOnDate(LocalDate.of(2026, 12, 1)));
    }

    @Test
    public void setDeadline_invalidString_exceptionThrown() throws BroException {
        Deadline deadline = new Deadline("submit report", "2026-09-15");
        assertThrows(BroException.class, () -> deadline.setDeadline(""));
    }

    @Test
    public void equalsAndHashCode_matchingAndDifferent_behaveCorrectly() throws BroException {
        Deadline d1 = new Deadline("submit report", "2026-09-15");
        Deadline d2 = new Deadline("SUBMIT REPORT", "2026-09-15");
        Deadline d3 = new Deadline("submit report", "2026-09-16");
        Deadline d4 = new Deadline("other report", "2026-09-15");

        assertEquals(d1, d2);
        assertEquals(d1.hashCode(), d2.hashCode());
        assertFalse(d1.equals(d3));
        assertFalse(d1.equals(d4));
        assertFalse(d1.equals(null));
    }
}

