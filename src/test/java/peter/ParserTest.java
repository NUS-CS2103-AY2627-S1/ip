package peter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import org.junit.jupiter.api.Test;

/**
 * Tests for {@link Parser}.
 */
public class ParserTest {

    @Test
    public void parseInput_commandWithArguments_splitsKeywordAndArguments() throws PeterException {
        Parser.ParsedInput parsed = Parser.parseInput("deadline return book /by 2019-12-02 1800");

        assertEquals(Command.DEADLINE, parsed.getCommand());
        assertEquals("return book /by 2019-12-02 1800", parsed.getArguments());
    }

    @Test
    public void parseInput_commandWithNoArguments_returnsEmptyArguments() throws PeterException {
        Parser.ParsedInput parsed = Parser.parseInput("list");

        assertEquals(Command.LIST, parsed.getCommand());
        assertEquals("", parsed.getArguments());
    }

    @Test
    public void parseInput_unrecognisedKeyword_returnsUnknownCommand() throws PeterException {
        Parser.ParsedInput parsed = Parser.parseInput("fly to the moon");

        assertEquals(Command.UNKNOWN, parsed.getCommand());
    }

    @Test
    public void parseInput_emptyString_throwsPeterException() {
        assertThrows(PeterException.class, () -> Parser.parseInput(""));
    }

    @Test
    public void parseInput_wholeInputIsWhitespace_throwsPeterException() {
        assertThrows(PeterException.class, () -> Parser.parseInput("     "));
    }

    @Test
    public void parseInput_leadingAndTrailingWhitespace_stillRecognisesCommand() throws PeterException {
        // A leading space used to make the keyword parse as an empty string,
        // which meant the whole line was treated as an unrecognised command.
        Parser.ParsedInput parsed = Parser.parseInput("  todo read book  ");

        assertEquals(Command.TODO, parsed.getCommand());
        assertEquals("read book", parsed.getArguments());
    }

    @Test
    public void parseInput_multipleSpacesBetweenKeywordAndArguments_trimsArguments() throws PeterException {
        Parser.ParsedInput parsed = Parser.parseInput("todo    read book");

        assertEquals(Command.TODO, parsed.getCommand());
        assertEquals("read book", parsed.getArguments());
    }

    @Test
    public void parseTodo_nonEmptyDescription_createsTodoWithThatDescription() throws PeterException {
        Todo todo = Parser.parseTodo("read book");
        assertEquals("read book", todo.getDescription());
    }

    @Test
    public void parseTodo_emptyDescription_throwsPeterException() {
        assertThrows(PeterException.class, () -> Parser.parseTodo(""));
    }

    @Test
    public void parseTodo_descriptionContainsPipe_throwsPeterException() {
        // '|' is the storage file's field delimiter; a description containing one would
        // silently corrupt the saved line and lose the task on the next load.
        assertThrows(PeterException.class, () -> Parser.parseTodo("buy milk | bread"));
    }

    @Test
    public void parseDeadline_dateOnly_defaultsTimeToMidnight() throws PeterException {
        Deadline deadline = Parser.parseDeadline("return book /by 2019-12-02");

        assertEquals("return book", deadline.getDescription());
        assertEquals(LocalDateTime.of(2019, 12, 2, 0, 0), deadline.getBy());
    }

    @Test
    public void parseDeadline_dateAndTime_parsesExactTime() throws PeterException {
        Deadline deadline = Parser.parseDeadline("return book /by 2019-12-02 1800");

        assertEquals(LocalDateTime.of(2019, 12, 2, 18, 0), deadline.getBy());
    }

    @Test
    public void parseDeadline_missingByKeyword_throwsPeterException() {
        assertThrows(PeterException.class, () -> Parser.parseDeadline("return book"));
    }

    @Test
    public void parseDeadline_emptyDescription_throwsPeterException() {
        assertThrows(PeterException.class, () -> Parser.parseDeadline("/by 2019-12-02"));
    }

    @Test
    public void parseDeadline_malformedDate_throwsPeterException() {
        assertThrows(PeterException.class, () -> Parser.parseDeadline("return book /by not-a-date"));
    }

    @Test
    public void parseDeadline_nonExistentCalendarDate_throwsPeterException() {
        // February never has a 30th; the default date-time resolver would otherwise
        // silently roll this over to Feb 28 instead of rejecting it.
        assertThrows(PeterException.class, () -> Parser.parseDeadline("return book /by 2019-02-30"));
    }

    @Test
    public void parseDeadline_duplicateByMarker_throwsPeterException() {
        assertThrows(PeterException.class, () -> Parser.parseDeadline("return book /by 2019-12-02 /by 2019-12-03"));
    }

    @Test
    public void parseDeadline_descriptionContainsPipe_throwsPeterException() {
        assertThrows(PeterException.class, () -> Parser.parseDeadline("return book | urgent /by 2019-12-02"));
    }

