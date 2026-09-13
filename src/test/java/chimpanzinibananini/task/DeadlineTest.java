package chimpanzinibananini.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

import org.junit.jupiter.api.Test;

/** Tests deadline parsing, display, and storage behavior. */
class DeadlineTest {

    @Test
    void getTypeIcon_deadline_returnsDeadlineIcon() {
        Deadline deadline = new Deadline("return book", LocalDateTime.of(2019, 12, 2, 18, 0));

        assertEquals("D", deadline.getTypeIcon());
    }

    @Test
    void parseDateTime_dayFirstDateAndTime_returnsParsedDateTime() {
        assertEquals(
                LocalDateTime.of(2019, 12, 2, 18, 0),
                Deadline.parseDateTime("2/12/2019 1800"));
    }

    @Test
    void parseDateTime_isoDateAndCompactTime_returnsParsedDateTime() {
        assertEquals(
                LocalDateTime.of(2019, 10, 15, 9, 30),
                Deadline.parseDateTime("2019-10-15 0930"));
    }

    @Test
    void parseDateTime_isoDateTime_returnsParsedDateTime() {
        assertEquals(
                LocalDateTime.of(2024, 2, 29, 23, 59),
                Deadline.parseDateTime("2024-02-29T23:59:00"));
    }

    @Test
    void parseDateTime_isoDateOnly_returnsStartOfDay() {
        assertEquals(
                LocalDateTime.of(2019, 10, 15, 0, 0),
                Deadline.parseDateTime("2019-10-15"));
    }

    @Test
    void parseDateTime_invalidCalendarDate_throwsDateTimeParseException() {
        assertThrows(
                DateTimeParseException.class,
                () -> Deadline.parseDateTime("2023-02-29"));
    }

    @Test
    void parseDateTime_invalidTime_throwsDateTimeParseException() {
        assertThrows(
                DateTimeParseException.class,
                () -> Deadline.parseDateTime("2/12/2019 2400"));
    }

    @Test
    void parseDateTime_unsupportedFormat_throwsDateTimeParseException() {
        assertThrows(
                DateTimeParseException.class,
                () -> Deadline.parseDateTime("December 2, 2019 at 6pm"));
    }

    @Test
    void parseDateTime_emptyValue_throwsDateTimeParseException() {
        assertThrows(DateTimeParseException.class, () -> Deadline.parseDateTime(""));
    }

    @Test
    void toDataString_newDeadline_returnsUnmarkedStorageFormat() {
        Deadline deadline = new Deadline("return book", LocalDateTime.of(2019, 12, 2, 18, 0));

        assertEquals("D | 0 | return book | 2019-12-02T18:00:00", deadline.toDataString());
    }

    @Test
    void toDataString_markedDeadline_returnsMarkedStorageFormat() {
        Deadline deadline = new Deadline("return book", LocalDateTime.of(2019, 12, 2, 18, 0));
        deadline.markAsDone();

        assertEquals("D | 1 | return book | 2019-12-02T18:00:00", deadline.toDataString());
    }

    @Test
    void toDataString_descriptionWithSpecialCharacters_escapesDescription() {
        Deadline deadline = new Deadline(
                "read \\ write | review\nnotes",
                LocalDateTime.of(2019, 12, 2, 18, 0));

        assertEquals(
                "D | 0 | read \\\\ write \\| review\\nnotes | 2019-12-02T18:00:00",
                deadline.toDataString());
    }

    @Test
    void toString_dateOnlyDeadline_displaysDateWithoutTime() {
        Deadline deadline = new Deadline("return book", "2019-12-02");

        assertEquals("[D][ ] return book (by: Dec 2 2019)", deadline.toString());
    }

    @Test
    void toString_deadlineWithTime_displaysReadableDateAndTime() {
        Deadline deadline = new Deadline("return book", "2/12/2019 1800");

        assertEquals("[D][ ] return book (by: Dec 2 2019, 6:00PM)", deadline.toString());
    }

    @Test
    void toString_markedDeadline_displaysCompletedStatus() {
        Deadline deadline = new Deadline("return book", "2019-12-02");
        deadline.markAsDone();

        assertEquals("[D][X] return book (by: Dec 2 2019)", deadline.toString());
    }
}
