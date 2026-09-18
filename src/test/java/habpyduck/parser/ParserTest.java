package habpyduck.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import habpyduck.HabpyDuckException;
import habpyduck.command.AddCommand;
import habpyduck.command.Command;
import habpyduck.command.DeleteCommand;
import habpyduck.command.ExitCommand;
import habpyduck.command.FindCommand;
import habpyduck.command.FindTagCommand;
import habpyduck.command.ListCommand;
import habpyduck.command.MarkCommand;
import habpyduck.command.TagCommand;
import habpyduck.command.UnmarkCommand;
import habpyduck.command.UntagCommand;

/**
 * Tests how raw user input is parsed into command types, command objects, dates,
 * and task indexes.
 */
public class ParserTest {
    private static final String UNKNOWN_COMMAND_MESSAGE = "OH NO!!! I don't understand that command friend :(. "
            + "Try todo, deadline, event, list, mark, unmark, delete, find, tag, untag, or findtag!";
    private static final String INVALID_TAG_MESSAGE = "OH NO!!! Tags must start with # and use only letters, "
            + "numbers, hyphens, or underscores. Try something like: tag 2 #fun";
    private static final String INVALID_DEADLINE_DATE_MESSAGE = "OH NO!!! Please enter the deadline date in "
            + "DD/MM/YYYY format, or include time like: 25/8/2026 1800";

    private final Parser parser = new Parser();

    @Test
    public void getCommandType_knownCommands_returnsMatchingCommandTypes() {
        assertEquals(CommandType.LIST, parser.getCommandType("list"));
        assertEquals(CommandType.MARK, parser.getCommandType("mark 1"));
        assertEquals(CommandType.UNMARK, parser.getCommandType("unmark 1"));
        assertEquals(CommandType.DELETE, parser.getCommandType("delete 1"));
        assertEquals(CommandType.FIND, parser.getCommandType("find book"));
        assertEquals(CommandType.FINDTAG, parser.getCommandType("findtag #fun"));
        assertEquals(CommandType.TAG, parser.getCommandType("tag 1 #fun"));
        assertEquals(CommandType.UNTAG, parser.getCommandType("untag 1 #fun"));
        assertEquals(CommandType.TODO, parser.getCommandType("todo read book"));
        assertEquals(CommandType.DEADLINE, parser.getCommandType("deadline return book /by 25/8/2026 1800"));
        assertEquals(CommandType.EVENT, parser.getCommandType("event meeting /from 25/8/2026 1400 /to 25/8/2026 1600"));
        assertEquals(CommandType.BYE, parser.getCommandType("bye"));
    }

    @Test
    public void getCommandType_commandWithExtraSpaces_returnsMatchingCommandType() {
        assertEquals(CommandType.TODO, parser.getCommandType("   todo read book   "));
    }

    @Test
    public void getCommandType_unknownOrBlankCommand_returnsUnknownCommandType() {
        assertEquals(CommandType.UNKNOWN, parser.getCommandType("hello"));
        assertEquals(CommandType.UNKNOWN, parser.getCommandType("   "));
    }

    @Test
    public void parse_validNonAddCommands_returnsMatchingCommandClasses() throws HabpyDuckException {
        assertInstanceOf(ListCommand.class, parser.parse("list"));
        assertInstanceOf(MarkCommand.class, parser.parse("mark 1"));
        assertInstanceOf(UnmarkCommand.class, parser.parse("unmark 1"));
        assertInstanceOf(DeleteCommand.class, parser.parse("delete 1"));
        assertInstanceOf(FindCommand.class, parser.parse("find book"));
        assertInstanceOf(FindTagCommand.class, parser.parse("findtag #fun"));
        assertInstanceOf(TagCommand.class, parser.parse("tag 1 #fun #school"));
        assertInstanceOf(UntagCommand.class, parser.parse("untag 1 #fun"));
        assertInstanceOf(ExitCommand.class, parser.parse("bye"));
    }

    @Test
    public void parse_validAddCommands_returnsAddCommand() throws HabpyDuckException {
        assertInstanceOf(AddCommand.class, parser.parse("todo read book"));
        assertInstanceOf(AddCommand.class, parser.parse("deadline return book /by 25/8/2026"));
        assertInstanceOf(AddCommand.class, parser.parse("deadline return book /by 25/8/2026 1800"));
        assertInstanceOf(AddCommand.class,
                parser.parse("event meeting /from 25/8/2026 1400 /to 25/8/2026 1600"));
    }

