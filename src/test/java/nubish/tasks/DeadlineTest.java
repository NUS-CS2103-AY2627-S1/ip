package nubish.tasks;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

import org.junit.jupiter.api.Test;

/**
 * Tests printDeadline task status, saving, and date parsing behavior.
 */
public class DeadlineTest {
    /**
     * Verifies that a new printDeadline task starts as not done.
     */
    @Test
    public void getStatusIcon_newDeadline_returnsUndoneIcon() {
        Task task = new Deadline("read book", "12/10/2020 1800");

        assertEquals(" ", task.getStatusIcon());
    }

    /**
     * Verifies that a loaded done printDeadline task shows the done icon.
     */
    @Test
    public void getStatusIcon_savedDoneDeadline_returnsDoneIcon() {
        Task task = new Deadline(true, "read book", "12/10/2020 1800");

        assertEquals("X", task.getStatusIcon());
    }

    /**
     * Verifies that a loaded undone printDeadline task shows the undone icon.
     */
    @Test
    public void getStatusIcon_savedUndoneDeadline_returnsUndoneIcon() {
        Task task = new Deadline(false, "read book", "12/10/2020 1800");

        assertEquals(" ", task.getStatusIcon());
    }

    /**
     * Verifies that marking a printDeadline task changes its status icon to done.
     */
    @Test
    public void getStatusIcon_markedDeadline_returnsDoneIcon() {
        Task task = new Deadline("read book", "12/10/2020 1800");

        task.markAsDone();

        assertEquals("X", task.getStatusIcon());
    }

    /**
     * Verifies that unmarking a printDeadline task changes its status icon to undone.
     */
    @Test
    public void getStatusIcon_unmarkedTask_returnsUndoneIcon() {
        Task task = new Deadline(true, "read book", "12/10/2020 1800");

        task.unmarkAsDone();

        assertEquals(" ", task.getStatusIcon());
    }

    /**
     * Verifies that an undone printDeadline task is saved in the expected file format.
     */
    @Test
    public void saveString_unmarkedDeadline_returnsCorrectFormat() {
        Task task = new Deadline("read book", "12/10/2020 1800");

        assertEquals("D | 0 | read book | 12/10/2020 1800", task.saveString());
    }

    /**
     * Verifies that a done printDeadline task is saved in the expected file format.
     */
    @Test
    public void saveString_markedDeadline_returnsCorrectFormat() {
        Task task = new Deadline(true, "read book", "12/10/2020 1800");

        assertEquals("D | 1 | read book | 12/10/2020 1800", task.saveString());
    }

    /**
     * Verifies that the deadline date-time is exposed after parsing.
     */
    @Test
    public void getDeadline_validDeadline_returnsParsedDateTime() {
        Deadline deadline = new Deadline("read book", "12/10/2020 1800");

        assertEquals(LocalDateTime.of(2020, 10, 12, 18, 0), deadline.getDeadline());
    }

    /**
     * Verifies that a printDeadline task is displayed in the expected user-facing format.
     */
    @Test
    public void toString_validDeadline_returnsFormattedDeadline() {
        Task task = new Deadline("read book", "12/10/2020 1800");

        assertEquals("[D][ ] read book (by: 1800 Oct 12 2020)", task.toString());
    }

    /**
     * Verifies that deadlines must use the expected date-time format.
     */
    @Test
    public void convertToDateTime_throwsErrorWhenWrongFormatIsUsed() {
        assertThrows(DateTimeParseException.class, () -> new Deadline("read book", "12/10/20 1800"));
    }

    /**
     * Verifies that deadlines reject non-existent calendar dates.
     */
    @Test
    public void convertToDateTime_nonExistentDate_throwsException() {
        assertThrows(DateTimeParseException.class, () -> new Deadline("read book", "30/02/2020 1800"));
    }
}
