package milo.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import milo.exception.MiloException;

/**
 * Deadline's constructor is the most complex piece of logic in the codebase:
 * it tries four date/time formats, resolves them strictly (so an impossible
 * date like 31/2/2019 is rejected rather than silently clamped), and tracks
 * whether a time was given so a date-only deadline can be told apart from
 * one due at exactly midnight. These tests exercise every accepted format,
 * every rejection path, and both display forms that fall out of hasTime.
 */
class DeadlineTest {
    @Test
    void constructor_isoDateOnly_parsesAndDisplaysWithoutTime() throws MiloException {
        Deadline deadline = new Deadline("return book", "2019-10-15");
        assertEquals("[D][ ] return book (by: Oct 15 2019)", deadline.toString());
    }

    @Test
    void constructor_isoDateWithTime_parsesAndDisplaysLowercaseAmPm() throws MiloException {
        Deadline deadline = new Deadline("return book", "2019-10-15 1800");
        assertEquals("[D][ ] return book (by: Oct 15 2019, 6:00pm)", deadline.toString());
    }

    @Test
    void constructor_slashDateOnly_parsesCorrectly() throws MiloException {
        Deadline deadline = new Deadline("return book", "2/12/2019");
        assertEquals("[D][ ] return book (by: Dec 2 2019)", deadline.toString());
    }

    @Test
    void constructor_slashDateWithTime_parsesCorrectly() throws MiloException {
        Deadline deadline = new Deadline("return book", "2/12/2019 1800");
        assertEquals("[D][ ] return book (by: Dec 2 2019, 6:00pm)", deadline.toString());
    }

    @Test
    void constructor_morningTime_showsLowercaseAm() throws MiloException {
        Deadline deadline = new Deadline("return book", "2019-01-05 0930");
        assertEquals("[D][ ] return book (by: Jan 5 2019, 9:30am)", deadline.toString());
    }

    @Test
    void constructor_whitespaceAroundDate_isTrimmedBeforeParsing() throws MiloException {
        Deadline deadline = new Deadline("return book", "  2019-10-15  ");
        assertEquals("[D][ ] return book (by: Oct 15 2019)", deadline.toString());
    }

    @Test
    void constructor_freeText_throwsMiloException() {
        assertThrows(MiloException.class, () -> new Deadline("return book", "no idea :-p"));
    }

    @Test
    void constructor_dayOutOfRangeForMonth_throwsMiloException() {
        // Strict resolution must reject this rather than clamping to Feb 28,
        // which is what java.time's default lenient resolver would do.
        assertThrows(MiloException.class, () -> new Deadline("return book", "31/2/2019"));
    }

    @Test
    void constructor_isoDayOutOfRange_throwsMiloException() {
        assertThrows(MiloException.class, () -> new Deadline("return book", "2019-02-30"));
    }

    @Test
    void constructor_monthOutOfRange_throwsMiloException() {
        assertThrows(MiloException.class, () -> new Deadline("return book", "2019-13-01"));
    }

    @Test
    void constructor_hourOutOfRange_throwsMiloException() {
        assertThrows(MiloException.class, () -> new Deadline("return book", "2019-10-15 2599"));
    }

    @Test
    void constructor_leapDayOnLeapYear_isAccepted() throws MiloException {
        Deadline deadline = new Deadline("return book", "29/2/2020");
        assertEquals("[D][ ] return book (by: Feb 29 2020)", deadline.toString());
    }

    @Test
    void constructor_leapDayOnNonLeapYear_throwsMiloException() {
        assertThrows(MiloException.class, () -> new Deadline("return book", "29/2/2019"));
    }

    @Test
    void toString_midnightGivenExplicitly_showsTimeUnlikeDateOnly() throws MiloException {
        // hasTime distinguishes "no time given" (midnight, hidden) from
        // "due at exactly 00:00" (midnight, shown) - both parse to the same
        // LocalDateTime, so only the flag tells them apart.
        Deadline dateOnly = new Deadline("a", "2019-10-15");
        Deadline explicitMidnight = new Deadline("b", "2019-10-15 0000");
        assertEquals("[D][ ] a (by: Oct 15 2019)", dateOnly.toString());
        assertEquals("[D][ ] b (by: Oct 15 2019, 12:00am)", explicitMidnight.toString());
    }

    @Test
    void toFileFormat_dateOnly_writesIsoDateWithoutTime() throws MiloException {
        Deadline deadline = new Deadline("return book", "2/12/2019");
        assertEquals("D | 0 | return book | 2019-12-02", deadline.toFileFormat());
    }

    @Test
    void toFileFormat_withTime_writesIsoDateAndTime() throws MiloException {
        Deadline deadline = new Deadline("return book", "2/12/2019 1800");
        assertEquals("D | 0 | return book | 2019-12-02 1800", deadline.toFileFormat());
    }

    @Test
    void toFileFormat_doneDeadline_flagIsOne() throws MiloException {
        Deadline deadline = new Deadline("return book", "2019-10-15");
        deadline.markAsDone();
        assertEquals("D | 1 | return book | 2019-10-15", deadline.toFileFormat());
    }
}
