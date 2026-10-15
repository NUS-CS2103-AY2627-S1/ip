package pan;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link Parser}, the app's pure text-parsing helpers.
 *
 * <p>{@code Parser} is the highest-value class to test here: its methods are
 * {@code static} and side-effect free (text in, value or {@link PanException}
 * out), so they can be checked directly without stubbing files or console I/O.
 *
 * <p>Happy-path tests assert on {@link Task#toFileString()} rather than
 * {@code toString()}: {@code toFileString()} writes the date in fixed ISO-8601
 * form, so the assertion does not depend on the machine's locale (unlike the
 * {@code h:mma} display format used by {@code toString()}).
 *
 * <p>Failure-path tests only check that a {@link PanException} is thrown, not
 * its message text - the messages are long, informal, and likely to be reworded,
 * which would make message assertions brittle.
 */
public class ParserTest {

    // ---------- commandWord ----------

    @Test
    public void commandWord_multiWordInput_returnsFirstWord() {
        assertEquals("deadline",
                Parser.commandWord("deadline return book /by 2019-12-01 1800"));
    }

    @Test
    public void commandWord_singleWord_returnsWholeWord() {
        assertEquals("list", Parser.commandWord("list"));
    }

    @Test
    public void commandWord_surroundingWhitespace_isTrimmed() {
        assertEquals("mark", Parser.commandWord("   mark 2   "));
    }

    @Test
    public void commandWord_emptyOrBlankInput_returnsEmptyString() {
        assertEquals("", Parser.commandWord(""));
        assertEquals("", Parser.commandWord("    "));
    }

    // ---------- arguments ----------

    @Test
    public void arguments_multiWordInput_returnsEverythingAfterFirstWord() {
        assertEquals("return book /by 2019-12-01 1800",
                Parser.arguments("deadline return book /by 2019-12-01 1800"));
    }

    @Test
    public void arguments_commandOnly_returnsEmptyString() {
        assertEquals("", Parser.arguments("list"));
    }

    @Test
    public void arguments_extraWhitespaceBetweenWords_isTrimmed() {
        assertEquals("read", Parser.arguments("todo     read    "));
    }

    // ---------- parseDateTime ----------

    @Test
    public void parseDateTime_validInputFormat_returnsMatchingDateTime() throws PanException {
        assertEquals(LocalDateTime.of(2019, 10, 15, 18, 0),
                Parser.parseDateTime("2019-10-15 1800"));
    }

    @Test
    public void parseDateTime_surroundingWhitespace_stillParses() throws PanException {
        assertEquals(LocalDateTime.of(2019, 10, 15, 18, 0),
                Parser.parseDateTime("   2019-10-15 1800   "));
    }

    @Test
    public void parseDateTime_wrongSeparators_exceptionThrown() {
        assertThrows(PanException.class, () -> Parser.parseDateTime("2019/10/15 6pm"));
    }

    @Test
    public void parseDateTime_freeTextDate_exceptionThrown() {
        assertThrows(PanException.class, () -> Parser.parseDateTime("next Sunday"));
    }

    @Test
    public void parseDateTime_missingTimeComponent_exceptionThrown() {
        assertThrows(PanException.class, () -> Parser.parseDateTime("2019-10-15"));
    }

    @Test
    public void parseDateTime_impossibleCalendarValues_exceptionThrown() {
        assertThrows(PanException.class, () -> Parser.parseDateTime("2019-13-40 1800"));
    }

    @Test
    public void parseDateTime_emptyString_exceptionThrown() {
        assertThrows(PanException.class, () -> Parser.parseDateTime(""));
    }

    // ---------- parseDeadline ----------

    @Test
    public void parseDeadline_validArguments_deadlineCreated() throws PanException {
        Deadline deadline = Parser.parseDeadline("return book /by 2019-12-01 1800");
        assertEquals("D | 0 | return book | 2019-12-01T18:00", deadline.toFileString());
    }

    @Test
    public void parseDeadline_extraWhitespace_trimmedAndParsed() throws PanException {
        Deadline deadline = Parser.parseDeadline("   return book    /by    2019-12-01 1800   ");
        assertEquals("D | 0 | return book | 2019-12-01T18:00", deadline.toFileString());
    }

    @Test
    public void parseDeadline_missingDescription_exceptionThrown() {
        assertThrows(PanException.class, () -> Parser.parseDeadline("/by 2019-12-01 1800"));
    }

    @Test
    public void parseDeadline_missingByKeyword_exceptionThrown() {
        assertThrows(PanException.class, () -> Parser.parseDeadline("return book"));
    }

    @Test
    public void parseDeadline_byKeywordButNoDate_exceptionThrown() {
        assertThrows(PanException.class, () -> Parser.parseDeadline("return book /by "));
    }

