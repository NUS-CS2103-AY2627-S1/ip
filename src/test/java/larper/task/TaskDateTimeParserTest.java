package larper.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TaskDateTimeParserTest {
    private static final String TODAY_PROPERTY = "larper.today";

    @BeforeEach
    public void setUp() {
        System.setProperty(TODAY_PROPERTY, "2026-08-23");
    }

    @AfterEach
    public void tearDown() {
        System.clearProperty(TODAY_PROPERTY);
    }

    @Test
    public void parse_supportedDateTimeInputs_expectedDateAndTime() {
        TaskDateTime isoDateTime = TaskDateTimeParser.parse("by 2019-10-15 2pm", "no time");
        assertEquals(LocalDate.of(2019, 10, 15), isoDateTime.getDate());
        assertEquals("1400", isoDateTime.getTime());

        TaskDateTime slashDateTime = TaskDateTimeParser.parse("2/12/2019 18:30", "no time");
        assertEquals(LocalDate.of(2019, 12, 2), slashDateTime.getDate());
        assertEquals("1830", slashDateTime.getTime());

        TaskDateTime monthNameDateTime = TaskDateTimeParser.parse("from AUGUST 6th 9:30AM", "no time");
        assertEquals(LocalDate.of(2026, 8, 6), monthNameDateTime.getDate());
        assertEquals("0930", monthNameDateTime.getTime());

        TaskDateTime noTimeDateTime = TaskDateTimeParser.parse("to 8 Sept 2026 no time", "1400");
        assertEquals(LocalDate.of(2026, 9, 8), noTimeDateTime.getDate());
        assertEquals("no time", noTimeDateTime.getTime());

        TaskDateTime defaultTimeDateTime = TaskDateTimeParser.parse("Aug 8", "0830");
        assertEquals(LocalDate.of(2026, 8, 8), defaultTimeDateTime.getDate());
        assertEquals("0830", defaultTimeDateTime.getTime());
    }

    @Test
    public void parse_missingDate_exceptionThrown() {
        assertThrows(IllegalArgumentException.class, () -> TaskDateTimeParser.parse("", "no time"));
        assertThrows(IllegalArgumentException.class, () -> TaskDateTimeParser.parse("by no time", "no time"));
        assertThrows(RuntimeException.class, () -> TaskDateTimeParser.parse("not a date", "no time"));
    }

    @Test
    public void parseDate_supportedDateFormats_expectedLocalDate() {
        assertEquals(LocalDate.of(2019, 10, 15), TaskDateTimeParser.parseDate("2019-10-15"));
        assertEquals(LocalDate.of(2019, 12, 2), TaskDateTimeParser.parseDate("2/12/2019"));
        assertEquals(LocalDate.of(2026, 8, 6), TaskDateTimeParser.parseDate("Aug 6"));
        assertEquals(LocalDate.of(2026, 8, 6), TaskDateTimeParser.parseDate("August 6th"));
        assertEquals(LocalDate.of(2026, 8, 6), TaskDateTimeParser.parseDate("August 6th, 2026"));
        assertEquals(LocalDate.of(2026, 8, 6), TaskDateTimeParser.parseDate("6 AUGUST"));
        assertEquals(LocalDate.of(2026, 9, 8), TaskDateTimeParser.parseDate("8 Sept 2026"));
        assertEquals(LocalDate.of(2026, 1, 9), TaskDateTimeParser.parseDate("janurary 9"));
    }

    @Test
    public void parseDate_weekdayInput_expectedNextMatchingDate() {
        assertEquals(LocalDate.of(2026, 8, 23), TaskDateTimeParser.parseDate("Sunday"));
        assertEquals(LocalDate.of(2026, 8, 24), TaskDateTimeParser.parseDate("Mon"));
        assertEquals(LocalDate.of(2026, 8, 29), TaskDateTimeParser.parseDate("sat"));
    }

    @Test
    public void normalizeTime_supportedFormats_expectedMilitaryTime() {
        assertEquals("1400", TaskDateTimeParser.normalizeTime("2pm"));
        assertEquals("1430", TaskDateTimeParser.normalizeTime("2:30pm"));
        assertEquals("0000", TaskDateTimeParser.normalizeTime("12am"));
        assertEquals("1200", TaskDateTimeParser.normalizeTime("12pm"));
        assertEquals("0930", TaskDateTimeParser.normalizeTime("930"));
        assertEquals("0930", TaskDateTimeParser.normalizeTime("09:30"));
        assertEquals("1800", TaskDateTimeParser.normalizeTime("18:00"));
        assertEquals("no time", TaskDateTimeParser.normalizeTime("No Time"));
    }

    @Test
    public void normalizeTime_invalidFormat_exceptionThrown() {
        assertThrows(IllegalArgumentException.class, () -> TaskDateTimeParser.normalizeTime("evening"));
        assertThrows(IllegalArgumentException.class, () -> TaskDateTimeParser.normalizeTime(""));
    }

    @Test
    public void looksLikeTime_supportedAndUnsupportedInputs_expectedBoolean() {
        assertTrue(TaskDateTimeParser.looksLikeTime("2pm"));
        assertTrue(TaskDateTimeParser.looksLikeTime("2:30pm"));
        assertTrue(TaskDateTimeParser.looksLikeTime("1400"));
        assertTrue(TaskDateTimeParser.looksLikeTime("14:00"));

        assertFalse(TaskDateTimeParser.looksLikeTime("no time"));
        assertFalse(TaskDateTimeParser.looksLikeTime("2 pm"));
        assertFalse(TaskDateTimeParser.looksLikeTime("tomorrow"));
    }

    @Test
    public void isValidTimeAnswer_supportedAndUnsupportedInputs_expectedBoolean() {
        assertTrue(TaskDateTimeParser.isValidTimeAnswer("no time"));
        assertTrue(TaskDateTimeParser.isValidTimeAnswer("2pm"));
        assertTrue(TaskDateTimeParser.isValidTimeAnswer("14:00"));

        assertFalse(TaskDateTimeParser.isValidTimeAnswer("later"));
    }

    @Test
    public void containsMonthName_supportedMonthWords_expectedBoolean() {
        assertTrue(TaskDateTimeParser.containsMonthName("deadline on AUGUST 6th"));
        assertTrue(TaskDateTimeParser.containsMonthName("deadline on feb 7"));
        assertTrue(TaskDateTimeParser.containsMonthName("deadline on janurary 9"));

        assertFalse(TaskDateTimeParser.containsMonthName("deadline on Monday"));
        assertFalse(TaskDateTimeParser.containsMonthName("remember"));
    }

    @Test
    public void formatDate_validDate_expectedDisplayFormat() {
        assertEquals("Oct 15 2019", TaskDateTimeParser.formatDate(LocalDate.of(2019, 10, 15)));
        assertEquals("Aug 06 2026", TaskDateTimeParser.formatDate(LocalDate.of(2026, 8, 6)));
    }
}
