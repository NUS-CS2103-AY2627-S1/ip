package dave.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for the {@link Deadline} class.
 */
public class DeadlineTest {

    @Test
    public void canSnooze_default_returnsTrue() {
        Deadline deadline = new Deadline("submit report", LocalDateTime.of(2026, 10, 1, 18, 0));
        assertTrue(deadline.canSnooze());
    }

    @Test
    public void snoozeTo_newDateTime_updatesDeadline() {
        Deadline deadline = new Deadline("submit report", LocalDateTime.of(2026, 10, 1, 18, 0));
        LocalDateTime newBy = LocalDateTime.of(2026, 10, 15, 23, 59);

        deadline.snoozeTo(newBy, true);
        assertEquals(newBy, deadline.getBy());
        assertTrue(deadline.hasTime());
        assertEquals("[D][ ] submit report (by: Oct 15 2026 23:59)", deadline.toString());
    }

    @Test
    public void snoozeBy_positiveDays_shiftsDeadlineForward() {
        Deadline deadline = new Deadline("submit report", LocalDateTime.of(2026, 10, 1, 18, 0));

        deadline.snoozeBy(3, ChronoUnit.DAYS);
        LocalDateTime expected = LocalDateTime.of(2026, 10, 4, 18, 0);
        assertEquals(expected, deadline.getBy());
        assertEquals("[D][ ] submit report (by: Oct 04 2026 18:00)", deadline.toString());
    }

    @Test
    public void toFileFormat_snoozedDeadline_serializesUpdatedDate() {
        Deadline deadline = new Deadline("submit report", LocalDateTime.of(2026, 10, 1, 18, 0));
        deadline.snoozeBy(1, ChronoUnit.DAYS);

        assertEquals("D | 0 | submit report | 2026-10-02 18:00", deadline.toFileFormat());
    }
}
