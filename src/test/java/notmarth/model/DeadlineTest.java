package notmarth.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

/** Tests deadline date parsing, date matching, storage values, and display text. */
class DeadlineTest {
    /** Verifies that date-only deadlines preserve date-only display and storage formats. */
    @Test
    void deadlineCreation_dateOnlyInput_preservesDateOnlyFormatting() {
        Deadline deadline = new Deadline("submit report", "2019-10-15");

        assertEquals(LocalDateTime.of(2019, 10, 15, 0, 0), deadline.getBy());
        assertEquals("2019-10-15", deadline.getByForStorage());
        assertEquals("[D][ ] submit report (by: Oct 15 2019)", deadline.toString());
    }

    /** Verifies that timed deadlines match only their calendar date. */
    @Test
    void isDueOn_timedDeadline_matchesOnlyCalendarDate() {
        Deadline deadline = new Deadline("return book", "2/12/2019 1800");

        assertTrue(deadline.isDueOn(LocalDate.of(2019, 12, 2)));
        assertFalse(deadline.isDueOn(LocalDate.of(2019, 12, 3)));
        assertEquals("2019-12-02T18:00", deadline.getByForStorage());
        assertEquals("[D][ ] return book (by: Dec 02 2019 6:00 PM)", deadline.toString());
    }

    /** Verifies that a deadline can be created from a typed date-time value. */
    @Test
    void deadlineCreation_localDateTimeInput_acceptsTypedValue() {
        Deadline deadline = new Deadline("meeting", LocalDateTime.of(2019, 10, 15, 14, 30));

        assertEquals(LocalDateTime.of(2019, 10, 15, 14, 30), deadline.getBy());
        assertEquals("2019-10-15T14:30", deadline.getByForStorage());
    }

    /** Verifies that a typed deadline cannot contain a missing date-time value. */
    @Test
    void deadlineCreation_nullTypedValue_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
                () -> new Deadline("missing", (LocalDateTime) null));
        assertThrows(IllegalArgumentException.class,
                () -> new Deadline("missing", (LocalDate) null));
    }
}
