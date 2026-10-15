package lumine.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import lumine.LumineException;

/**
 * Tests {@link Event}.
 *
 * <p>Events accept paired free-text values or strictly increasing date-times.
 * The integration between {@link Event} and {@link TaskDateTime} is verified
 * through {@code toString}, {@code toStorageString}, {@code getToDate}, and {@code isDueOn}.
 */
class EventTest {

    // -------------------------------------------------------------------------
    // toString
    // -------------------------------------------------------------------------

    @Test
    void toString_plainTextTimes_displayedAsIs() {
        Event e = new Event("test", "Mon 2pm", "4pm");
        assertEquals("[E][ ] test (from: Mon 2pm to: 4pm)", e.toString());
    }

    @Test
    void constructor_dateOnlyTimes_throwsFormatError() {
        LumineException exception = assertThrows(
                LumineException.class, () -> new Event("test", "2025 12 31", "2026 01 01"));
        assertEquals("Sorry, you need to enter date in format yyyy MM dd HHmm for both from and to date.",
                exception.getMessage());
    }

    @Test
    void toString_dateTimeTimes_formatsToMmmDdYyyyHhMm() {
        Event e = new Event("test", "2025 12 31 1400", "2025 12 31 1600");
        assertEquals("[E][ ] test (from: Dec 31 2025 14:00 to: Dec 31 2025 16:00)", e.toString());
    }

    @Test
    void toString_doneEvent_showsXStatusIcon() {
        Event e = new Event("test", "Mon 2pm", "4pm");
        e.markDone();
        assertEquals("[E][X] test (from: Mon 2pm to: 4pm)", e.toString());
    }

    @Test
    void toString_undoneEvent_hideXStatusIcon() {
        Event e = new Event("test", "Mon 2pm", "4pm");
        e.markDone();
        assertEquals("[E][X] test (from: Mon 2pm to: 4pm)", e.toString());
        e.markUndone();
        assertEquals("[E][ ] test (from: Mon 2pm to: 4pm)", e.toString());
    }

    @Test
    void constructor_freeTextStartAndDatedEnd_throwsFormatError() {
        LumineException exception = assertThrows(
                LumineException.class, () -> new Event("test", "Mon 2pm", "2025 12 31 1600"));
        assertEquals("Sorry, you need to enter date in format yyyy MM dd HHmm for both from and to date.",
                exception.getMessage());
    }

    // -------------------------------------------------------------------------
    // toStorageString
    // -------------------------------------------------------------------------

    @Test
    void toStorageString_plainTextTimes_storedAsIs() {
        Event e = new Event("test", "Mon 2pm", "4pm");
        assertEquals("E | 0 | test | Mon 2pm | 4pm", e.toStorageString());
    }

    @Test
    void toStorageString_crossMidnightDateTimes_storedInExistingFormat() {
        Event e = new Event("test", "2025 12 31 2300", "2026 01 01 0100");
        assertEquals("E | 0 | test | 2025 12 31 2300 | 2026 01 01 0100", e.toStorageString());
    }

    @Test
    void toStorageString_dateTimeTimes_storedInYyyyMmDdHhmmFormat() {
        Event e = new Event("test", "2025 12 31 1400", "2025 12 31 1600");
        assertEquals("E | 0 | test | 2025 12 31 1400 | 2025 12 31 1600", e.toStorageString());
    }

    @Test
    void toStorageString_doneEvent_containsOneFlag() {
        Event e = new Event("test", "Mon 2pm", "4pm");
        e.markDone();
        assertEquals("E | 1 | test | Mon 2pm | 4pm", e.toStorageString());
    }

    @Test
    void toStorageString_undoneEvent_containsNoFlag() {
        Event e = new Event("test", "Mon 2pm", "4pm");
        e.markDone();
        assertEquals("E | 1 | test | Mon 2pm | 4pm", e.toStorageString());
        e.markUndone();
        assertEquals("E | 0 | test | Mon 2pm | 4pm", e.toStorageString());
    }

    // -------------------------------------------------------------------------
    // getToDate
    // -------------------------------------------------------------------------

    @Test
    void getToDate_plainTextTo_returnsNull() {
        // When /to is free text there is no calendar date to return.
        Event e = new Event("test", "Mon 2pm", "4pm");
        assertNull(e.getToDate());
    }

