package epi.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;

import org.junit.jupiter.api.Test;

/** Protects strict calendar validation and round-trip date formatting. */
class DeadlineTest {
    @Test
    void constructor_impossibleDatesAndTimes_rejectsInsteadOfNormalizing() {
        for (String date : List.of("2019-02-29 1200", "2020-02-30 1200", "2020-04-31 1200",
                "1900-02-29 1200", "2020-13-01 1200", "2020-00-01 1200", "2020-01-00 1200",
                "2020-01-01 2400", "2020-01-01 1260", "2020-01-01 12:00", "0000-01-01 1200",
                "-0001-01-01 1200", "Sunday", "")) {
            assertThrows(DateTimeParseException.class, () -> new Deadline("book", date), date);
        }
    }

    @Test
    void constructor_leapDayAndTimeBoundaries_preservesRealDate() {
        Deadline leapDay = new Deadline("book", "2000-02-29 0000");
        assertEquals(LocalDateTime.of(2000, 2, 29, 0, 0), leapDay.getSortDate());
        assertEquals("D | 0 | book | 2000-02-29 0000", leapDay.toFileFormat());
        assertEquals(LocalDateTime.of(2020, 12, 31, 23, 59),
                new Deadline("book", "2020-12-31 2359").getSortDate());
    }
}
