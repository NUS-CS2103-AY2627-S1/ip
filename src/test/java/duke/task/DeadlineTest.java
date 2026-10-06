package duke.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import duke.exception.DukeException;

/**
 * Unit tests for the {@link Deadline} class.
 */
public class DeadlineTest {

    @Test
    public void constructor_validDateAndTime_success() throws DukeException {
        Deadline deadline = new Deadline("submit report", "2026-09-01 1800");
        assertEquals("D | 0 | submit report | 2026-09-01T18:00", deadline.toFileFormat());
        assertEquals("submit report (by: Sep 1 2026, 6:00 PM)", deadline.toString());
    }

    @Test
    public void constructor_validDateOnly_success() throws DukeException {
        Deadline deadline = new Deadline("submit report", "2026-09-01");
        assertEquals("D | 0 | submit report | 2026-09-01T00:00", deadline.toFileFormat());
        assertEquals("submit report (by: Sep 1 2026, 12:00 AM)", deadline.toString());
    }

    @Test
    public void constructor_validIsoDateTime_success() throws DukeException {
        Deadline deadline = new Deadline("submit report", "2026-09-01T18:00");
        assertEquals("D | 0 | submit report | 2026-09-01T18:00", deadline.toFileFormat());
        assertEquals("submit report (by: Sep 1 2026, 6:00 PM)", deadline.toString());
    }

    @Test
    public void constructor_invalidDateFormat_exceptionThrown() {
        DukeException exception = assertThrows(DukeException.class, () ->
                new Deadline("submit report", "01-09-2026"));
        assertEquals(
                "Invalid date format. Please use yyyy-MM-dd or yyyy-MM-dd HHmm (e.g., 2019-10-15 1800).",
                exception.getMessage());
    }

    @Test
    public void constructor_invalidDateValues_exceptionThrown() {
        DukeException exception = assertThrows(DukeException.class, () ->
                new Deadline("submit report", "2026-13-01 1800"));
        assertEquals(
                "Invalid date format. Please use yyyy-MM-dd or yyyy-MM-dd HHmm (e.g., 2019-10-15 1800).",
                exception.getMessage());
    }

    @Test
    public void getTaskIcon_returnsD() throws DukeException {
        Deadline deadline = new Deadline("read book", "2026-09-01 1200");
        assertEquals("D", deadline.getTaskIcon());
    }

    @Test
    public void toFileFormat_unmarkedDeadline_formattedCorrectly() throws DukeException {
        Deadline deadline = new Deadline("return book", "2026-09-05 2359");
        assertEquals("D | 0 | return book | 2026-09-05T23:59", deadline.toFileFormat());
    }

    @Test
    public void toFileFormat_markedDeadline_formattedCorrectly() throws DukeException {
        Deadline deadline = new Deadline("return book", "2026-09-05 2359");
        deadline.markAsDone();
        assertEquals("D | 1 | return book | 2026-09-05T23:59", deadline.toFileFormat());
    }

    @Test
    public void toString_unmarkedDeadline_formattedCorrectly() throws DukeException {
        Deadline deadline = new Deadline("finish homework", "2026-10-15 1500");
        assertEquals("finish homework (by: Oct 15 2026, 3:00 PM)", deadline.toString());
    }

    @Test
    public void toString_markedDeadline_formattedCorrectly() throws DukeException {
        Deadline deadline = new Deadline("finish homework", "2026-10-15 1500");
        deadline.markAsDone();
        assertEquals("finish homework (by: Oct 15 2026, 3:00 PM)", deadline.toString());
        assertEquals("X", deadline.getStatusIcon());
    }

    @Test
    public void markAsNotDone_markedDeadline_resetsStatusIcon() throws DukeException {
        Deadline deadline = new Deadline("finish homework", "2026-10-15 1500");
        deadline.markAsDone();
        assertEquals("X", deadline.getStatusIcon());
        deadline.markAsNotDone();
        assertEquals(" ", deadline.getStatusIcon());
    }

    @Test
    public void equals_andHashCode_operateCorrectly() throws DukeException {
        Deadline d1 = new Deadline("submit report", "2026-09-01 1800");
        Deadline d2 = new Deadline("submit report", "2026-09-01 1800");
        Deadline d3 = new Deadline("other report", "2026-09-01 1800");
        Deadline d4 = new Deadline("submit report", "2026-09-02 1800");

        assertEquals(d1, d2);
        assertEquals(d1.hashCode(), d2.hashCode());
        org.junit.jupiter.api.Assertions.assertNotEquals(d1, d3);
        org.junit.jupiter.api.Assertions.assertNotEquals(d1, d4);
        org.junit.jupiter.api.Assertions.assertNotEquals(d1, null);
    }
}
