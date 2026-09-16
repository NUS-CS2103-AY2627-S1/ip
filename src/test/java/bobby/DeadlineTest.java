package bobby;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

import org.junit.jupiter.api.Test;

/** Tests deadline parsing, identity, and date representations. */
public class DeadlineTest {

    /** Verifies date-only input is represented as midnight without displaying a time. */
    @Test
    public void fromInput_dateOnly_midnightValueAndDateOnlyTextReturned() {
        Deadline deadline = Deadline.fromInput("return book", "2026-09-20");

        assertEquals(LocalDateTime.of(2026, 9, 20, 0, 0), deadline.getDueDateTime());
        assertEquals("[D][ ] return book (by: Sep 20 2026)", deadline.toString());
        assertEquals("D | 0 | return book | 2026-09-20 | NONE", deadline.toStorageString());
    }

    /** Verifies date-time input is retained in display and storage representations. */
    @Test
    public void fromInput_dateAndTime_typedValueAndTimeTextReturned() {
        Deadline deadline = Deadline.fromInput("call client", "2026-09-20 1805");
        deadline.setPriority(Priority.LOW);
        deadline.markAsDone();

        assertEquals(LocalDateTime.of(2026, 9, 20, 18, 5), deadline.getDueDateTime());
        assertEquals("[D][X][P: LOW] call client (by: Sep 20 2026 6:05 PM)", deadline.toString());
        assertEquals("D | 1 | call client | 2026-09-20 1805 | LOW", deadline.toStorageString());
    }

    /** Verifies both deadline constructors preserve their intended date precision. */
    @Test
    public void constructors_dateAndDateTime_expectedStoragePrecisionReturned() {
        Deadline dateOnly = new Deadline("date only", LocalDate.of(2026, 1, 2));
        Deadline dateAndTime = new Deadline("date and time", LocalDateTime.of(2026, 1, 2, 0, 0));

        assertEquals("D | 0 | date only | 2026-01-02 | NONE", dateOnly.toStorageString());
        assertEquals("D | 0 | date and time | 2026-01-02 0000 | NONE", dateAndTime.toStorageString());
    }

    /** Verifies strict parsing rejects invalid dates, times, and unsupported formats. */
    @Test
    public void fromInput_invalidValues_dateTimeParseExceptionThrown() {
        assertThrows(DateTimeParseException.class, () ->
                Deadline.fromInput("invalid date", "2026-02-29"));
        assertThrows(DateTimeParseException.class, () ->
                Deadline.fromInput("invalid time", "2026-09-20 2400"));
        assertThrows(DateTimeParseException.class, () ->
                Deadline.fromInput("unsupported format", "20-09-2026"));
    }

    /** Verifies deadline identity includes type, normalized description, date, and time precision. */
    @Test
    public void hasSameDetails_variedDeadlines_expectedResultReturned() {
        Deadline deadline = new Deadline("Return Book", LocalDate.of(2026, 9, 20));

        assertTrue(deadline.hasSameDetails(
                new Deadline(" return   book ", LocalDate.of(2026, 9, 20))));
        assertFalse(deadline.hasSameDetails(
                new Deadline("return book", LocalDate.of(2026, 9, 21))));
        assertFalse(deadline.hasSameDetails(
                new Deadline("return book", LocalDateTime.of(2026, 9, 20, 0, 0))));
        assertFalse(deadline.hasSameDetails(new ToDo("return book")));
        assertFalse(deadline.hasSameDetails(null));
    }
}
