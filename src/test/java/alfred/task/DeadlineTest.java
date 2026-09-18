package alfred.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

/**
 * Tests {@link Deadline#occursOn(LocalDate)} and save format.
 */
public class DeadlineTest {
    @Test
    public void occursOn_matchingCalendarDate_returnsTrue() {
        Deadline deadline = new Deadline("return book", date("2019-10-15"));
        assertTrue(deadline.occursOn(LocalDate.of(2019, 10, 15)));
        assertFalse(deadline.occursOn(LocalDate.of(2019, 10, 16)));
        assertFalse(deadline.occursOn(LocalDate.of(2019, 10, 14)));
    }

    @Test
    public void occursOn_dateTimeDeadline_matchesDateOnly() {
        Deadline deadline = new Deadline("submit report", date("2019-10-15 1800"));
        assertTrue(deadline.occursOn(LocalDate.of(2019, 10, 15)));
        assertFalse(deadline.occursOn(LocalDate.of(2019, 10, 16)));
    }

    @Test
    public void toSaveFormat_pendingDateOnly() {
        Deadline deadline = new Deadline("return book", date("2019-10-15"));
        assertEquals("D | 0 | return book | 2019-10-15 | UA", deadline.toSaveFormat());
        assertEquals("[D][ ] return book (by: Oct 15 2019)", deadline.getDisplayText());
    }

    private static TaskDateTime date(String text) {
        TaskDateTime parsed = TaskDateTime.parseUserInput(text);
        assertNotNull(parsed);
        return parsed;
    }
}
