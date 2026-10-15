package bucket;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.format.DateTimeParseException;

import org.junit.jupiter.api.Test;

public class ParserTest {

    // ---------- splitting a line into command and argument ----------

    @Test
    public void getCommand_wordAndArgument_returnsFirstWordOnly() {
        assertEquals("todo", new Parser("todo read book").getCommand());
    }

    @Test
    public void getArgument_wordAndArgument_returnsEverythingAfterFirstWord() {
        assertEquals("read book", new Parser("todo read book").getArgument());
    }

    @Test
    public void getArgument_commandWithNoArgument_returnsEmptyString() {
        assertEquals("", new Parser("list").getArgument());
    }

    // An empty line must not blow up; it simply has no command word
    @Test
    public void getCommand_emptyInput_returnsEmptyString() {
        assertEquals("", new Parser("").getCommand());
    }

    @Test
    public void getCommand_onlySpaces_returnsEmptyString() {
        assertEquals("", new Parser("      ").getCommand());
    }

    // Leading and trailing spaces are the user being imprecise, not a different command
    @Test
    public void getCommand_surroundingSpaces_trimmed() {
        assertEquals("list", new Parser("   list   ").getCommand());
    }

    // Only the first space splits, so the rest of the line stays as typed
    @Test
    public void getArgument_severalSpacesInside_keptWhole() {
        assertEquals("read  the   book", new Parser("todo read  the   book").getArgument());
    }

    // ---------- deadlines ----------

    @Test
    public void toDeadline_validInput_descriptionAndDateParsed() {
        Deadline deadline = Parser.toDeadline("return book /by 2019-10-15");
        assertEquals("D | 0 | return book | 2019-10-15", deadline.toSaveString());
    }

    @Test
    public void toDeadline_descriptionWithSpaces_keptWhole() {
        Deadline deadline = Parser.toDeadline("return the library book /by 2019-10-15");
        assertEquals("D | 0 | return the library book | 2019-10-15", deadline.toSaveString());
    }

    @Test
    public void toDeadline_wordInsteadOfDate_throwsDateTimeParseException() {
        assertThrows(DateTimeParseException.class, () -> Parser.toDeadline("return book /by tomorrow"));
    }

    // Month 13 parses as text but is not a real date
    @Test
    public void toDeadline_impossibleMonth_throwsDateTimeParseException() {
        assertThrows(DateTimeParseException.class, () -> Parser.toDeadline("return book /by 2019-13-01"));
    }

    // 2019 was not a leap year, so the 29th of February does not exist
    @Test
    public void toDeadline_impossibleDayForMonth_throwsDateTimeParseException() {
        assertThrows(DateTimeParseException.class, () -> Parser.toDeadline("x /by 2019-02-29"));
    }

    @Test
    public void toDeadline_slashesInsteadOfDashes_throwsDateTimeParseException() {
        assertThrows(DateTimeParseException.class, () -> Parser.toDeadline("x /by 15/10/2019"));
    }

    @Test
    public void toDeadline_dayMonthOrder_throwsDateTimeParseException() {
        assertThrows(DateTimeParseException.class, () -> Parser.toDeadline("x /by 15-10-2019"));
    }

    // Missing the marker leaves nothing to read the date from
    @Test
    public void toDeadline_missingByMarker_throwsArrayIndexOutOfBounds() {
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> Parser.toDeadline("return book 2019-10-15"));
    }

    @Test
    public void toDeadline_nothingAfterByMarker_throwsDateTimeParseException() {
        assertThrows(DateTimeParseException.class, () -> Parser.toDeadline("return book /by "));
    }

    // ---------- events ----------

    // The save form is used to check the parse, because it shows the dates
    // as plain yyyy-mm-dd - so a wrong date can't hide behind the display format
    @Test
    public void toEvent_validInput_allThreePartsParsed() {
        Event event = Parser.toEvent("project meeting /from 2019-10-15 /to 2019-10-16");
        assertEquals("E | 0 | project meeting | 2019-10-15 | 2019-10-16", event.toSaveString());
    }

    // The description must survive the split with its spaces intact,
    // and the /from and /to markers must not be swallowed into it
    @Test
    public void toEvent_descriptionWithManySpaces_keptWhole() {
        Event event = Parser.toEvent("team lunch at the new place /from 2020-01-01 /to 2020-01-02");
        assertEquals("E | 0 | team lunch at the new place | 2020-01-01 | 2020-01-02", event.toSaveString());
    }

    // A date that isn't a real date should be rejected, not silently accepted
    @Test
    public void toEvent_invalidDate_throwsDateTimeParseException() {
        assertThrows(DateTimeParseException.class, ()
                -> Parser.toEvent("meeting /from tomorrow /to 2019-10-16"));
    }

    // Month 13 parses as text but isn't a real date, so it must still be rejected
    @Test
    public void toEvent_impossibleDate_throwsDateTimeParseException() {
        assertThrows(DateTimeParseException.class, ()
                -> Parser.toEvent("meeting /from 2019-13-01 /to 2019-10-16"));
    }

    // The second date is checked just as strictly as the first
    @Test
    public void toEvent_invalidEndDate_throwsDateTimeParseException() {
        assertThrows(DateTimeParseException.class, ()
                -> Parser.toEvent("meeting /from 2019-10-15 /to someday"));
    }

    @Test
    public void toEvent_missingFromMarker_throwsArrayIndexOutOfBounds() {
        assertThrows(ArrayIndexOutOfBoundsException.class, ()
                -> Parser.toEvent("meeting 2019-10-15 /to 2019-10-16"));
    }

    @Test
    public void toEvent_missingToMarker_throwsArrayIndexOutOfBounds() {
        assertThrows(ArrayIndexOutOfBoundsException.class, ()
                -> Parser.toEvent("meeting /from 2019-10-15"));
    }

    /*
     * An event that ends before it starts is accepted here on purpose. Parser's job
     * is to read what was typed, and both dates are real dates; whether the pair
     * makes sense is a separate question this class deliberately does not answer.
     */
    @Test
    public void toEvent_endBeforeStart_stillParsed() {
        Event event = Parser.toEvent("backwards /from 2019-10-16 /to 2019-10-15");
        assertEquals("E | 0 | backwards | 2019-10-16 | 2019-10-15", event.toSaveString());
    }

    // ---------- task numbers ----------

    // What the user types is one-based; the list underneath is zero-based
    @Test
    public void toIndex_one_returnsZero() {
        assertEquals(0, Parser.toIndex("1"));
    }

    @Test
    public void toIndex_ten_returnsNine() {
        assertEquals(9, Parser.toIndex("10"));
    }

    @Test
    public void toIndex_notANumber_throwsNumberFormatException() {
        assertThrows(NumberFormatException.class, () -> Parser.toIndex("abc"));
    }

    @Test
    public void toIndex_emptyArgument_throwsNumberFormatException() {
        assertThrows(NumberFormatException.class, () -> Parser.toIndex(""));
    }

    @Test
    public void toIndex_decimal_throwsNumberFormatException() {
        assertThrows(NumberFormatException.class, () -> Parser.toIndex("1.5"));
    }

    /*
     * "mark 0" gives -1, and that is correct here. Parser only shifts the number;
     * whether an index exists is for the list to say, which is what turns it into a
     * message the user sees rather than a crash.
     */
    @Test
    public void toIndex_zero_returnsMinusOneRatherThanThrowing() {
        assertEquals(-1, Parser.toIndex("0"));
    }
}