    @Test
    public void parse_addCommandsWithExtraSpacesAroundMarkers_returnsAddCommand() throws HabpyDuckException {
        assertInstanceOf(AddCommand.class, parser.parse("deadline return book    /by    25/8/2026 1800"));
        assertInstanceOf(AddCommand.class,
                parser.parse("event meeting    /from    25/8/2026 1400    /to    25/8/2026 1600"));
    }

    @Test
    public void parse_byeCommand_returnsExitCommandThatExits() throws HabpyDuckException {
        Command command = parser.parse("bye");

        assertInstanceOf(ExitCommand.class, command);
        assertTrue(command.isExit());
    }

    @Test
    public void parse_blankCommand_exceptionThrown() {
        assertParseExceptionMessage("   ",
                "OH NO!!! I didn't catch a command, friend. Please type something for me.");
    }

    @Test
    public void parse_unknownCommand_exceptionThrown() {
        assertParseExceptionMessage("hello", UNKNOWN_COMMAND_MESSAGE);
    }

    @Test
    public void parse_findWithoutKeyword_exceptionThrown() {
        assertParseExceptionMessage("find",
                "OH NO!!! Please tell me what keyword to find, like: find book");
    }

    @Test
    public void parse_findTagWithoutTag_exceptionThrown() {
        assertParseExceptionMessage("findtag",
                "OH NO!!! Please tell me which tag to use, like: findtag #fun");
    }

    @Test
    public void parse_tagWithoutEnoughDetails_exceptionThrown() {
        assertParseExceptionMessage("tag 1",
                "OH NO!!! Please use this format for tags: tag TASK_NUMBER #TAG...");
    }

    @Test
    public void parse_tagWithInvalidTaskNumber_exceptionThrown() {
        assertParseExceptionMessage("tag abc #fun",
                "OH NO!!! Please use a number after tag, like: tag 2");
    }

    @Test
    public void parse_tagWithoutHash_exceptionThrown() {
        assertParseExceptionMessage("tag 1 fun", INVALID_TAG_MESSAGE);
    }

    @Test
    public void parse_tagWithInvalidCharacters_exceptionThrown() {
        assertParseExceptionMessage("tag 1 #fun!",
                "OH NO!!! Tags must start with # and use only letters, numbers, "
                        + "hyphens, or underscores. Try something like: tag 2 #fun");
    }

    @Test
    public void parse_untagWithoutEnoughDetails_exceptionThrown() {
        assertParseExceptionMessage("untag 1",
                "OH NO!!! Please use this format for untagging: untag TASK_NUMBER #TAG");
    }

    @Test
    public void parse_untagWithTooManyTags_exceptionThrown() {
        assertParseExceptionMessage("untag 1 #fun #school",
                "OH NO!!! Please use this format for untagging: untag TASK_NUMBER #TAG");
    }

    @Test
    public void parse_todoWithoutDescription_exceptionThrown() {
        assertParseExceptionMessage("todo",
                "OH NO!!! A todo needs a description, friend. Try something like: todo read book");
    }

    @Test
    public void parse_deadlineWithoutByMarker_exceptionThrown() {
        assertParseExceptionMessage("deadline return book",
                "OH NO!!! Please use this format for deadlines: deadline DESCRIPTION /by DD/MM/YYYY "
                        + "or DD/MM/YYYY HHmm :)");
    }

    @Test
    public void parse_deadlineWithoutDescription_exceptionThrown() {
        assertParseExceptionMessage("deadline  /by 25/8/2026 1800",
                "OH NO!!! A deadline needs a description, friend. Try again!");
    }

    @Test
    public void parse_deadlineWithoutDateTime_exceptionThrown() {
        assertParseExceptionMessage("deadline return book /by ",
                "OH NO!!! A deadline needs a date, friend. Try something like: 25/8/2026");
    }

