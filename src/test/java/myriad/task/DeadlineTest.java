package myriad.task;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import myriad.MyriadException;

/**
 * Tests for when a {@link Deadline} counts as taking place: its overlaps
 * method, and the occursDuring method Task builds on it. A deadline with no
 * time stands for its whole day, and one with a time for that exact minute,
 * so the tests pair each kind with queries just inside and just outside it.
 */
public class DeadlineTest {

    /** Builds a TaskDateTime, unwrapping the checked exception for brevity. */
    private static TaskDateTime at(String raw) {
        try {
            return TaskDateTime.parse(raw);
        } catch (MyriadException e) {
            throw new AssertionError("test fixture should parse: " + raw, e);
        }
    }

    /** Builds a not-done Deadline due at the given date/time text. */
    private static Deadline dueAt(String raw) {
        return new Deadline("homework", at(raw));
    }

    // ---------------------------------------------------------------
    // occursDuring -- the "show" command's check
    // ---------------------------------------------------------------

    @Test
    public void occursDuring_dateOnlyDeadlineQueriedLateThatDay_true() {
        assertTrue(dueAt("2019-12-02").occursDuring(at("2019-12-02 2359")));
    }

    @Test
    public void occursDuring_timedDeadlineQueriedOnItsDate_true() {
        // A date-only query covers the whole day, so any time on it matches.
        assertTrue(dueAt("2019-12-02 1800").occursDuring(at("2019-12-02")));
    }

    @Test
    public void occursDuring_timedDeadlineQueriedAtSameTime_true() {
        assertTrue(dueAt("2019-12-02 1800").occursDuring(at("2019-12-02 1800")));
    }

    @Test
    public void occursDuring_timedDeadlineQueriedAtOtherTime_false() {
        assertFalse(dueAt("2019-12-02 1800").occursDuring(at("2019-12-02 1759")));
    }

    @Test
    public void occursDuring_queriedOnAdjacentDays_false() {
        Deadline deadline = dueAt("2019-12-02");
        assertFalse(deadline.occursDuring(at("2019-12-01")));
        assertFalse(deadline.occursDuring(at("2019-12-03")));
    }

    // ---------------------------------------------------------------
    // overlaps -- the "stats" command's due-soon check
    // ---------------------------------------------------------------

    @Test
    public void overlaps_periodEndingExactlyAtDeadline_true() {
        // Both ends of the period are inclusive.
        assertTrue(dueAt("2019-12-02 1800").overlaps(
                LocalDateTime.parse("2019-12-01T00:00"), LocalDateTime.parse("2019-12-02T18:00")));
    }

    @Test
    public void overlaps_periodEndingJustBeforeDeadline_false() {
        assertFalse(dueAt("2019-12-02 1800").overlaps(
                LocalDateTime.parse("2019-12-01T00:00"), LocalDateTime.parse("2019-12-02T17:59")));
    }

    @Test
    public void overlaps_periodStartingLateOnDateOnlyDeadline_true() {
        // A date-only deadline lasts until the end of its day.
        assertTrue(dueAt("2019-12-02").overlaps(
                LocalDateTime.parse("2019-12-02T23:59"), LocalDateTime.parse("2019-12-05T00:00")));
    }

    // ---------------------------------------------------------------
    // hasSameDetails
    // ---------------------------------------------------------------

    @Test
    public void hasSameDetails_sameDescriptionAndDate_true() {
        assertTrue(new Deadline("homework", at("2019-12-02 1800"))
                .hasSameDetails(new Deadline("homework", at("2/12/2019 18:00"))));
    }

    @Test
    public void hasSameDetails_differentDate_false() {
        // The same piece of homework can be due again another week.
        assertFalse(new Deadline("homework", at("2019-12-02"))
                .hasSameDetails(new Deadline("homework", at("2019-12-09"))));
    }
}
