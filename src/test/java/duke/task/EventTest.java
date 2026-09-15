package duke.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

public class EventTest {

    @Test
    public void toDataString_notDone_correctFormat() {
        Event event = new Event(
                "Orientation camp",
                LocalDate.of(2026, 8, 1),
                LocalDate.of(2026, 8, 4)
        );
        assertEquals("E | 0 | Orientation camp | 2026-08-01 | 2026-08-04", event.toDataString());
    }

    @Test
    public void toDataString_done_correctFormat() {
        Event event = new Event(
                "Orientation camp",
                LocalDate.of(2026, 8, 1),
                LocalDate.of(2026, 8, 4)
        );
        event.markAsDone();
        assertEquals("E | 1 | Orientation camp | 2026-08-01 | 2026-08-04", event.toDataString());
    }

    @Test
    public void toString_notDone_correctFormat() {
        Event event = new Event(
                "Orientation camp",
                LocalDate.of(2026, 8, 1),
                LocalDate.of(2026, 8, 4)
        );
        assertEquals("[E] [ ] Orientation camp (from: 2026-08-01 to: 2026-08-04)", event.toString());
    }

    @Test
    public void toString_done_correctFormat() {
        Event event = new Event(
                "Orientation camp",
                LocalDate.of(2026, 8, 1),
                LocalDate.of(2026, 8, 4)
        );
        event.markAsDone();
        assertEquals("[E] [X] Orientation camp (from: 2026-08-01 to: 2026-08-04)", event.toString());
    }

    @Test
    public void toDataString_nullDates_printsNull() {
        Event event = new Event("Orientation camp", null, null);
        assertEquals("E | 0 | Orientation camp | null | null", event.toDataString());
    }

    @Test
    public void toString_nullDates_printsNull() {
        Event event = new Event("Orientation camp", null, null);
        assertEquals("[E] [ ] Orientation camp (from: null to: null)", event.toString());
    }

    @Test
    public void toString_emptyDescription_keepsExpectedSpacing() {
        Event event = new Event("", LocalDate.of(2026, 8, 1), LocalDate.of(2026, 8, 4));
        assertEquals("[E] [ ]  (from: 2026-08-01 to: 2026-08-04)", event.toString());
    }

    @Test
    public void hasSameDetails_sameDescriptionAndDatesIgnoringCase_returnsTrue() {
        Event first = new Event("Orientation Camp", LocalDate.of(2026, 8, 1), LocalDate.of(2026, 8, 4));
        Event second = new Event("orientation camp", LocalDate.of(2026, 8, 1), LocalDate.of(2026, 8, 4));
        second.markAsDone();

        assertTrue(first.hasSameDetails(second));
    }

    @Test
    public void hasSameDetails_differentStartOrEndDate_returnsFalse() {
        Event event = new Event("camp", LocalDate.of(2026, 8, 1), LocalDate.of(2026, 8, 4));
        Event differentStart = new Event("camp", LocalDate.of(2026, 8, 2), LocalDate.of(2026, 8, 4));
        Event differentEnd = new Event("camp", LocalDate.of(2026, 8, 1), LocalDate.of(2026, 8, 5));

        assertFalse(event.hasSameDetails(differentStart));
        assertFalse(event.hasSameDetails(differentEnd));
    }

    @Test
    public void hasSameDetails_differentDescriptionOrType_returnsFalse() {
        Event event = new Event("camp", LocalDate.of(2026, 8, 1), LocalDate.of(2026, 8, 4));
        Event differentDescription = new Event("meeting", LocalDate.of(2026, 8, 1), LocalDate.of(2026, 8, 4));

        assertFalse(event.hasSameDetails(differentDescription));
        assertFalse(event.hasSameDetails(new ToDo("camp")));
    }
}
