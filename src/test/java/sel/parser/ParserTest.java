package sel.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import sel.command.CommandType;
import sel.exception.SelException;

public class ParserTest {

    @Test
    public void parseCommandType_recognisesAllKnownCommands() {
        assertEquals(CommandType.BYE, Parser.parseCommandType("bye"));
        assertEquals(CommandType.LIST, Parser.parseCommandType("list"));
        assertEquals(CommandType.MARK, Parser.parseCommandType("mark 1"));
        assertEquals(CommandType.UNMARK, Parser.parseCommandType("unmark 1"));
        assertEquals(CommandType.DELETE, Parser.parseCommandType("delete 1"));
        assertEquals(CommandType.TODO, Parser.parseCommandType("todo read book"));
        assertEquals(CommandType.DEADLINE, Parser.parseCommandType("deadline return book /by 2019-12-02 1800"));
        assertEquals(CommandType.EVENT, Parser.parseCommandType("event meeting /from 2019-12-02 1400 /to 1600"));
        assertEquals(CommandType.FIND, Parser.parseCommandType("find book"));
    }

    @Test
    public void parseCommandType_unknownWord_returnsUnknown() {
        assertEquals(CommandType.UNKNOWN, Parser.parseCommandType("frobnicate"));
    }

    @Test
    public void parseCommandType_isCaseSensitiveAndOnlyLooksAtFirstWord() {
        assertEquals(CommandType.UNKNOWN, Parser.parseCommandType("BYE"));
        assertEquals(CommandType.LIST, Parser.parseCommandType("list please"));
    }

    @Test
    public void parseCommandType_leadingWhitespace_stillRecognised() {
        assertEquals(CommandType.BYE, Parser.parseCommandType("   bye"));
    }

    @Test
    public void parseIndex_validNumber_returnsZeroBasedIndex() throws SelException {
        int index = Parser.parseIndex("mark 3", "mark", "missing", "invalid");
        assertEquals(2, index);
    }

    @Test
    public void parseIndex_missingArgument_throwsWithMissingArgMessage() {
        SelException e = assertThrows(SelException.class, () ->
            Parser.parseIndex("mark", "mark", "missing arg", "invalid number"));
        assertEquals("missing arg", e.getMessage());
    }

    @Test
    public void parseIndex_nonNumericArgument_throwsWithInvalidNumberMessage() {
        SelException e = assertThrows(SelException.class, () ->
            Parser.parseIndex("mark abc", "mark", "missing arg", "invalid number"));
        assertEquals("invalid number", e.getMessage());
    }

    @Test
    public void parseIndex_extraWhitespaceAroundNumber_isTrimmed() throws SelException {
        int index = Parser.parseIndex("mark   3", "mark", "missing", "invalid");
        assertEquals(2, index);
    }

    @Test
    public void parseSimpleArgument_validArgument_returnsTrimmedText() throws SelException {
        String description = Parser.parseSimpleArgument("todo read book", "todo", "error");
        assertEquals("read book", description);
    }

    @Test
    public void parseSimpleArgument_commandWordOnly_throws() {
        SelException e = assertThrows(SelException.class, () ->
            Parser.parseSimpleArgument("todo", "todo", "empty task error"));
        assertEquals("empty task error", e.getMessage());
    }

    @Test
    public void parseSimpleArgument_onlyWhitespaceAfterCommandWord_throws() {
        SelException e = assertThrows(SelException.class, () ->
            Parser.parseSimpleArgument("todo    ", "todo", "empty task error"));
        assertEquals("empty task error", e.getMessage());
    }

    @Test
    public void parseDeadlineArgs_validCommand_extractsDescriptionAndDeadline() throws SelException {
        String[] args = Parser.parseDeadlineArgs("deadline return book /by 2019-12-02 1800");
        assertEquals("return book", args[0]);
        assertEquals("2019-12-02 1800", args[1]);
    }

    @Test
    public void parseDeadlineArgs_missingByMarker_throws() {
        assertThrows(SelException.class, () -> Parser.parseDeadlineArgs("deadline return book"));
    }

