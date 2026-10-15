package larry.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests date parsing performed by {@link TaskDateTime}.
 */
class TaskDateTimeTest {
    private static final String CURRENT_DATE_PROPERTY = "larry.currentDate";
    private static final String FIXED_CURRENT_DATE = "2026-08-27";

    private String originalCurrentDate;

    @BeforeEach
    void setUp() {
        originalCurrentDate = System.getProperty(CURRENT_DATE_PROPERTY);
        System.setProperty(CURRENT_DATE_PROPERTY, FIXED_CURRENT_DATE);
    }

    @AfterEach
    void tearDown() {
        if (originalCurrentDate == null) {
            System.clearProperty(CURRENT_DATE_PROPERTY);
        } else {
            System.setProperty(CURRENT_DATE_PROPERTY, originalCurrentDate);
        }
    }

    @Test
    void constructor_dateBeforeTime_valueParsed() {
        TaskDateTime taskDateTime = new TaskDateTime("6-8-2026 14:00");

        assertEquals(Optional.of(LocalDateTime.of(2026, 8, 6, 14, 0)), taskDateTime.getValue());
    }

    @Test
    void constructor_timeBeforeDate_valueParsed() {
        TaskDateTime taskDateTime = new TaskDateTime("1400 06/08/2026");

        assertEquals(Optional.of(LocalDateTime.of(2026, 8, 6, 14, 0)), taskDateTime.getValue());
    }

    @Test
    void constructor_timeOnly_fixedCurrentDateUsed() {
        TaskDateTime taskDateTime = new TaskDateTime("930");

        assertEquals(Optional.of(LocalDateTime.of(2026, 8, 27, 9, 30)), taskDateTime.getValue());
    }

    @Test
    void constructor_surroundingAndRepeatedWhitespace_valueParsed() {
        TaskDateTime taskDateTime = new TaskDateTime("  1400   6-8-2026  ");

        assertEquals(Optional.of(LocalDateTime.of(2026, 8, 6, 14, 0)), taskDateTime.getValue());
    }

    @Test
    void constructor_invalidInput_exceptionThrown() {
        assertThrows(DateTimeParseException.class, () ->
                new TaskDateTime(""));
        assertThrows(DateTimeParseException.class, () ->
                new TaskDateTime("6-8-2026"));
        assertThrows(DateTimeParseException.class, () ->
                new TaskDateTime("1400 1500"));
        assertThrows(DateTimeParseException.class, () ->
                new TaskDateTime("6-8-2026 1400 extra"));
        assertThrows(DateTimeParseException.class, () ->
                new TaskDateTime("24:00"));
        assertThrows(DateTimeParseException.class, () ->
                new TaskDateTime("1260"));
    }

    @Test
    void constructor_nullString_nullPointerExceptionThrown() {
        assertThrows(NullPointerException.class, () -> new TaskDateTime((String) null));
    }

    @Test
    void constructor_localDateTime_valuePreserved() {
        LocalDateTime expectedDateTime = LocalDateTime.of(2026, 8, 6, 14, 0, 30);
        TaskDateTime taskDateTime = new TaskDateTime(expectedDateTime);

        assertEquals(Optional.of(expectedDateTime), taskDateTime.getValue());
    }

    @Test
    void constructor_nullLocalDateTime_nullPointerExceptionThrown() {
        assertThrows(NullPointerException.class, () -> new TaskDateTime((LocalDateTime) null));
    }

    @Test
    void fromStorageString_isoDateTime_parsedValueReturned() {
        TaskDateTime taskDateTime = TaskDateTime.fromStorageString("2026-08-06T14:00:30");

        assertEquals(Optional.of(LocalDateTime.of(2026, 8, 6, 14, 0, 30)), taskDateTime.getValue());
    }

    @Test
    void fromStorageString_legacyText_emptyParsedValueReturned() {
        TaskDateTime taskDateTime = TaskDateTime.fromStorageString("Friday evening");

        assertEquals(Optional.empty(), taskDateTime.getValue());
    }

    @Test
    void fromStorageString_nullInput_nullPointerExceptionThrown() {
        assertThrows(NullPointerException.class, () -> TaskDateTime.fromStorageString(null));
    }

    @Test
    void parseDate_dateWithHyphens_parsedDateReturned() {
        assertEquals(LocalDate.of(2026, 6, 6), TaskDateTime.parseDate("6-6-2026"));
    }

    @Test
    void parseDate_dateWithSlashes_parsedDateReturned() {
        assertEquals(LocalDate.of(2026, 6, 6), TaskDateTime.parseDate("06/06/2026"));
    }

