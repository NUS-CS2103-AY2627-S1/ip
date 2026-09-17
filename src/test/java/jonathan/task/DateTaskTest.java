package jonathan.task;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

public class DateTaskTest {

    @Test
    public void deadline_matchingDate_returnsTrue() {
        Deadlines deadline = new Deadlines("submit report", "2026-09-20");

        assertTrue(deadline.isOccuringOn(LocalDate.of(2026, 9, 20)));
        assertFalse(deadline.isOccuringOn(LocalDate.of(2026, 9, 21)));
    }

    @Test
    public void event_dateWithinRange_returnsTrue() {
        Event event = new Event(
                "conference",
                "2026-09-20",
                "2026-09-22");

        assertTrue(event.isOccuringOn(LocalDate.of(2026, 9, 20)));
        assertTrue(event.isOccuringOn(LocalDate.of(2026, 9, 21)));
        assertTrue(event.isOccuringOn(LocalDate.of(2026, 9, 22)));
        assertFalse(event.isOccuringOn(LocalDate.of(2026, 9, 23)));
    }
}