    @Test
    public void parseDeadlineArgs_emptyDescription_throws() {
        assertThrows(SelException.class, () -> Parser.parseDeadlineArgs("deadline /by 2019-12-02 1800"));
    }

    @Test
    public void parseDeadlineArgs_emptyDeadline_throws() {
        assertThrows(SelException.class, () -> Parser.parseDeadlineArgs("deadline return book /by"));
    }

    @Test
    public void parseDeadlineArgs_bareCommandWord_throws() {
        assertThrows(SelException.class, () -> Parser.parseDeadlineArgs("deadline"));
    }

    @Test
    public void parseEventArgs_validCommand_extractsAllThreeFields() throws SelException {
        String[] args = Parser.parseEventArgs("event project meeting /from 2019-12-02 1400 /to 2019-12-02 1600");
        assertEquals("project meeting", args[0]);
        assertEquals("2019-12-02 1400", args[1]);
        assertEquals("2019-12-02 1600", args[2]);
    }

    @Test
    public void parseEventArgs_missingFromMarker_throws() {
        assertThrows(SelException.class, () ->
            Parser.parseEventArgs("event project meeting /to 2019-12-02 1600"));
    }

    @Test
    public void parseEventArgs_missingToMarker_throws() {
        assertThrows(SelException.class, () ->
            Parser.parseEventArgs("event project meeting /from 2019-12-02 1400"));
    }

    @Test
    public void parseEventArgs_emptyDescription_throws() {
        assertThrows(SelException.class, () ->
            Parser.parseEventArgs("event /from 2019-12-02 1400 /to 2019-12-02 1600"));
    }

    @Test
    public void parseEventArgs_emptyFrom_throws() {
        assertThrows(SelException.class, () ->
            Parser.parseEventArgs("event meeting /from /to 2019-12-02 1600"));
    }

    @Test
    public void parseEventArgs_emptyTo_throws() {
        assertThrows(SelException.class, () ->
            Parser.parseEventArgs("event meeting /from 2019-12-02 1400 /to"));
    }

    @Test
    public void parseDateTime_validInput_parsesCorrectly() throws SelException {
        LocalDateTime result = Parser.parseDateTime("2019-12-02 1800");
        assertEquals(LocalDateTime.of(2019, 12, 2, 18, 0), result);
    }

    @Test
    public void parseDateTime_missingTimeComponent_throws() {
        assertThrows(SelException.class, () -> Parser.parseDateTime("2019-12-02"));
    }

    @Test
    public void parseDateTime_wrongDateFormat_throws() {
        assertThrows(SelException.class, () -> Parser.parseDateTime("02-12-2019 1800"));
    }

    @Test
    public void parseDateTime_garbageInput_throws() {
        assertThrows(SelException.class, () -> Parser.parseDateTime("not a date"));
    }

    @Test
    public void parseDateTime_extraWhitespace_isTrimmedAndStillParses() throws SelException {
        LocalDateTime result = Parser.parseDateTime("  2019-12-02 1800  ");
        assertEquals(LocalDateTime.of(2019, 12, 2, 18, 0), result);
    }

    // ---------- normalising messy input ----------

    @Test
    public void normalise_trimsAndCollapsesRunsOfWhitespace() {
        assertEquals("mark 3", Parser.normalise("   mark    3  "));
        assertEquals("todo read book", Parser.normalise("todo\tread   book"));
        assertEquals("", Parser.normalise("     "));
        assertEquals("", Parser.normalise(null));
    }

    @Test
    public void parseCommandType_surroundingAndRepeatedSpaces_stillRecognised() {
        assertEquals(CommandType.MARK, Parser.parseCommandType("   mark    3   "));
        assertEquals(CommandType.UNKNOWN, Parser.parseCommandType("   "));
        assertEquals(CommandType.UNKNOWN, Parser.parseCommandType(null));
    }

    @Test
    public void parseIndex_leadingAndTrailingSpaces_areIgnored() throws SelException {
        assertEquals(2, Parser.parseIndex("  mark   3  ", "mark", "missing", "invalid"));
    }

