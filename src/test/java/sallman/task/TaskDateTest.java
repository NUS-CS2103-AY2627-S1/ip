package sallman.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.util.Locale;

import org.junit.jupiter.api.Test;

import sallman.SallmanException;

/**
 * Tests that {@link TaskDate} accepts the input format and only that format,
 * and shows dates back in the display format.
 */
public class TaskDateTest {

    @Test
    public void parse_inputFormat_dateReturned() throws Exception {
        assertEquals(LocalDate.of(2019, 10, 15), TaskDate.parse("2019-10-15"));
    }

    @Test
    public void parse_leapDayInALeapYear_accepted() throws Exception {
        assertEquals(LocalDate.of(2020, 2, 29), TaskDate.parse("2020-02-29"));
    }

    @Test
    public void parse_impossibleCalendarDate_exceptionThrown() {
        // These have the right shape but cannot exist, so a check on the
        // pattern alone would let them through.
        assertThrows(SallmanException.class, () -> TaskDate.parse("2019-02-30"));
        assertThrows(SallmanException.class, () -> TaskDate.parse("2019-02-29"));
        assertThrows(SallmanException.class, () -> TaskDate.parse("2019-13-01"));
    }

    @Test
    public void parse_dayFirstFormat_exceptionThrown() {
        // A natural thing to type, and wrong, so it must not be guessed at.
        assertThrows(SallmanException.class, () -> TaskDate.parse("15-10-2019"));
    }

    @Test
    public void parse_wordsRatherThanADate_exceptionNamesTheFormat() {
        SallmanException e = assertThrows(SallmanException.class, () -> TaskDate.parse("Sunday"));
        assertEquals("I'm sorry, but I couldn't read \"Sunday\" as a date.", e.getMessage());
    }

    @Test
    public void parse_dayPastTheEndOfTheMonth_exceptionNamesTheMonthLength() {
        SallmanException e = assertThrows(SallmanException.class, () -> TaskDate.parse("2019-02-30"));

        assertEquals("As a large language model, I must point out there is no such date as 2019-02-30.",
                e.getMessage());
        assertEquals("February 2019 has 28 days.", e.toLines()[1]);
    }

    @Test
    public void parse_leapDayInACommonYear_exceptionNamesTheMonthLength() {
        SallmanException e = assertThrows(SallmanException.class, () -> TaskDate.parse("2019-02-29"));

        assertEquals("February 2019 has 28 days.", e.toLines()[1]);
    }

    @Test
    public void parse_monthOutOfRange_exceptionNamesTheMonth() {
        SallmanException e = assertThrows(SallmanException.class, () -> TaskDate.parse("2019-13-01"));

        assertEquals("As a large language model, I must point out there is no month 13 in \"2019-13-01\".",
                e.getMessage());
    }

    @Test
    public void parse_monthZero_exceptionNamesTheMonth() {
        SallmanException e = assertThrows(SallmanException.class, () -> TaskDate.parse("2019-00-15"));

        assertEquals("Months run from 01 to 12.", e.toLines()[1]);
    }

    @Test
    public void format_computerSetToAnotherLanguage_monthStillInEnglish() {
        // British and Singapore English write "Sept", German "Sept.", and Chinese
        // uses its own characters, so the display must not follow the computer.
        Locale original = Locale.getDefault();
        try {
            for (String language : new String[] {"en-SG", "en-GB", "de-DE", "zh-CN"}) {
                Locale.setDefault(Locale.forLanguageTag(language));
                assertEquals("Sep 25 2026", TaskDate.format(LocalDate.of(2026, 9, 25)), language);
            }
        } finally {
            Locale.setDefault(original);
        }
    }

    @Test
    public void parse_emptyText_exceptionThrown() {
        assertThrows(SallmanException.class, () -> TaskDate.parse(""));
    }

    @Test
    public void format_anyDate_displayFormatUsed() {
        // Displayed differently from how it is typed, and the day is padded to
        // two digits so listed dates line up.
        assertEquals("Oct 15 2019", TaskDate.format(LocalDate.of(2019, 10, 15)));
        assertEquals("Aug 06 2019", TaskDate.format(LocalDate.of(2019, 8, 6)));
        assertEquals("Jan 01 2020", TaskDate.format(LocalDate.of(2020, 1, 1)));
    }

    @Test
    public void formatThenParse_sameDate_survivesBothConversions() throws Exception {
        LocalDate original = LocalDate.of(2019, 10, 15);
        assertEquals(original, TaskDate.parse(original.toString()));
    }
}
