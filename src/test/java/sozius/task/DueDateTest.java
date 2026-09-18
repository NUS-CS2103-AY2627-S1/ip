package sozius.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalTime;

import org.junit.jupiter.api.Test;

import sozius.exception.SoziusException;

public class DueDateTest {

    @Test
    public void parse_dateOnly_success() throws SoziusException {
        DueDate d = DueDate.parse("2024-12-31");
        assertEquals(LocalDate.of(2024, 12, 31), d.getDate());
        assertNull(d.getTime());
    }

    @Test
    public void parse_dateWithTime_success() throws SoziusException {
        DueDate d = DueDate.parse("2024-12-31 1800");
        assertEquals(LocalDate.of(2024, 12, 31), d.getDate());
        assertEquals(LocalTime.of(18, 0), d.getTime());
    }

    @Test
    public void parse_extraWhitespace_trimmed() throws SoziusException {
        DueDate d = DueDate.parse("   2024-01-01   0900  ");
        assertEquals(LocalDate.of(2024, 1, 1), d.getDate());
        assertEquals(LocalTime.of(9, 0), d.getTime());
    }

    @Test
    public void parse_emptyString_throwsException() {
        SoziusException e = assertThrows(SoziusException.class, () -> DueDate.parse(""));
        assertTrue(e.getMessage().contains("Missing date"));
    }

    @Test
    public void parse_null_throwsException() {
        assertThrows(SoziusException.class, () -> DueDate.parse(null));
    }

    @Test
    public void parse_wrongDateFormat_throwsException() {
        assertThrows(SoziusException.class, () -> DueDate.parse("31/12/2024"));
        assertThrows(SoziusException.class, () -> DueDate.parse("Dec 31 2024"));
        assertThrows(SoziusException.class, () -> DueDate.parse("2024-12-31T18:00"));
    }

    @Test
    public void parse_nonexistentCalendarDate_throwsException() {
        // 2023 is not a leap year
        SoziusException e = assertThrows(SoziusException.class, () -> DueDate.parse("2023-02-29"));
        assertTrue(e.getMessage().contains("does not exist"));
    }

    @Test
    public void parse_leapDayOnLeapYear_success() throws SoziusException {
        DueDate d = DueDate.parse("2024-02-29");
        assertEquals(LocalDate.of(2024, 2, 29), d.getDate());
    }

    @Test
    public void parse_wrongTimeFormat_throwsException() {
        assertThrows(SoziusException.class, () -> DueDate.parse("2024-12-31 18:00"));
        assertThrows(SoziusException.class, () -> DueDate.parse("2024-12-31 6pm"));
    }

    @Test
    public void parse_minuteOutOfRange_throwsException() {
        assertThrows(SoziusException.class, () -> DueDate.parse("2024-12-31 1260"));
    }

    @Test
    public void parse_hour24_throwsException() {
        // the error message promises hours 00-23, so 2400 should be rejected
        assertThrows(SoziusException.class, () -> DueDate.parse("2024-12-31 2400"));
    }

    @Test
    public void parse_tooManyParts_throwsException() {
        SoziusException e = assertThrows(SoziusException.class, () -> DueDate.parse("2024-12-31 1800 extra"));
        assertTrue(e.getMessage().contains("too many parts"));
    }

    @Test
    public void toUserString_dateOnly_formatsDate() throws SoziusException {
        DueDate d = DueDate.parse("2024-12-31");
        assertEquals("Dec 31 2024", d.toUserString());
    }

    @Test
    public void toUserString_withTime_formatsDateAndTime() throws SoziusException {
        DueDate d = DueDate.parse("2024-12-31 1800");
        assertEquals("Dec 31 2024 1800", d.toUserString());
    }

    @Test
    public void toFileString_dateOnly_formatsIsoDate() throws SoziusException {
        DueDate d = DueDate.parse("2024-12-31");
        assertEquals("2024-12-31", d.toFileString());
    }

    @Test
    public void toFileString_withTime_formatsIsoDateAndTime() throws SoziusException {
        DueDate d = DueDate.parse("2024-12-31 1800");
        assertEquals("2024-12-31 1800", d.toFileString());
    }

    @Test
    public void parse_roundTrip_preservesValue() throws SoziusException {
        DueDate original = DueDate.parse("2024-12-31 1800");
        DueDate reparsed = DueDate.parse(original.toFileString());
        assertEquals(original.getDate(), reparsed.getDate());
        assertEquals(original.getTime(), reparsed.getTime());
    }
}