    @Test
    public void parseSimpleArgument_repeatedInnerSpaces_areCollapsed() throws SelException {
        assertEquals("read book",
            Parser.parseSimpleArgument("todo   read    book", "todo", "error"));
    }

    // ---------- commands that take no arguments ----------

    @Test
    public void requireNoArguments_bareCommand_passes() throws SelException {
        Parser.requireNoArguments("  list  ", "list");
        Parser.requireNoArguments("bye", "bye");
    }

    @Test
    public void requireNoArguments_extraText_throws() {
        assertThrows(SelException.class, () -> Parser.requireNoArguments("list all", "list"));
        assertThrows(SelException.class, () -> Parser.requireNoArguments("bye now", "bye"));
    }

    // ---------- task numbers ----------

    @Test
    public void parseIndex_twoNumbers_throws() {
        assertThrows(SelException.class, () ->
            Parser.parseIndex("mark 1 2", "mark", "missing", "invalid"));
    }

    @Test
    public void parseIndex_negativeOrDecimalNumber_throwsWithInvalidNumberMessage() {
        SelException negative = assertThrows(SelException.class, () ->
            Parser.parseIndex("mark -1", "mark", "missing", "invalid number"));
        assertEquals("invalid number", negative.getMessage());

        SelException decimal = assertThrows(SelException.class, () ->
            Parser.parseIndex("mark 2.5", "mark", "missing", "invalid number"));
        assertEquals("invalid number", decimal.getMessage());
    }

    @Test
    public void parseIndex_numberTooLargeForAnInt_throwsWithoutCrashing() {
        assertThrows(SelException.class, () ->
            Parser.parseIndex("mark 99999999999999999999", "mark", "missing", "invalid"));
    }

    // ---------- reserved characters ----------

    @Test
    public void parseDescription_containingSaveFileSeparator_throws() {
        assertThrows(SelException.class, () ->
            Parser.parseDescription("todo tea | coffee", "todo", "error"));
    }

    @Test
    public void parseDeadlineArgs_separatorInDescription_throws() {
        assertThrows(SelException.class, () ->
            Parser.parseDeadlineArgs("deadline a | b /by 2019-12-02 1800"));
    }

    // ---------- markers given twice, missing, or out of order ----------

    @Test
    public void parseDeadlineArgs_byGivenTwice_throws() {
        assertThrows(SelException.class, () ->
            Parser.parseDeadlineArgs("deadline x /by 2019-12-02 1800 /by 2019-12-03 1800"));
    }

    @Test
    public void parseDeadlineArgs_eventMarkersUsedInstead_explainsTheMixUp() {
        SelException e = assertThrows(SelException.class, () ->
            Parser.parseDeadlineArgs("deadline x /from 2019-12-02 1800"));
        assertTrue(e.getMessage().contains("/by"));
    }

    @Test
    public void parseEventArgs_fromOrToGivenTwice_throws() {
        assertThrows(SelException.class, () -> Parser.parseEventArgs(
            "event x /from 2019-12-02 1400 /from 2019-12-02 1500 /to 2019-12-02 1600"));
        assertThrows(SelException.class, () -> Parser.parseEventArgs(
            "event x /from 2019-12-02 1400 /to 2019-12-02 1600 /to 2019-12-02 1700"));
    }

    @Test
    public void parseEventArgs_markersOutOfOrder_throws() {
        assertThrows(SelException.class, () -> Parser.parseEventArgs(
            "event x /to 2019-12-02 1600 /from 2019-12-02 1400"));
    }

    @Test
    public void parseEventArgs_deadlineMarkerUsedInstead_explainsTheMixUp() {
        SelException e = assertThrows(SelException.class, () ->
            Parser.parseEventArgs("event x /by 2019-12-02 1400"));
        assertTrue(e.getMessage().contains("/from"));
    }