    @Test
    public void parseDeadline_unparseableDate_exceptionThrown() {
        assertThrows(PanException.class, () -> Parser.parseDeadline("return book /by tomorrow"));
    }

    // ---------- parseEvent ----------

    @Test
    public void parseEvent_validArguments_eventCreated() throws PanException {
        Event event = Parser.parseEvent(
                "project meeting /from 2019-12-01 1400 /to 2019-12-01 1600");
        assertEquals("E | 0 | project meeting | 2019-12-01T14:00 | 2019-12-01T16:00",
                event.toFileString());
    }

    @Test
    public void parseEvent_missingDescription_exceptionThrown() {
        assertThrows(PanException.class, () ->
                Parser.parseEvent("/from 2019-12-01 1400 /to 2019-12-01 1600"));
    }

    @Test
    public void parseEvent_missingFromKeyword_exceptionThrown() {
        assertThrows(PanException.class, () -> Parser.parseEvent("project meeting"));
    }

    @Test
    public void parseEvent_missingToKeyword_exceptionThrown() {
        assertThrows(PanException.class, () ->
                Parser.parseEvent("project meeting /from 2019-12-01 1400"));
    }

    @Test
    public void parseEvent_emptyFromValue_exceptionThrown() {
        assertThrows(PanException.class, () ->
                Parser.parseEvent("project meeting /from /to 2019-12-01 1600"));
    }

    @Test
    public void parseEvent_emptyToValue_exceptionThrown() {
        assertThrows(PanException.class, () ->
                Parser.parseEvent("project meeting /from 2019-12-01 1400 /to "));
    }

    @Test
    public void parseEvent_unparseableFromDate_exceptionThrown() {
        assertThrows(PanException.class, () ->
                Parser.parseEvent("project meeting /from someday /to 2019-12-01 1600"));
    }

    @Test
    public void parseEvent_unparseableToDate_exceptionThrown() {
        assertThrows(PanException.class, () ->
                Parser.parseEvent("project meeting /from 2019-12-01 1600 /to someday"));
    }
    // ---------- parseUpdateOption ----------

    @Test
    public void parseUpdateOption_dateOption_returnsNameAndValue() throws PanException {
        Parser.UpdateOption option = Parser.parseUpdateOption("/to 2019-12-01 1900");

        assertEquals("to", option.name());
        assertEquals("2019-12-01 1900", option.value());
    }

    @Test
    public void parseUpdateOption_descriptionOption_keepsWholeValue() throws PanException {
        Parser.UpdateOption option = Parser.parseUpdateOption("/desc read a long book");

        assertEquals("desc", option.name());
        assertEquals("read a long book", option.value());
    }

    @Test
    public void parseUpdateOption_surroundingWhitespace_isTrimmed() throws PanException {
        Parser.UpdateOption option = Parser.parseUpdateOption("   /by 2019-12-01 1800   ");

        assertEquals("by", option.name());
        assertEquals("2019-12-01 1800", option.value());
    }

    @Test
    public void parseUpdateOption_slashInsideValue_isNotASecondOption() throws PanException {
        Parser.UpdateOption option = Parser.parseUpdateOption("/desc read a/b");

        assertEquals("read a/b", option.value());
    }

    @Test
    public void parseUpdateOption_missingSlash_exceptionThrown() {
        assertThrows(PanException.class, () -> Parser.parseUpdateOption("by 2019-12-01 1800"));
    }

    @Test
    public void parseUpdateOption_missingValue_exceptionThrown() {
        assertThrows(PanException.class, () -> Parser.parseUpdateOption("/by"));
    }

    @Test
    public void parseUpdateOption_emptyInput_exceptionThrown() {
        assertThrows(PanException.class, () -> Parser.parseUpdateOption(""));
    }

    @Test
    public void parseEvent_endBeforeStart_exceptionThrown() {
        assertThrows(PanException.class, () ->
                Parser.parseEvent("party /from 2019-12-01 1600 /to 2019-12-01 1400"));
    }

    @Test
    public void parseEvent_endEqualsStart_isAccepted() throws PanException {
        Event event = Parser.parseEvent("party /from 2019-12-01 1600 /to 2019-12-01 1600");

        assertEquals("E | 0 | party | 2019-12-01T16:00 | 2019-12-01T16:00",
                event.toFileString());
    }

    @Test
    public void parseUpdateOption_upperCaseOption_isFoldedToLowerCase() throws PanException {
        Parser.UpdateOption option = Parser.parseUpdateOption("/BY 2019-12-01 1800");

        assertEquals("by", option.name());
        assertEquals("2019-12-01 1800", option.value());
    }

    @Test
    public void parseUpdateOption_twoOptions_exceptionThrown() {
        assertThrows(PanException.class, () ->
                Parser.parseUpdateOption("/desc lunch /by 2019-12-01 1800"));
    }
}