    @Test
    public void parse_deadlineWithDuplicateByMarkers_exceptionThrown() {
        assertParseExceptionMessage("deadline return book /by 25/8/2026 1800 /by 26/8/2026 1800",
                "OH NO!!! A deadline should have only one /by marker, friend.");
    }

    @Test
    public void parse_deadlineWithInvalidDateTime_exceptionThrown() {
        assertParseExceptionMessage("deadline return book /by 2026-08-25",
                INVALID_DEADLINE_DATE_MESSAGE);
    }

    @Test
    public void parse_eventWithoutRequiredMarkers_exceptionThrown() {
        assertParseExceptionMessage("event meeting /from 2pm",
                "OH NO!!! Please use this format for events: event DESCRIPTION /from START /to END :)");
    }

    @Test
    public void parse_eventWithDuplicateMarkers_exceptionThrown() {
        assertParseExceptionMessage("event meeting /from 2pm /from 3pm /to 4pm",
                "OH NO!!! An event should have one /from marker and one /to marker, friend.");
    }

    @Test
    public void parse_eventWithToBeforeFrom_exceptionThrown() {
        assertParseExceptionMessage("event meeting /to 4pm /from 2pm",
                "OH NO!!! Please put /from before /to for events, friend.");
    }

    @Test
    public void parse_eventWithoutDescription_exceptionThrown() {
        assertParseExceptionMessage("event  /from 2pm /to 4pm",
                "OH NO!!! An event needs a description, friend. Try again!");
    }

    @Test
    public void parse_eventWithoutStart_exceptionThrown() {
        assertParseExceptionMessage("event meeting /from  /to 4pm",
                "OH NO!!! An event needs a start time, friend. Try again!");
    }

    @Test
    public void parse_eventWithoutEnd_exceptionThrown() {
        assertParseExceptionMessage("event meeting /from 2pm /to ",
                "OH NO!!! An event needs an end time, friend. Try again!");
    }

    @Test
    public void parse_eventWithStartBeforeEnd_returnsAddCommand() throws HabpyDuckException {
        assertInstanceOf(AddCommand.class,
                parser.parse("event meeting /from 18/9/2026 1400 /to 18/9/2026 1600"));
    }

    @Test
    public void parse_eventWithDateOnlyStartAndEnd_returnsAddCommand() throws HabpyDuckException {
        assertInstanceOf(AddCommand.class,
                parser.parse("event orientation /from 20/9/2026 /to 25/9/2026"));
    }

    @Test
    public void parse_eventWithMixedDateOnlyAndDateTime_returnsAddCommand() throws HabpyDuckException {
        assertInstanceOf(AddCommand.class,
                parser.parse("event hackathon /from 20/9/2026 0900 /to 21/9/2026"));
    }

    @Test
    public void parse_eventWithStartAfterEnd_exceptionThrown() {
        assertParseExceptionMessage("event meeting /from 18/9/2026 1800 /to 18/9/2026 1600",
                "OH NO!!! An event must end after it starts, friend.");
    }

    @Test
    public void parse_eventWithStartEqualToEnd_exceptionThrown() {
        assertParseExceptionMessage("event meeting /from 18/9/2026 1800 /to 18/9/2026 1800",
                "OH NO!!! An event must end after it starts, friend.");
    }

    @Test
    public void parseUserEventDateTime_validDateTime_returnsLocalDateTime() throws HabpyDuckException {
        LocalDateTime expected = LocalDateTime.of(2026, 8, 25, 18, 0);

        assertEquals(expected, parser.parseUserEventDateTime("25/8/2026 1800"));
    }

    @Test
    public void parseUserEventDateTime_validDateOnly_returnsStartOfDay() throws HabpyDuckException {
        LocalDateTime expected = LocalDateTime.of(2026, 8, 25, 0, 0);

        assertEquals(expected, parser.parseUserEventDateTime("25/8/2026"));
    }

    @Test
    public void parseUserEventDateTime_invalidFormat_exceptionThrown() {
        assertParseExceptionMessage("event meeting /from 20/13/2026 /to 25/8/2026 1800",
                "OH NO!!! Please enter the event date in DD/MM/YYYY format, or include time like: 25/8/2026 1800");
    }

