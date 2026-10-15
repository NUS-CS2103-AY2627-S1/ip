package zen.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;


public class DeadlineTest {
    private static final LocalDateTime DATE_TIME = LocalDateTime.of(2026, 10, 10, 10, 30, 0);
    private static final LocalDate DATE = DATE_TIME.toLocalDate();
    private static final String DESCRIPTION = "assignment";

    @Test
    public void toStorageString_isNotDone_formatsCorrectly() {
        Deadline deadline = new Deadline(DESCRIPTION, DATE_TIME);
        String expectedOutput = String.format("D | %d | %s | %s | none", 0, DESCRIPTION, DATE_TIME);
        assertEquals(expectedOutput, deadline.toStorageString());
    }

    @Test
    public void toStorageString_isDone_formatsCorrectly() {
        Deadline deadline = new Deadline(DESCRIPTION, DATE_TIME);
        deadline.markAsDone();
        String expectedOutput = String.format("D | %d | %s | %s | none", 1, DESCRIPTION, DATE_TIME);
        assertEquals(expectedOutput, deadline.toStorageString());
    }

    @Test
    public void occursOn_sameDate_returnsTrue() {
        Deadline deadline = new Deadline(DESCRIPTION, DATE_TIME);
        assertTrue(deadline.occursOn(DATE));
    }

    @Test
    public void occursOn_previousDate_returnsFalse() {
        Deadline deadline = new Deadline(DESCRIPTION, DATE_TIME);
        assertFalse(deadline.occursOn(DATE.minusDays(1)));
    }

    @Test
    public void occursOn_nextDate_returnsFalse() {
        Deadline deadline = new Deadline(DESCRIPTION, DATE_TIME);
        assertFalse(deadline.occursOn(DATE.plusDays(1)));
    }

    @Test
    public void occursOn_lateNightDeadline_matchesCorrectDate() {
        LocalDateTime lateNight = LocalDateTime.of(2026, 10, 10, 23, 59);
        Deadline deadline = new Deadline(DESCRIPTION, lateNight);
        assertTrue(deadline.occursOn(LocalDate.of(2026, 10, 10)));
        assertFalse(deadline.occursOn(LocalDate.of(2026, 10, 11)));
    }
}