    @Test
    public void parseArgs_markerMustBeAWholeWord() throws SelException {
        // "/tomorrow" starts with "/to" but is not the /to marker, and a
        // description may legitimately contain a slash.
        String[] event = Parser.parseEventArgs(
            "event and/or picnic /from 2019-12-02 1400 /to 2019-12-02 1600");
        assertEquals("and/or picnic", event[0]);

        assertThrows(SelException.class, () ->
            Parser.parseEventArgs("event trip /from 2019-12-02 1400 /tomorrow"));
    }

    // ---------- dates that do not exist ----------

    @Test
    public void parseDateTime_nonExistentDate_throwsInsteadOfSilentlyShifting() {
        // The default SMART resolver would turn these into Feb 28, Apr 30
        // and the next midnight respectively.
        assertThrows(SelException.class, () -> Parser.parseDateTime("2019-02-30 1800"));
        assertThrows(SelException.class, () -> Parser.parseDateTime("2019-04-31 1800"));
        assertThrows(SelException.class, () -> Parser.parseDateTime("2019-12-02 2400"));
    }

    @Test
    public void parseDateTime_feb29_acceptedOnlyInALeapYear() throws SelException {
        assertEquals(LocalDateTime.of(2020, 2, 29, 18, 0),
            Parser.parseDateTime("2020-02-29 1800"));
        assertThrows(SelException.class, () -> Parser.parseDateTime("2019-02-29 1800"));
    }

    @Test
    public void parseDateTime_impossibleMonthDayOrTime_throws() {
        assertThrows(SelException.class, () -> Parser.parseDateTime("2019-13-01 1800"));
        assertThrows(SelException.class, () -> Parser.parseDateTime("2019-00-10 1800"));
        assertThrows(SelException.class, () -> Parser.parseDateTime("2019-12-00 1800"));
        assertThrows(SelException.class, () -> Parser.parseDateTime("2019-12-02 1860"));
    }

    @Test
    public void parseDateTime_blankInput_throws() {
        assertThrows(SelException.class, () -> Parser.parseDateTime("   "));
        assertThrows(SelException.class, () -> Parser.parseDateTime(null));
    }

    @Test
    public void parseDateTime_repeatedSpacesBetweenDateAndTime_stillParses() throws SelException {
        assertEquals(LocalDateTime.of(2019, 12, 2, 18, 0),
            Parser.parseDateTime("2019-12-02    1800"));
    }

    @Test
    public void parseEventArgs_bareCommandWord_throws() {
        assertThrows(SelException.class, () -> Parser.parseEventArgs("event"));
    }

    @Test
    public void parseMarkers_notPrecededBySpace_areNotTreatedAsMarkers() {
        // "pay/by" is one word, so the /by in it is part of the description
        // rather than the marker that introduces the due date.
        assertThrows(SelException.class, () ->
            Parser.parseDeadlineArgs("deadline pay/by 2019-12-02 1800"));
        assertThrows(SelException.class, () ->
            Parser.parseEventArgs("event trip/from 2019-12-02 1400 /to 2019-12-02 1600"));
    }

    @Test
    public void parseDeadlineArgs_onlyToMarkerUsed_explainsTheMixUp() {
        SelException e = assertThrows(SelException.class, () ->
            Parser.parseDeadlineArgs("deadline x /to 2019-12-02 1800"));
        assertTrue(e.getMessage().contains("/by"));
    }

    @Test
    public void parseIndex_commandWordNotAtTheFront_stillReportsRatherThanCrashing() {
        // Defensive: parseCommandType matches the command word before these
        // methods are called, so this should not happen. It must report an
        // error rather than throw StringIndexOutOfBoundsException.
        assertThrows(SelException.class, () ->
            Parser.parseIndex("oops 3", "mark", "missing", "invalid"));
    }

    @Test
    public void parseDescription_validDescription_isReturnedUnchanged() throws SelException {
        assertEquals("read book", Parser.parseDescription("todo read book", "todo", "error"));
    }

    @Test
    public void parseDescription_missingDescription_throwsWithTheGivenMessage() {
        SelException e = assertThrows(SelException.class, () ->
            Parser.parseDescription("todo", "todo", "empty task error"));
        assertEquals("empty task error", e.getMessage());
    }
}
