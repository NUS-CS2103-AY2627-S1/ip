package myriad.task;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import myriad.MyriadException;

/**
 * Tests for when an {@link Event} counts as taking place: its overlaps
 * method, and the occursDuring method Task builds on it. An event spans
 * from the earliest instant of its start to the latest instant of its end,
 * so the tests probe each edge of that span from both sides.
 */
public class EventTest {

    /** Builds a TaskDateTime, unwrapping the checked exception for brevity. */
    private static TaskDateTime at(String raw) {
        try {
            return TaskDateTime.parse(raw);
        } catch (MyriadException e) {
            throw new AssertionError("test fixture should parse: " + raw, e);
        }
    }

    /** An event from 2 Dec 2019 14:00 to 4 Dec 2019 (the whole of that day). */
    private static Event conference() {
        return new Event("conference", at("2019-12-02 1400"), at("2019-12-04"));
    }

    // ---------------------------------------------------------------
    // checkTimesInOrder -- rejecting impossible events
    // ---------------------------------------------------------------

    @Test
    public void checkTimesInOrder_endAfterStart_noException() {
        assertDoesNotThrow(() -> Event.checkTimesInOrder(at("2019-12-02 1400"), at("2019-12-02 1401")));
    }

    @Test
    public void checkTimesInOrder_endBeforeStart_exceptionThrown() {
        MyriadException e = assertThrows(MyriadException.class, () ->
                Event.checkTimesInOrder(at("2019-12-05"), at("2019-12-01")));
        // Both times are named, so the user can see which one to fix.
        assertTrue(e.getMessage().contains("Dec 05 2019"));
        assertTrue(e.getMessage().contains("Dec 01 2019"));
    }

    @Test
    public void checkTimesInOrder_sameInstant_exceptionThrown() {
        assertThrows(MyriadException.class, () ->
                Event.checkTimesInOrder(at("2019-12-02 1400"), at("2019-12-02 1400")));
    }

    @Test
    public void checkTimesInOrder_sameDateWithoutTimes_noException() {
        // Each date-only value covers its whole day, so this is a one-day event.
        assertDoesNotThrow(() -> Event.checkTimesInOrder(at("2019-12-02"), at("2019-12-02")));
    }

    @Test
    public void checkTimesInOrder_timedStartAndDateOnlyEndOnSameDay_noException() {
        // The end covers the rest of that day, which comes after 14:00.
        assertDoesNotThrow(() -> Event.checkTimesInOrder(at("2019-12-02 1400"), at("2019-12-02")));
    }

    @Test
    public void checkTimesInOrder_dateOnlyEndOnDayBeforeTimedStart_exceptionThrown() {
        assertThrows(MyriadException.class, () ->
                Event.checkTimesInOrder(at("2019-12-02 0000"), at("2019-12-01")));
    }

    // ---------------------------------------------------------------
    // occursDuring -- the "show" command's check
    // ---------------------------------------------------------------

    @Test
    public void occursDuring_queryInsideSpan_true() {
        assertTrue(conference().occursDuring(at("2019-12-03")));
    }

    @Test
    public void occursDuring_queryAtExactStart_true() {
        assertTrue(conference().occursDuring(at("2019-12-02 1400")));
    }

    @Test
    public void occursDuring_timedQueryJustBeforeStart_false() {
        assertFalse(conference().occursDuring(at("2019-12-02 1359")));
    }

    @Test
    public void occursDuring_dateOnlyQueryOnStartDay_true() {
        // The query day covers 14:00, so it overlaps even though its own
        // start, midnight, is before the event's.
        assertTrue(conference().occursDuring(at("2019-12-02")));
    }

    @Test
    public void occursDuring_queryLateOnDateOnlyEndDay_true() {
        assertTrue(conference().occursDuring(at("2019-12-04 2359")));
    }

    @Test
    public void occursDuring_queryDayAfterEnd_false() {
        assertFalse(conference().occursDuring(at("2019-12-05")));
    }

    // ---------------------------------------------------------------
    // overlaps -- the "stats" command's due-soon check
    // ---------------------------------------------------------------

    @Test
    public void overlaps_periodInsideSpan_true() {
        assertTrue(conference().overlaps(
                LocalDateTime.parse("2019-12-03T09:00"), LocalDateTime.parse("2019-12-03T10:00")));
    }

    @Test
    public void overlaps_periodCoveringWholeSpan_true() {
        assertTrue(conference().overlaps(
                LocalDateTime.parse("2019-12-01T00:00"), LocalDateTime.parse("2019-12-31T00:00")));
    }

    @Test
    public void overlaps_periodEndingExactlyAtStart_true() {
        // Both ends of the period are inclusive.
        assertTrue(conference().overlaps(
                LocalDateTime.parse("2019-12-01T00:00"), LocalDateTime.parse("2019-12-02T14:00")));
    }

    @Test
    public void overlaps_periodEntirelyBeforeStart_false() {
        assertFalse(conference().overlaps(
                LocalDateTime.parse("2019-12-01T00:00"), LocalDateTime.parse("2019-12-02T13:59")));
    }

    @Test
    public void overlaps_periodStartingDayAfterEnd_false() {
        assertFalse(conference().overlaps(
                LocalDateTime.parse("2019-12-05T00:00"), LocalDateTime.parse("2019-12-06T00:00")));
    }

    // ---------------------------------------------------------------
    // hasSameDetails
    // ---------------------------------------------------------------

    @Test
    public void hasSameDetails_sameDescriptionStartAndEnd_true() {
        assertTrue(conference().hasSameDetails(conference()));
    }

    @Test
    public void hasSameDetails_differentStart_false() {
        assertFalse(conference().hasSameDetails(
                new Event("conference", at("2019-12-02 1500"), at("2019-12-04"))));
    }

    @Test
    public void hasSameDetails_differentEnd_false() {
        assertFalse(conference().hasSameDetails(
                new Event("conference", at("2019-12-02 1400"), at("2019-12-05"))));
    }
}
