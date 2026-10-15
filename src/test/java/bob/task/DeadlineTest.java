package bob.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import bob.exception.BobException;

/**
 * Tests the behavior of {@link Deadline}.
 */
public class DeadlineTest {

    @Test
    public void toString_unmarkedAndMarked_formattedCorrectly() {
        LocalDateTime time = LocalDateTime.of(2026, 11, 11, 18, 45);
        Deadline deadline = new Deadline("submit assignment", time);

        assertEquals("[D][ ] submit assignment (by: 11 November 2026, 1845 hrs)", deadline.toString());

        deadline.mark();
        assertEquals("[D][X] submit assignment (by: 11 November 2026, 1845 hrs)", deadline.toString());
    }

    @Test
    public void export_unmarkedAndMarked_formattedCorrectly() {
        LocalDateTime time = LocalDateTime.of(2026, 11, 11, 18, 45);
        Deadline deadline = new Deadline("submit assignment", time);

        assertEquals("D\u00010\u0001submit assignment\u00012026-11-11T18:45:00", deadline.export());

        deadline.mark();
        assertEquals("D\u00011\u0001submit assignment\u00012026-11-11T18:45:00", deadline.export());
    }

    @Test
    public void update_validFields_updatesAndPreservesDoneStatus() throws BobException {
        LocalDateTime initialTime = LocalDateTime.of(2026, 11, 11, 18, 45);
        Deadline deadline = new Deadline("submit assignment", initialTime);
        deadline.mark();

        LocalDateTime newTime = LocalDateTime.of(2026, 12, 12, 23, 59);
        Task updatedBoth = deadline.update("submit final assignment", newTime, null, null);
        assertEquals("[D][X] submit final assignment (by: 12 December 2026, 2359 hrs)", updatedBoth.toString());

        Task updatedDescOnly = deadline.update("submit revised assignment", null, null, null);
        assertEquals("[D][X] submit revised assignment (by: 11 November 2026, 1845 hrs)", updatedDescOnly.toString());

        Task updatedDeadlineOnly = deadline.update(null, newTime, null, null);
        assertEquals("[D][X] submit assignment (by: 12 December 2026, 2359 hrs)", updatedDeadlineOnly.toString());
    }

    @Test
    public void update_incompatibleEventFlags_throwsBobException() {
        LocalDateTime time = LocalDateTime.of(2026, 11, 11, 18, 45);
        Deadline deadline = new Deadline("submit assignment", time);

        assertThrows(BobException.class, () -> deadline.update(null, null, time, null));
        assertThrows(BobException.class, () -> deadline.update(null, null, null, time));
    }
}
