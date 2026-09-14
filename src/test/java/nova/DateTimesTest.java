package nova;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

public class DateTimesTest {

    @Test
    public void parse_isoDateWithTime_returnsThatMoment() throws NovaException {
        assertEquals(LocalDateTime.of(2019, 12, 2, 18, 0), DateTimes.parse("2019-12-02 1800"));
        assertEquals(LocalDateTime.of(2019, 12, 2, 18, 0), DateTimes.parse("2019-12-02 18:00"));
    }

    @Test
    public void parse_slashDateWithTime_returnsThatMoment() throws NovaException {
        assertEquals(LocalDateTime.of(2019, 12, 2, 18, 0), DateTimes.parse("2/12/2019 1800"));
        assertEquals(LocalDateTime.of(2019, 12, 2, 18, 0), DateTimes.parse("02/12/2019 18:00"));
    }

    @Test
    public void parse_dateWithoutTime_returnsMidnight() throws NovaException {
        assertEquals(LocalDateTime.of(2019, 12, 2, 0, 0), DateTimes.parse("2019-12-02"));
        assertEquals(LocalDateTime.of(2019, 12, 2, 0, 0), DateTimes.parse("2/12/2019"));
    }

    @Test
    public void parse_isoText_returnsThatMoment() throws NovaException {
        assertEquals(LocalDateTime.of(2019, 12, 2, 18, 0), DateTimes.parse("2019-12-02T18:00:00"));
    }

    @Test
    public void parse_surroundingWhitespace_ignored() throws NovaException {
        assertEquals(LocalDateTime.of(2019, 12, 2, 0, 0), DateTimes.parse("   2019-12-02   "));
    }

    @Test
    public void parse_unrecognisedText_exceptionThrown() {
        NovaException e = assertThrows(NovaException.class, () -> DateTimes.parse("tomorrow"));
        assertTrue(e.getMessage().contains("tomorrow"));
        assertTrue(e.getMessage().contains("2019-12-02"));
    }

    @Test
    public void parse_impossibleDate_exceptionThrown() {
        assertThrows(NovaException.class, () -> DateTimes.parse("2019-02-30"));
        assertThrows(NovaException.class, () -> DateTimes.parse("2019-13-01"));
        assertThrows(NovaException.class, () -> DateTimes.parse("2019-12-02 2500"));
    }

    @Test
    public void parse_emptyText_exceptionThrown() {
        assertThrows(NovaException.class, () -> DateTimes.parse(""));
    }

    @Test
    public void format_midnight_timeOmitted() {
        assertEquals("Dec 02 2019", DateTimes.format(LocalDateTime.of(2019, 12, 2, 0, 0)));
    }

    @Test
    public void format_timeOfDay_timeShownInLowerCase() {
        assertEquals("Dec 02 2019, 6:00pm", DateTimes.format(LocalDateTime.of(2019, 12, 2, 18, 0)));
        assertEquals("Aug 06 2019, 9:05am", DateTimes.format(LocalDateTime.of(2019, 8, 6, 9, 5)));
        assertEquals("Aug 06 2019, 12:30pm", DateTimes.format(LocalDateTime.of(2019, 8, 6, 12, 30)));
    }

    @Test
    public void formatDate_anyDate_usesDisplayFormat() {
        assertEquals("Jun 06 2019", DateTimes.formatDate(LocalDate.of(2019, 6, 6)));
    }

    @Test
    public void parseDate_dateWithTime_timeDiscarded() throws NovaException {
        assertEquals(LocalDate.of(2019, 12, 2), DateTimes.parseDate("2019-12-02 1800"));
    }

    @Test
    public void toFileString_anyMoment_roundTripsThroughParse() throws NovaException {
        LocalDateTime original = LocalDateTime.of(2019, 12, 2, 18, 0);
        String saved = DateTimes.toFileString(original);

        assertEquals("2019-12-02T18:00:00", saved);
        assertEquals(original, DateTimes.parse(saved));
    }
}
