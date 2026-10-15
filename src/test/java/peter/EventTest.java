package peter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

/**
 * Tests for {@link Event}.
 */
public class EventTest {
    @Test
    public void getFrom_returnsConstructorValue() {
        LocalDateTime from = LocalDateTime.of(2019, 12, 2, 14, 0);
        Event event = new Event("project meeting", from, LocalDateTime.of(2019, 12, 2, 16, 0));
        assertEquals(from, event.getFrom());
    }

    @Test
    public void constructor_endNotAfterStart_throwsAssertionError() {
        // Gradle's test task runs with assertions enabled, so this documented
        // invariant is actually checked here.
        assertThrows(AssertionError.class, () -> new Event("meeting",
                LocalDateTime.of(2019, 12, 2, 16, 0), LocalDateTime.of(2019, 12, 2, 14, 0)));
    }

    @Test
    public void toFileFormat_notDone_usesEventTypeCodeAndBothDates() {
        Event event = new Event("project meeting",
                LocalDateTime.of(2019, 12, 2, 14, 0), LocalDateTime.of(2019, 12, 2, 16, 0));
        assertEquals("E | 0 | project meeting | 2019-12-02T1400 | 2019-12-02T1600", event.toFileFormat());
    }

    @Test
    public void toFileFormat_done_usesOneMarker() {
        Event event = new Event("project meeting",
                LocalDateTime.of(2019, 12, 2, 14, 0), LocalDateTime.of(2019, 12, 2, 16, 0));
        event.markAsDone();
        assertEquals("E | 1 | project meeting | 2019-12-02T1400 | 2019-12-02T1600", event.toFileFormat());
    }

    @Test
    public void toString_neitherTimeAtMidnight_showsBothTimes() {
        Event event = new Event("project meeting",
                LocalDateTime.of(2019, 12, 2, 14, 0), LocalDateTime.of(2019, 12, 2, 16, 0));
        assertEquals("[E][ ] project meeting (from: Dec 02 2019, 2:00PM to: Dec 02 2019, 4:00PM)", event.toString());
    }

    @Test
    public void toString_startAtMidnight_omitsStartTimeButKeepsEndTime() {
        // Each end of the range is formatted independently, so a midnight
        // start alongside a non-midnight end should only omit the start time.
        Event event = new Event("all-day setup",
                LocalDateTime.of(2019, 12, 2, 0, 0), LocalDateTime.of(2019, 12, 2, 16, 0));
        assertEquals("[E][ ] all-day setup (from: Dec 02 2019 to: Dec 02 2019, 4:00PM)", event.toString());
    }

    @Test
    public void equals_sameDescriptionFromAndTo_returnsTrue() {
        LocalDateTime from = LocalDateTime.of(2019, 12, 2, 14, 0);
        LocalDateTime to = LocalDateTime.of(2019, 12, 2, 16, 0);
        assertEquals(new Event("meeting", from, to), new Event("meeting", from, to));
    }

    @Test
    public void equals_differentTo_returnsFalse() {
        LocalDateTime from = LocalDateTime.of(2019, 12, 2, 14, 0);
        Event first = new Event("meeting", from, LocalDateTime.of(2019, 12, 2, 16, 0));
        Event second = new Event("meeting", from, LocalDateTime.of(2019, 12, 2, 17, 0));
        assertFalse(first.equals(second));
    }

    @Test
    public void equals_differentFrom_returnsFalse() {
        LocalDateTime to = LocalDateTime.of(2019, 12, 2, 16, 0);
        Event first = new Event("meeting", LocalDateTime.of(2019, 12, 2, 14, 0), to);
        Event second = new Event("meeting", LocalDateTime.of(2019, 12, 2, 15, 0), to);
        assertFalse(first.equals(second));
    }

    @Test
    public void hashCode_equalEvents_haveSameHashCode() {
        LocalDateTime from = LocalDateTime.of(2019, 12, 2, 14, 0);
        LocalDateTime to = LocalDateTime.of(2019, 12, 2, 16, 0);
        assertEquals(new Event("meeting", from, to).hashCode(), new Event("meeting", from, to).hashCode());
    }
}