    @Test
    void parseDate_dateWithoutYear_currentYearUsed() {
        assertEquals(LocalDate.of(2026, 9, 7), TaskDateTime.parseDate("7-9"));
        assertEquals(LocalDate.of(2026, 9, 7), TaskDateTime.parseDate("07/09"));
    }

    @Test
    void parseDate_dateWithSurroundingWhitespace_parsedDateReturned() {
        assertEquals(LocalDate.of(2026, 12, 2), TaskDateTime.parseDate("  2-12-2026  "));
    }

    @Test
    void parseDate_leapDayInLeapYear_parsedDateReturned() {
        assertEquals(LocalDate.of(2024, 2, 29), TaskDateTime.parseDate("29-2-2024"));
    }

    @Test
    void parseDate_invalidCalendarDate_exceptionThrown() {
        assertThrows(DateTimeParseException.class, () ->
                TaskDateTime.parseDate("31-4-2026"));
        assertThrows(DateTimeParseException.class, () ->
                TaskDateTime.parseDate("29-2-2025"));
        assertThrows(DateTimeParseException.class, () ->
                TaskDateTime.parseDate("1-13-2026"));
    }

    @Test
    void parseDate_unsupportedFormat_exceptionThrown() {
        assertThrows(DateTimeParseException.class, () ->
                TaskDateTime.parseDate("2026-06-06"));
        assertThrows(DateTimeParseException.class, () ->
                TaskDateTime.parseDate("6-6-26"));
        assertThrows(DateTimeParseException.class, () ->
                TaskDateTime.parseDate("tomorrow"));
        assertThrows(DateTimeParseException.class, () ->
                TaskDateTime.parseDate(""));
    }

    @Test
    void parseDate_nullInput_nullPointerExceptionThrown() {
        assertThrows(NullPointerException.class, () -> TaskDateTime.parseDate(null));
    }

    @Test
    void formatDate_validDate_displayTextReturned() {
        assertEquals("2 Dec 2019", TaskDateTime.formatDate(LocalDate.of(2019, 12, 2)));
    }

    @Test
    void formatDate_nullInput_nullPointerExceptionThrown() {
        assertThrows(NullPointerException.class, () -> TaskDateTime.formatDate(null));
    }

    @Test
    void isOn_sameAndDifferentDates_expectedBooleanReturned() {
        TaskDateTime taskDateTime = new TaskDateTime(LocalDateTime.of(2026, 8, 6, 14, 0));

        assertTrue(taskDateTime.isOn(LocalDate.of(2026, 8, 6)));
        assertFalse(taskDateTime.isOn(LocalDate.of(2026, 8, 5)));
        assertFalse(taskDateTime.isOn(LocalDate.of(2026, 8, 7)));
    }

    @Test
    void isOn_legacyText_falseReturned() {
        TaskDateTime taskDateTime = TaskDateTime.fromStorageString("Friday evening");

        assertFalse(taskDateTime.isOn(LocalDate.of(2026, 8, 6)));
    }

    @Test
    void isOn_nullDate_nullPointerExceptionThrown() {
        TaskDateTime taskDateTime = new TaskDateTime(LocalDateTime.of(2026, 8, 6, 14, 0));

        assertThrows(NullPointerException.class, () -> taskDateTime.isOn(null));
    }

    @Test
    void toStorageString_parsedValue_isoDateTimeReturned() {
        TaskDateTime taskDateTime = new TaskDateTime(LocalDateTime.of(2026, 8, 6, 14, 0, 30));

        assertEquals("2026-08-06T14:00:30", taskDateTime.toStorageString());
    }

    @Test
    void toStorageString_legacyText_originalTextReturned() {
        TaskDateTime taskDateTime = TaskDateTime.fromStorageString("Friday evening");

        assertEquals("Friday evening", taskDateTime.toStorageString());
    }

    @Test
    void toString_parsedValue_userFriendlyTextReturned() {
        assertEquals("6 Aug 2026, 6:00 PM",
                new TaskDateTime(LocalDateTime.of(2026, 8, 6, 18, 0)).toString());
        assertEquals("6 Aug 2026, 12:00 AM",
                new TaskDateTime(LocalDateTime.of(2026, 8, 6, 0, 0)).toString());
        assertEquals("6 Aug 2026, 12:00 PM",
                new TaskDateTime(LocalDateTime.of(2026, 8, 6, 12, 0)).toString());
    }

    @Test
    void toString_legacyText_originalTextReturned() {
        TaskDateTime taskDateTime = TaskDateTime.fromStorageString("Friday evening");

        assertEquals("Friday evening", taskDateTime.toString());
    }
}