    @Test
    public void parseUserDeadlineDateTime_validDateTime_returnsLocalDateTime() throws HabpyDuckException {
        LocalDateTime expected = LocalDateTime.of(2026, 8, 25, 18, 0);

        assertEquals(expected, parser.parseUserDeadlineDateTime("25/8/2026 1800"));
    }

    @Test
    public void parseUserDeadlineDateTime_validDateOnly_returnsStartOfDay() throws HabpyDuckException {
        LocalDateTime expected = LocalDateTime.of(2026, 8, 25, 0, 0);

        assertEquals(expected, parser.parseUserDeadlineDateTime("25/8/2026"));
    }

    @Test
    public void parseUserDeadlineDateTime_validSingleDigitDateAndTime_returnsLocalDateTime()
            throws HabpyDuckException {
        LocalDateTime expected = LocalDateTime.of(2026, 1, 5, 9, 30);

        assertEquals(expected, parser.parseUserDeadlineDateTime("5/1/2026 0930"));
    }

    @Test
    public void parseUserDeadlineDateTime_invalidFormat_exceptionThrown() {
        HabpyDuckException exception = assertThrows(
                HabpyDuckException.class, () -> parser.parseUserDeadlineDateTime("2026-08-25 1800"));

        assertEquals(INVALID_DEADLINE_DATE_MESSAGE, exception.getMessage());
    }

    @Test
    public void parseUserDeadlineDateTime_blankText_exceptionThrown() {
        HabpyDuckException exception = assertThrows(
                HabpyDuckException.class, () -> parser.parseUserDeadlineDateTime("   "));

        assertEquals(INVALID_DEADLINE_DATE_MESSAGE, exception.getMessage());
    }

    @Test
    public void parseTaskIndex_firstTask_returnsZeroBasedIndex() throws HabpyDuckException {
        assertEquals(0, parser.parseTaskIndex("mark 1", "mark", 3));
    }

    @Test
    public void parseTaskIndex_lastTask_returnsZeroBasedIndex() throws HabpyDuckException {
        assertEquals(2, parser.parseTaskIndex("mark 3", "mark", 3));
    }

    @Test
    public void parseTaskIndex_extraSpaces_returnsZeroBasedIndex() throws HabpyDuckException {
        assertEquals(1, parser.parseTaskIndex("delete    2   ", "delete", 3));
    }

    @Test
    public void parseTaskIndex_missingTaskNumber_exceptionThrown() {
        HabpyDuckException exception = assertThrows(
                HabpyDuckException.class, () -> parser.parseTaskIndex("unmark", "unmark", 3));

        assertEquals("OH NO!!! Please tell me which task to unmark, like: unmark 2",
                exception.getMessage());
    }

    @Test
    public void parseTaskIndex_nonNumericTaskNumber_exceptionThrown() {
        HabpyDuckException exception = assertThrows(
                HabpyDuckException.class, () -> parser.parseTaskIndex("delete abc", "delete", 3));

        assertEquals("OH NO!!! Please use a number after delete, like: delete 2",
                exception.getMessage());
    }

    @Test
    public void parseTaskIndex_zeroTaskNumber_exceptionThrown() {
        HabpyDuckException exception = assertThrows(
                HabpyDuckException.class, () -> parser.parseTaskIndex("mark 0", "mark", 3));

        assertEquals("OH NO!!! Task 0 does not exist in your list.", exception.getMessage());
    }

    @Test
    public void parseTaskIndex_negativeTaskNumber_exceptionThrown() {
        HabpyDuckException exception = assertThrows(
                HabpyDuckException.class, () -> parser.parseTaskIndex("mark -1", "mark", 3));

        assertEquals("OH NO!!! Task -1 does not exist in your list.", exception.getMessage());
    }

    @Test
    public void parseTaskIndex_taskNumberAboveTaskCount_exceptionThrown() {
        HabpyDuckException exception = assertThrows(
                HabpyDuckException.class, () -> parser.parseTaskIndex("mark 4", "mark", 3));

        assertEquals("OH NO!!! Task 4 does not exist in your list.", exception.getMessage());
    }

    private void assertParseExceptionMessage(String command, String expectedMessage) {
        HabpyDuckException exception = assertThrows(HabpyDuckException.class, () -> parser.parse(command));

        assertEquals(expectedMessage, exception.getMessage());
    }
}
