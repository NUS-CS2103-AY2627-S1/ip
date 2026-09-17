package lebron.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

public class EventTest {
    private final LocalDateTime start = LocalDateTime.of(2019, 10, 16, 9, 0);
    private final LocalDateTime end = LocalDateTime.of(2019, 10, 16, 11, 0);

    @Test
    public void equals_sameDescriptionAndDates_returnsTrue() {
        assertEquals(new Event("project meeting", start, end), new Event("project meeting", start, end));
    }

    @Test
    public void equals_differentEndDate_returnsFalse() {
        Event other = new Event("project meeting", start, LocalDateTime.of(2019, 10, 16, 12, 0));
        assertNotEquals(new Event("project meeting", start, end), other);
    }

    @Test
    public void toString_notDone_correctFormatWithHumanReadableDates() {
        Event event = new Event("project meeting", start, end);
        assertEquals("[E][ ] project meeting (from Oct 16 2019, 9:00 AM til Oct 16 2019, 11:00 AM)", event.toString());
    }

    @Test
    public void toString_done_correctFormat() {
        Event event = new Event("project meeting", start, end);
        event.setStatus(true);
        assertEquals("[E][X] project meeting (from Oct 16 2019, 9:00 AM til Oct 16 2019, 11:00 AM)", event.toString());
    }

    @Test
    public void reformat_notDone_correctSaveFormatWithIsoDates() {
        Event event = new Event("project meeting", start, end);
        assertEquals("E | 0 | project meeting | 2019-10-16T09:00 | 2019-10-16T11:00", event.reformat());
    }
}
