package aster.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

/**
 * Tests {@link TaskDates#parseOrNull(String)} and {@link TaskDates#format(LocalDate)}.
 *
 * <p>{@code parseOrNull} is the single point at which text becomes a date anywhere in
 * Aster, since both the parser and the storage layer ask it, so the cases below pin down
 * every part of its promise: it tolerates a missing or padded value, it answers with
 * {@code null} rather than throwing, and it reads dates strictly, refusing a date that
 * never existed instead of quietly moving it to a nearby one.
 *
 * <p>This class sits in the same package as the class it tests, which is what lets it
 * reach the package-private {@code format}. Nothing in the production code is made more
 * visible for these tests.
 *
 * <p>A note on what the {@code format} cases prove. They check the display strings
 * themselves: the English month abbreviation, the order of the fields, and the padded
 * day. They do <em>not</em> prove that the display is independent of the computer's
 * configured locale, because a test running under an English locale would still pass if
 * that independence were removed. That independence rests on the explicit
 * {@code Locale.ENGLISH} in {@code TaskDates}, which is established by reading the
 * source, not by these tests.
 */
class TaskDatesTest {

    // ---------- parseOrNull: what it accepts ----------

    @Test
    void parseOrNull_isoDate_returnsThatDate() {
        assertEquals(LocalDate.of(2019, 12, 2), TaskDates.parseOrNull("2019-12-02"));
    }

    @Test
    void parseOrNull_surroundingWhitespace_returnsThatDate() {
        assertEquals(LocalDate.of(2019, 12, 2), TaskDates.parseOrNull("  2019-12-02\t"));
    }

    @Test
    void parseOrNull_leapDayInLeapYear_returnsThatDate() {
        // Paired with the non-leap case below: together they require the reading to be
        // strict about the calendar, rather than simply suspicious of 29 February.
        assertEquals(LocalDate.of(2020, 2, 29), TaskDates.parseOrNull("2020-02-29"));
    }

    @Test
    void parseOrNull_ofStoredForm_returnsOriginalDate() {
        LocalDate date = LocalDate.of(2019, 6, 6);

        assertEquals(date, TaskDates.parseOrNull(TaskDates.toStorage(date)));
    }

    // ---------- parseOrNull: what it refuses ----------

    @Test
    void parseOrNull_null_returnsNull() {
        // The method's contract permits a missing value and returns null rather than
        // throwing an exception.
        assertNull(TaskDates.parseOrNull(null));
    }

    @Test
    void parseOrNull_blank_returnsNull() {
        assertNull(TaskDates.parseOrNull("   "));
    }

    @Test
    void parseOrNull_leapDayInNonLeapYear_returnsNull() {
        assertNull(TaskDates.parseOrNull("2019-02-29"));
    }

    @Test
    void parseOrNull_monthOutOfRange_returnsNull() {
        assertNull(TaskDates.parseOrNull("2019-13-01"));
    }

    @Test
    void parseOrNull_slashSeparatedDate_returnsNull() {
        assertNull(TaskDates.parseOrNull("2/12/2019"));
    }

    @Test
    void parseOrNull_singleDigitDay_returnsNull() {
        // The near miss: a loosened pattern would let this through and quietly widen the
        // form a date may be typed in.
        assertNull(TaskDates.parseOrNull("2019-12-2"));
    }

    @Test
    void parseOrNull_trailingText_returnsNull() {
        // Reading only the front of this would silently drop the time the user typed.
        assertNull(TaskDates.parseOrNull("2019-12-02 1800"));
    }

    // ---------- format: how a date is shown ----------

    @Test
    void format_date_usesEnglishMonthAbbreviation() {
        assertEquals("Dec 25 2019", TaskDates.format(LocalDate.of(2019, 12, 25)));
    }

    @Test
    void format_singleDigitDay_padsDayToTwoDigits() {
        assertEquals("Jan 05 2019", TaskDates.format(LocalDate.of(2019, 1, 5)));
    }

    @Test
    void format_andToStorage_produceDifferentForms() {
        LocalDate date = LocalDate.of(2019, 12, 2);

        assertNotEquals(TaskDates.format(date), TaskDates.toStorage(date));
    }
}