    @Test
    public void parseDeadline_extraSpacesAroundDateTime_stillParses() throws PeterException {
        Deadline deadline = Parser.parseDeadline("return book /by  2019-12-02   1800");

        assertEquals(LocalDateTime.of(2019, 12, 2, 18, 0), deadline.getBy());
    }

    @Test
    public void parseEvent_validFromAndTo_parsesDescriptionAndBothDates() throws PeterException {
        Event event = Parser.parseEvent(
                "project meeting /from 2019-12-02 1400 /to 2019-12-02 1600");

        assertEquals("project meeting", event.getDescription());
        assertEquals(LocalDateTime.of(2019, 12, 2, 14, 0), event.getFrom());
        // 'to' has no public getter, so verify it via the serialized storage line
        assertEquals("E | 0 | project meeting | 2019-12-02T1400 | 2019-12-02T1600",
                event.toFileFormat());
    }

    @Test
    public void parseEvent_missingFromKeyword_throwsPeterException() {
        assertThrows(PeterException.class, () -> Parser.parseEvent("project meeting /to 2019-12-02 1600"));
    }

    @Test
    public void parseEvent_missingToKeyword_throwsPeterException() {
        assertThrows(PeterException.class, () -> Parser.parseEvent("project meeting /from 2019-12-02 1400"));
    }

    @Test
    public void parseEvent_emptyToValue_throwsPeterException() {
        assertThrows(PeterException.class, () -> Parser.parseEvent("project meeting /from 2019-12-02 1400 /to"));
    }

    @Test
    public void parseEvent_duplicateFromMarker_throwsPeterException() {
        assertThrows(PeterException.class, () -> Parser.parseEvent(
                "project meeting /from 2019-12-02 1400 /from 2019-12-02 1500 /to 2019-12-02 1600"));
    }

    @Test
    public void parseEvent_duplicateToMarker_throwsPeterException() {
        assertThrows(PeterException.class, () -> Parser.parseEvent(
                "project meeting /from 2019-12-02 1400 /to 2019-12-02 1600 /to 2019-12-02 1700"));
    }

    @Test
    public void parseEvent_endBeforeStart_throwsPeterException() {
        assertThrows(PeterException.class, () -> Parser.parseEvent(
                "project meeting /from 2019-12-02 1600 /to 2019-12-02 1400"));
    }

    @Test
    public void parseEvent_endEqualsStart_throwsPeterException() {
        assertThrows(PeterException.class, () -> Parser.parseEvent(
                "project meeting /from 2019-12-02 1400 /to 2019-12-02 1400"));
    }

    @Test
    public void parseEvent_descriptionContainsPipe_throwsPeterException() {
        assertThrows(PeterException.class, () -> Parser.parseEvent(
                "meeting | urgent /from 2019-12-02 1400 /to 2019-12-02 1600"));
    }

    @Test
    public void parseEvent_extraSpacesAroundDateTimes_stillParses() throws PeterException {
        Event event = Parser.parseEvent(
                "project meeting /from  2019-12-02   1400 /to  2019-12-02   1600");

        assertEquals(LocalDateTime.of(2019, 12, 2, 14, 0), event.getFrom());
    }

    @Test
    public void parseTaskIndex_validNumber_returnsZeroBasedIndex() throws PeterException {
        assertEquals(0, Parser.parseTaskIndex("1"));
        assertEquals(4, Parser.parseTaskIndex("5"));
    }

    @Test
    public void parseTaskIndex_nonNumericInput_throwsPeterException() {
        assertThrows(PeterException.class, () -> Parser.parseTaskIndex("abc"));
    }

    @Test
    public void parseTaskIndex_emptyInput_throwsPeterException() {
        assertThrows(PeterException.class, () -> Parser.parseTaskIndex(""));
    }

    @Test
    public void parseDate_validFormat_parsesCorrectDate() throws PeterException {
        LocalDate date = Parser.parseDate("2019-12-02");
        assertEquals(LocalDate.of(2019, 12, 2), date);
    }

    @Test
    public void parseDate_invalidFormat_throwsPeterException() {
        assertThrows(PeterException.class, () -> Parser.parseDate("02/12/2019"));
    }

    @Test
    public void parseDate_nonExistentCalendarDate_throwsPeterException() {
        assertThrows(PeterException.class, () -> Parser.parseDate("2019-02-30"));
    }

    @Test
    public void parseDate_extraSpaces_stillParses() throws PeterException {
        LocalDate date = Parser.parseDate("  2019-12-02  ");
        assertEquals(LocalDate.of(2019, 12, 2), date);
    }

    @Test
    public void parseDeadline_dateOnly_resultingTimeIsMidnight() throws PeterException {
        Deadline deadline = Parser.parseDeadline("return book /by 2019-12-02");
        assertEquals(LocalTime.MIDNIGHT, deadline.getBy().toLocalTime());
    }
}
