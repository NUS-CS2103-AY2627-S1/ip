package harvey.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.util.Locale;

import org.junit.jupiter.api.Test;

import harvey.HarveyException;

/**
 * Tests {@link Deadline}, in particular the two directions a date travels.
 * <p>
 * A deadline is written to the save file in one format and shown to the user in another,
 * and the same {@code parseDate} reads both what the user types and what the file holds.
 * If those two formats ever stop agreeing, tasks silently vanish on the next startup, so
 * the round trip is checked explicitly here.
 */
public class DeadlineTest {
    @Test
    public void parseDate_isoDate_returnsThatDate() throws HarveyException {
        assertEquals(LocalDate.of(2019, 10, 15), Deadline.parseDate("2019-10-15"));
    }

    @Test
    public void parseDate_leapDay_returnsThatDate() throws HarveyException {
        // 2020 is a leap year, so this is a real date and must be accepted.
        assertEquals(LocalDate.of(2020, 2, 29), Deadline.parseDate("2020-02-29"));
    }

    @Test
    public void parseDate_dayThatDoesNotExist_exceptionThrown() {
        // Well formed but impossible: 2019 is not a leap year. A parser that only
        // checked the shape of the text would let this through.
        assertThrows(HarveyException.class, () -> Deadline.parseDate("2019-02-29"));
    }

    @Test
    public void parseDate_dayMonthYearOrder_exceptionThrown() {
        assertThrows(HarveyException.class, () -> Deadline.parseDate("15/10/2019"));
    }

    @Test
    public void parseDate_words_exceptionThrown() {
        assertThrows(HarveyException.class, () -> Deadline.parseDate("tomorrow"));
    }

    @Test
    public void parseDate_emptyText_exceptionThrown() {
        assertThrows(HarveyException.class, () -> Deadline.parseDate(""));
    }

    @Test
    public void parseDate_unparseableText_messageNamesTheExpectedFormat() {
        HarveyException e = assertThrows(
                HarveyException.class, () -> Deadline.parseDate("tomorrow"));
        // The message has to tell the user what to type instead, not just that it failed.
        assertEquals("I could not read \"tomorrow\" as a date. "
                + "Please write it as yyyy-mm-dd, for example 2019-10-15.", e.getMessage());
    }

    @Test
    public void toString_notDone_showsDisplayDateFormat() {
        Deadline deadline = new Deadline("return book", LocalDate.of(2019, 10, 15));
        assertEquals("[D][ ] return book (by: Oct 15 2019)", deadline.toString());
    }

    @Test
    public void toString_done_showsCross() {
        Deadline deadline = new Deadline("return book", LocalDate.of(2019, 10, 15));
        deadline.markAsDone();
        assertEquals("[D][X] return book (by: Oct 15 2019)", deadline.toString());
    }

    @Test
    public void toFileFormat_notDone_usesIsoDateAndZeroFlag() {
        Deadline deadline = new Deadline("return book", LocalDate.of(2019, 10, 15));
        // The file must hold the input format, not the display format.
        assertEquals("D | 0 | return book | 2019-10-15", deadline.toFileFormat());
    }

    @Test
    public void toFileFormat_done_usesOneFlag() {
        Deadline deadline = new Deadline("return book", LocalDate.of(2019, 10, 15));
        deadline.markAsDone();
        assertEquals("D | 1 | return book | 2019-10-15", deadline.toFileFormat());
    }

    @Test
    public void toFileFormat_thenParseDate_dateSurvivesTheRoundTrip() throws HarveyException {
        LocalDate original = LocalDate.of(2019, 10, 15);
        String line = new Deadline("return book", original).toFileFormat();

        // Pull the date back out of the line the same way Storage does, and check
        // parseDate accepts it. This is the property that keeps saved tasks loadable.
        String savedDate = line.substring(line.lastIndexOf(" | ") + 3);
        assertEquals(original, Deadline.parseDate(savedDate));
    }

    @Test
    public void toString_computerSetToChinese_monthStillInEnglish() {
        // Locale.setDefault is what a computer's language setting changes. It is global,
        // so it is put back in finally even if the assertion fails.
        Locale original = Locale.getDefault();
        try {
            Locale.setDefault(Locale.CHINA);
            assertEquals("[D][ ] return book (by: Oct 15 2019)",
                    new Deadline("return book", LocalDate.of(2019, 10, 15)).toString());
        } finally {
            Locale.setDefault(original);
        }
    }
}
