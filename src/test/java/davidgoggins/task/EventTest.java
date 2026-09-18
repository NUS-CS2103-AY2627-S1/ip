package davidgoggins.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import davidgoggins.DavidGogginsException;

/** Tests the rules specific to {@link Event}: its two dates and their order. */
public class EventTest {

    @Test
    public void constructor_validDates_eventCreated() throws DavidGogginsException {
        Event event = new Event("camp", "2026-10-01", "2026-10-03");
        assertEquals("[E][ ] camp (from: 2026-10-01 to: 2026-10-03)", event.toString());
        assertEquals("E | 0 | camp | 2026-10-01 | 2026-10-03", event.toSaveFormat());
    }

    /** Dates carry no time of day, so a one-day event must be allowed. */
    @Test
    public void constructor_startAndEndOnSameDay_eventCreated() throws DavidGogginsException {
        assertEquals("[E][ ] exam (from: 2026-11-20 to: 2026-11-20)",
                new Event("exam", "2026-11-20", "2026-11-20").toString());
    }

    @Test
    public void constructor_endBeforeStart_exceptionThrown() {
        DavidGogginsException e = assertThrows(DavidGogginsException.class, () ->
                new Event("camp", "2026-10-03", "2026-10-01"));
        assertTrue(e.getMessage().contains("cannot end before it starts"), e.getMessage());
    }

    @Test
    public void constructor_malformedStart_exceptionThrown() {
        DavidGogginsException e = assertThrows(DavidGogginsException.class, () ->
                new Event("camp", "next week", "2026-10-01"));
        assertTrue(e.getMessage().contains("yyyy-mm-dd"), e.getMessage());
    }

    @Test
    public void constructor_impossibleEnd_exceptionThrown() {
        DavidGogginsException e = assertThrows(DavidGogginsException.class, () ->
                new Event("camp", "2026-04-29", "2026-04-31"));
        assertTrue(e.getMessage().contains("not a real date"), e.getMessage());
    }

    @Test
    public void isDuplicateOf_sameDescriptionAndDates_true() throws DavidGogginsException {
        assertTrue(new Event("camp", "2026-10-01", "2026-10-03")
                .isDuplicateOf(new Event("Camp", "2026-10-01", "2026-10-03")));
    }

    @Test
    public void isDuplicateOf_differentStart_false() throws DavidGogginsException {
        assertFalse(new Event("camp", "2026-10-01", "2026-10-03")
                .isDuplicateOf(new Event("camp", "2026-10-02", "2026-10-03")));
    }

    @Test
    public void isDuplicateOf_differentEnd_false() throws DavidGogginsException {
        assertFalse(new Event("camp", "2026-10-01", "2026-10-03")
                .isDuplicateOf(new Event("camp", "2026-10-01", "2026-10-04")));
    }
}
