package thomas.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalTime;

import org.junit.jupiter.api.Test;

import thomas.ThomasException;

class DeadlineTest {
    @Test
    void constructor_parsesDateAndOptionalTime() throws ThomasException {
        Deadline deadline = new Deadline("submit report", "2026-08-26 1800");

        assertEquals(LocalDate.of(2026, 8, 26), deadline.getByDate());
        assertEquals(LocalTime.of(18, 0), deadline.getByTime());
    }

    @Test
    void occursOn_matchesOnlyDeadlineDate() throws ThomasException {
        Deadline deadline = new Deadline("submit report", "2026-08-26");

        assertTrue(deadline.occursOn(LocalDate.of(2026, 8, 26)));
        assertFalse(deadline.occursOn(LocalDate.of(2026, 8, 25)));
        assertFalse(deadline.occursOn(LocalDate.of(2026, 8, 27)));
    }

    @Test
    void toFileFormat_andToString_includeDeadlineData() throws ThomasException {
        Deadline deadline = new Deadline("submit report", "2026-08-26 1800");

        assertEquals("D | N | submit report | 2026-08-26 1800", deadline.toFileFormat());
        assertEquals("[D][ ] submit report (by: Aug 26 2026, 6:00pm)", deadline.toString());
    }
}
