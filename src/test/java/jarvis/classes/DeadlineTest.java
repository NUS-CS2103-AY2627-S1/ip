package jarvis.classes;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

/** Tests the behavior of {@link Deadline}. */
public class DeadlineTest {
    /** Verifies that a deadline with a date only formats correctly. */
    @Test
    public void constructor_dateOnlyDeadlineFormatsCorrectly() {
        Deadline deadline = new Deadline("Read book", LocalDateTime.of(2026, 8, 22, 0, 0));

        assertEquals(Task.CompletionStatus.UNDONE, deadline.getStatus());
        assertEquals("[D][] Read book (by: 08 22 2026)", deadline.toString());
    }

    /** Verifies that a date only deadline can be marked as done and displays the done marker. */
    @Test
    public void setCompletionStatus_doneDateOnlyDeadlineDisplaysDoneMarker() {
        Deadline deadline = new Deadline("Read book", LocalDateTime.of(2026, 8, 22, 0, 0));

        deadline.setCompletionStatus(Task.CompletionStatus.DONE);

        assertEquals(Task.CompletionStatus.DONE, deadline.getStatus());
        assertEquals("[D][X] Read book (by: 08 22 2026)", deadline.toString());
    }

    /** Verifies that a date only completed deadline can be marked undone again. */
    @Test
    public void setCompletionStatus_undoneDateOnlyDeadlineDisplaysUndoneMarker() {
        Deadline deadline = new Deadline("Read book", LocalDateTime.of(2026, 8, 22, 0, 0));

        deadline.setCompletionStatus(Task.CompletionStatus.DONE);
        deadline.setCompletionStatus(Task.CompletionStatus.UNDONE);

        assertEquals(Task.CompletionStatus.UNDONE, deadline.getStatus());
        assertEquals("[D][] Read book (by: 08 22 2026)", deadline.toString());
    }

    /** Verifies that a deadline with a time formats correctly. */
    @Test
    public void constructor_dateAndTimeDeadlineFormatsCorrectly() {
        Deadline deadline = new Deadline("Read book", LocalDateTime.of(2026, 8, 22, 23, 45));

        assertEquals("[D][] Read book (by: 08 22 2026 23:45)", deadline.toString());
    }

    /** Verifies that a date and time deadline can be marked as done and displays the done marker. */
    @Test
    public void setCompletionStatus_doneDateAndTimeDeadlineDisplaysDoneMarker() {
        Deadline deadline = new Deadline("Read book", LocalDateTime.of(2026, 8, 22, 23, 45));

        deadline.setCompletionStatus(Task.CompletionStatus.DONE);

        assertEquals(Task.CompletionStatus.DONE, deadline.getStatus());
        assertEquals("[D][X] Read book (by: 08 22 2026 23:45)", deadline.toString());
    }

    /** Verifies that a completed date and time data can be marked undone again. */
    @Test
    public void setCompletionStatus_undoneDateAndTimeDeadlineDisplaysUndoneMarker() {
        Deadline deadline = new Deadline("Read book", LocalDateTime.of(2026, 8, 22, 23, 45));

        deadline.setCompletionStatus(Task.CompletionStatus.DONE);
        deadline.setCompletionStatus(Task.CompletionStatus.UNDONE);

        assertEquals(Task.CompletionStatus.UNDONE, deadline.getStatus());
        assertEquals("[D][] Read book (by: 08 22 2026 23:45)", deadline.toString());
    }
}
