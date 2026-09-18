package alfred.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

/**
 * Tests date parsing, display, save format, and ordering on {@link TaskDateTime}.
 */
public class TaskDateTimeTest {
    @Test
    public void parseUserInput_isoDate_returnsDateOnly() {
        TaskDateTime parsed = TaskDateTime.parseUserInput("2019-10-15");
        assertNotNull(parsed);
        assertEquals(LocalDate.of(2019, 10, 15), parsed.toLocalDate());
        assertEquals("Oct 15 2019", parsed.toDisplayString());
        assertEquals("2019-10-15", parsed.toSaveString());
    }

    @Test
    public void parseUserInput_slashDate_returnsDateOnly() {
        TaskDateTime parsed = TaskDateTime.parseUserInput("2/12/2019");
        assertNotNull(parsed);
        assertEquals(LocalDate.of(2019, 12, 2), parsed.toLocalDate());
        assertEquals("Dec 02 2019", parsed.toDisplayString());
    }

    @Test
    public void parseUserInput_isoDateTimeHmm_returnsDateTime() {
        TaskDateTime parsed = TaskDateTime.parseUserInput("2019-10-15 1800");
        assertNotNull(parsed);
        assertEquals("Oct 15 2019, 6:00PM", parsed.toDisplayString());
        assertEquals("2019-10-15T18:00:00", parsed.toSaveString());
        assertEquals("Oct 15 2019", parsed.toDisplayDate());
    }

    @Test
    public void parseUserInput_isoDateTimeColon_returnsDateTime() {
        TaskDateTime parsed = TaskDateTime.parseUserInput("2019-10-15 18:00");
        assertNotNull(parsed);
        assertEquals("Oct 15 2019, 6:00PM", parsed.toDisplayString());
    }

    @Test
    public void parseUserInput_slashDateTime_returnsDateTime() {
        TaskDateTime parsed = TaskDateTime.parseUserInput("2/12/2019 1800");
        assertNotNull(parsed);
        assertEquals("Dec 02 2019, 6:00PM", parsed.toDisplayString());
    }

    @Test
    public void parseUserInput_invalidText_returnsNull() {
        assertNull(TaskDateTime.parseUserInput("Sunday"));
        assertNull(TaskDateTime.parseUserInput("15-10-2019"));
        assertNull(TaskDateTime.parseUserInput("2019/10/15"));
    }

    @Test
    public void parseUserInput_impossibleDate_returnsNull() {
        assertNull(TaskDateTime.parseUserInput("2019-02-31"));
        assertNull(TaskDateTime.parseUserInput("31/2/2019"));
        assertNull(TaskDateTime.parseUserInput("2019-02-29"));
    }

    @Test
    public void parseUserInput_blankOrNull_returnsNull() {
        assertNull(TaskDateTime.parseUserInput(null));
        assertNull(TaskDateTime.parseUserInput(""));
        assertNull(TaskDateTime.parseUserInput("   "));
    }

    @Test
    public void parseSaved_isoDate_roundTrips() {
        TaskDateTime parsed = TaskDateTime.parseSaved("2019-10-15");
        assertNotNull(parsed);
        assertEquals("2019-10-15", parsed.toSaveString());
        assertEquals("Oct 15 2019", parsed.toDisplayString());
    }

    @Test
    public void parseSaved_isoDateTime_roundTrips() {
        TaskDateTime parsed = TaskDateTime.parseSaved("2019-10-15T18:00:00");
        assertNotNull(parsed);
        assertEquals("2019-10-15T18:00:00", parsed.toSaveString());
        assertEquals("Oct 15 2019, 6:00PM", parsed.toDisplayString());
    }

    @Test
    public void parseSaved_invalid_returnsNull() {
        assertNull(TaskDateTime.parseSaved(null));
        assertNull(TaskDateTime.parseSaved(""));
        assertNull(TaskDateTime.parseSaved("2019-10-15 18:00"));
        assertNull(TaskDateTime.parseSaved("not-a-date"));
    }

    @Test
    public void isAfter_laterInstant_returnsTrue() {
        TaskDateTime earlier = TaskDateTime.parseUserInput("2019-10-15");
        TaskDateTime later = TaskDateTime.parseUserInput("2019-10-16");
        assertNotNull(earlier);
        assertNotNull(later);
        assertTrue(later.isAfter(earlier));
        assertFalse(earlier.isAfter(later));
        assertFalse(earlier.isAfter(earlier));
    }

    @Test
    public void isAfter_sameDateDifferentTime_comparesInstants() {
        TaskDateTime morning = TaskDateTime.parseUserInput("2019-10-15 0900");
        TaskDateTime evening = TaskDateTime.parseUserInput("2019-10-15 1800");
        assertNotNull(morning);
        assertNotNull(evening);
        assertTrue(evening.isAfter(morning));
        assertFalse(morning.isAfter(evening));
    }
}