    @Test
    void constructor_datedStartAndFreeTextEnd_throwsFormatError() {
        LumineException exception = assertThrows(
                LumineException.class, () -> new Event("test", "2025 12 31 1400", "4pm"));
        assertEquals("Sorry, you need to enter date in format yyyy MM dd HHmm for both from and to date.",
                exception.getMessage());
    }

    @Test
    void getToDate_dateTimeTo_returnsDatePortionOnly() {
        Event e = new Event("test", "2025 12 31 1400", "2025 12 31 1600");
        assertEquals(LocalDate.of(2025, 12, 31), e.getToDate());
    }

    @Test
    void isDueOn_matchingEndDate_returnsTrue() {
        Event event = new Event("test", "2026 01 01 1400", "2026 01 02 1600");

        assertTrue(event.isDueOn(LocalDate.of(2026, 1, 2)));
    }

    @Test
    void isDueOn_startDateOrUnstructuredEndDate_returnsFalse() {
        Event datedEvent = new Event("dated", "2026 01 01 1400", "2026 01 02 1600");
        Event freeTextEvent = new Event("free text", "Monday", "Friday");

        assertFalse(datedEvent.isDueOn(LocalDate.of(2026, 1, 1)));
        assertFalse(freeTextEvent.isDueOn(LocalDate.of(2026, 1, 1)));
    }

    // -------------------------------------------------------------------------
    // constructor validation
    // -------------------------------------------------------------------------

    @Test
    void constructor_emptyFrom_throwsLumineException() {
        assertThrows(LumineException.class, () -> new Event("test", "  ", "4pm"));
    }

    @Test
    void constructor_emptyTo_throwsLumineException() {
        assertThrows(LumineException.class, () -> new Event("test", "Mon 2pm", "  "));
    }

    @Test
    void constructor_impossibleFromDate_throwsDateNotFound() {
        assertThrows(LumineException.class, () -> new Event("test", "2026 02 30 1400", "2026 03 01 1600"));
    }

    @Test
    void constructor_impossibleToDate_throwsDateNotFound() {
        assertThrows(LumineException.class, () -> new Event("test", "2026 02 28 1400", "2026 02 30 1600"));
    }

    @Test
    void constructor_endBeforeStart_throwsRangeError() {
        LumineException exception = assertThrows(
                LumineException.class, () -> new Event("test", "2026 01 02 1400", "2026 01 01 1600"));
        assertEquals("Hmmmm, the event end date and start date isn't valid, "
                        + "try again with a valid range instead",
                exception.getMessage());
    }

    @Test
    void constructor_equalTimes_throwsRangeError() {
        LumineException exception = assertThrows(
                LumineException.class, () -> new Event("test", "2026 01 01 1400", "2026 01 01 1400"));
        assertEquals("Hmmmm, the event end date and start date isn't valid, "
                        + "try again with a valid range instead",
                exception.getMessage());
    }

    @Test
    void constructor_endEarlierOnSameDay_throwsRangeError() {
        assertThrows(LumineException.class, () -> new Event("test", "2026 01 01 1600", "2026 01 01 1400"));
    }

    @Test
    void constructor_missingStartTime_throwsFormatError() {
        LumineException exception = assertThrows(
                LumineException.class, () -> new Event("test", "2026 01 01", "2026 01 02 1600"));
        assertEquals("Sorry, you need to enter date in format yyyy MM dd HHmm for both from and to date.",
                exception.getMessage());
    }

    @Test
    void constructor_missingEndTime_throwsFormatError() {
        LumineException exception = assertThrows(
                LumineException.class, () -> new Event("test", "2026 01 01 1400", "2026 01 02"));
        assertEquals("Sorry, you need to enter date in format yyyy MM dd HHmm for both from and to date.",
                exception.getMessage());
    }

    @Test
    void constructor_dateWithMalformedTime_throwsFormatError() {
        LumineException exception = assertThrows(
                LumineException.class, () -> new Event("test", "2026 01 01 14:00", "2026 01 01 1600"));
        assertEquals("Sorry, you need to enter date in format yyyy MM dd HHmm for both from and to date.",
                exception.getMessage());
    }

    @Test
    void toStorageString_dateTimesWithExtraWhitespace_returnsCanonicalValues() {
        Event event = new Event("test", " 2026  01 01\t1400 ", "2026 01 01  1600");

        assertEquals("E | 0 | test | 2026 01 01 1400 | 2026 01 01 1600", event.toStorageString());
    }

    @Test
    void constructor_nullDescription_throwsLumineException() {
        assertThrows(LumineException.class, () -> new Event(null, "Mon 2pm", "4pm"));
    }
}
