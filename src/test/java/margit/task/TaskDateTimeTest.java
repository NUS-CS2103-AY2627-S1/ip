package margit.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

/** Tests parsing and save-file conversion for {@link TaskDateTime}. */
class TaskDateTimeTest {
    @Test
    void parse_supportedDateTime_savesInCanonicalDateTimeFormat() {
        TaskDateTime dateTime = TaskDateTime.parse("2/12/2019 1800");

        assertEquals("DT:2019-12-02 1800", dateTime.toSaveFormat());
    }

    @Test
    void parse_supportedDate_savesInCanonicalDateFormat() {
        TaskDateTime date = TaskDateTime.parse("2/12/2019");

        assertEquals("D:2019-12-02", date.toSaveFormat());
    }

    @Test
    void toSortDateTime_dateOnlyValue_returnsEndOfDay() {
        TaskDateTime date = TaskDateTime.parse("2/12/2019");

        assertEquals(LocalDateTime.of(2019, 12, 2, 23, 59), date.toSortDateTime());
    }

    @Test
    void parse_unrecognisedInput_preservesRawValueForSaving() {
        TaskDateTime date = TaskDateTime.parse("someday soon");

        assertEquals("RAW:someday soon", date.toSaveFormat());
        assertNull(date.toLocalDate());
    }

    @Test
    void parseDateOnly_dateAndDateTime_returnsDateComponent() {
        assertEquals(LocalDate.of(2019, 12, 2), TaskDateTime.parseDateOnly("2/12/2019"));
        assertEquals(LocalDate.of(2019, 12, 2), TaskDateTime.parseDateOnly("2/12/2019 1800"));
    }

    @Test
    void parseDateOnly_invalidInput_returnsNull() {
        assertNull(TaskDateTime.parseDateOnly("not a date"));
    }

    @Test
    void fromSaveFormat_allRepresentations_reconstructsOriginalValue() {
        assertEquals("DT:2019-12-02 1800", TaskDateTime.fromSaveFormat("DT:2019-12-02 1800").toSaveFormat());
        assertEquals("D:2019-12-02", TaskDateTime.fromSaveFormat("D:2019-12-02").toSaveFormat());
        assertEquals("RAW:someday", TaskDateTime.fromSaveFormat("RAW:someday").toSaveFormat());
    }
}
