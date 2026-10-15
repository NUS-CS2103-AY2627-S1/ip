package james.parser;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import james.command.Command;
import james.exception.UserInputException;
import james.task.Deadline;
import james.task.Event;
import james.task.Task;
import james.task.ToDo;

/**
 * Unit tests for {@link Parser}.
 */
public class ParserTest {

    @Test
    public void extractArguments_commandWithArguments_returnsArgumentText() throws UserInputException {
        String[] parts = Parser.parseCommand("todo read book");
        assertEquals("read book", Parser.extractArguments(parts));
    }

    @Test
    public void extractArguments_commandWithoutArguments_returnsNull() throws UserInputException {
        String[] parts = Parser.parseCommand("list");
        assertNull(Parser.extractArguments(parts));
    }

    // ==========================================
    // parseCommand Tests
    // ==========================================

    /**
     * Tests that parsing a single-word command returns a single-element array containing the command word.
     */
    @Test
    public void parseCommand_singleWord_returnsSingleElementArray() throws UserInputException {
        String[] result = Parser.parseCommand("list");
        assertArrayEquals(new String[]{"list"}, result);
    }

    /**
     * Tests that parsing a command with arguments splits the command word from the trailing argument string.
     */
    @Test
    public void parseCommand_commandWithArguments_returnsSplitArray() throws UserInputException {
        String[] result = Parser.parseCommand("todo read book");
        assertArrayEquals(new String[]{"todo", "read book"}, result);
    }

    /**
     * Tests that leading and trailing whitespace is trimmed when parsing a command.
     */
    @Test
    public void parseCommand_commandWithWhitespace_trimsCorrectly() throws UserInputException {
        String[] result = Parser.parseCommand("   mark 2   ");
        assertArrayEquals(new String[]{"mark", "2"}, result);
    }

    /**
     * Tests that passing null to parseCommand throws UserInputException.
     */
    @Test
    public void parseCommand_nullString_throwsUserInputException() {
        assertThrows(UserInputException.class, () -> Parser.parseCommand(null));
    }

    /**
     * Tests that passing an empty string to parseCommand throws UserInputException.
     */
    @Test
    public void parseCommand_emptyString_throwsUserInputException() {
        assertThrows(UserInputException.class, () -> Parser.parseCommand(""));
    }

    /**
     * Tests that passing whitespace-only string to parseCommand throws UserInputException.
     */
    @Test
    public void parseCommand_whitespaceOnly_throwsUserInputException() {
        assertThrows(UserInputException.class, () -> Parser.parseCommand("   "));
    }

    // ==========================================
    // parseCommandType Tests
    // ==========================================

    /**
     * Tests that lowercase valid command strings are parsed to their respective Command enums.
     */
    @Test
    public void parseCommandType_validLowerCase_returnsCommand() throws UserInputException {
        assertEquals(Command.TODO, Parser.parseCommandType("todo"));
        assertEquals(Command.DEADLINE, Parser.parseCommandType("deadline"));
        assertEquals(Command.EVENT, Parser.parseCommandType("event"));
        assertEquals(Command.MARK, Parser.parseCommandType("mark"));
        assertEquals(Command.UNMARK, Parser.parseCommandType("unmark"));
        assertEquals(Command.DELETE, Parser.parseCommandType("delete"));
        assertEquals(Command.LIST, Parser.parseCommandType("list"));
        assertEquals(Command.BYE, Parser.parseCommandType("bye"));
        assertEquals(Command.LIST_BY_DATE, Parser.parseCommandType("list_by_date"));
        assertEquals(Command.FIND, Parser.parseCommandType("find"));
    }

    /**
     * Tests that a valid find keyword is trimmed before use.
     */
    @Test
    public void parseFindKeyword_validKeyword_returnsTrimmedKeyword() throws UserInputException {
        assertEquals("book", Parser.parseFindKeyword("  book  "));
    }

    /**
     * Tests that a missing find keyword is rejected.
     */
    @Test
    public void parseFindKeyword_missingKeyword_throwsUserInputException() {
        assertThrows(UserInputException.class, () -> Parser.parseFindKeyword(" "));
    }

    /**
     * Tests that uppercase valid command strings are parsed to their respective Command enums.
     */
    @Test
    public void parseCommandType_validUpperCase_returnsCommand() throws UserInputException {
        assertEquals(Command.TODO, Parser.parseCommandType("TODO"));
    }

    /**
     * Tests that unrecognized command strings throw UserInputException.
     */
    @Test
    public void parseCommandType_invalidCommand_throwsUserInputException() {
        assertThrows(UserInputException.class, () -> Parser.parseCommandType("unknown"));
    }

    // ==========================================
    // parseTaskNumber Tests
    // ==========================================

    /**
     * Tests that a valid 1-based task number string is converted to the correct 0-based index.
     */
    @Test
    public void parseTaskNumber_validNumber_returnsZeroBasedIndex() throws UserInputException {
        assertEquals(0, Parser.parseTaskNumber("1", "mark", 3));
        assertEquals(2, Parser.parseTaskNumber("3", "delete", 3));
    }

    /**
     * Tests that passing a null argument string to parseTaskNumber throws UserInputException.
     */
    @Test
    public void parseTaskNumber_nullArgument_throwsUserInputException() {
        assertThrows(UserInputException.class, () -> Parser.parseTaskNumber(null, "mark", 3));
    }

    /**
     * Tests that passing an empty or whitespace argument string to parseTaskNumber throws UserInputException.
     */
    @Test
    public void parseTaskNumber_emptyArgument_throwsUserInputException() {
        assertThrows(UserInputException.class, () -> Parser.parseTaskNumber("   ", "mark", 3));
    }

    /**
     * Tests that passing non-numeric text to parseTaskNumber throws UserInputException.
     */
    @Test
    public void parseTaskNumber_nonNumeric_throwsUserInputException() {
        assertThrows(UserInputException.class, () -> Parser.parseTaskNumber("abc", "mark", 3));
    }

    /**
     * Tests that passing a task number of 0 to parseTaskNumber throws UserInputException.
     */
    @Test
    public void parseTaskNumber_zeroNumber_throwsUserInputException() {
        assertThrows(UserInputException.class, () -> Parser.parseTaskNumber("0", "mark", 3));
    }

    /**
     * Tests that passing a negative task number to parseTaskNumber throws UserInputException.
     */
    @Test
    public void parseTaskNumber_negativeNumber_throwsUserInputException() {
        assertThrows(UserInputException.class, () -> Parser.parseTaskNumber("-1", "mark", 3));
    }

    /**
     * Tests that passing a task number exceeding the current list size throws UserInputException.
     */
    @Test
    public void parseTaskNumber_exceedsListSize_throwsUserInputException() {
        assertThrows(UserInputException.class, () -> Parser.parseTaskNumber("4", "delete", 3));
    }

    // ==========================================
    // parseDate Tests
    // ==========================================

    /**
     * Tests that a valid yyyy-mm-dd date string is correctly parsed into a LocalDate object.
     */
    @Test
    public void parseDate_validDateString_returnsParsedDate() throws UserInputException {
        LocalDate expected = LocalDate.of(2026, 5, 20);
        assertEquals(expected, Parser.parseDate("2026-05-20"));
    }

    /**
     * Tests that passing a null date string throws UserInputException.
     */
    @Test
    public void parseDate_nullString_throwsUserInputException() {
        assertThrows(UserInputException.class, () -> Parser.parseDate(null));
    }

    /**
     * Tests that passing an empty or whitespace date string throws UserInputException.
     */
    @Test
    public void parseDate_emptyString_throwsUserInputException() {
        assertThrows(UserInputException.class, () -> Parser.parseDate("   "));
    }

    /**
     * Tests that passing an invalid date format throws UserInputException.
     */
    @Test
    public void parseDate_invalidFormat_throwsUserInputException() {
        assertThrows(UserInputException.class, () -> Parser.parseDate("20-05-2026"));
    }

    /**
     * Tests that passing a non-existent date throws UserInputException.
     */
    @Test
    public void parseDate_nonExistentDate_throwsUserInputException() {
        assertThrows(UserInputException.class, () -> Parser.parseDate("2026-02-30"));
    }

    // ==========================================
    // parseTodo Tests
    // ==========================================

    /**
     * Tests that a valid description string creates a ToDo instance with matching description.
     */
    @Test
    public void parseTodo_validDescription_returnsToDoTask() throws UserInputException {
        Task task = Parser.parseTodo("read novel");
        assertInstanceOf(ToDo.class, task);
        assertEquals("read novel", task.getDescription());
    }

    /**
     * Tests that passing a null description to parseTodo throws UserInputException.
     */
    @Test
    public void parseTodo_nullArgument_throwsUserInputException() {
        assertThrows(UserInputException.class, () -> Parser.parseTodo(null));
    }

    /**
     * Tests that passing an empty or whitespace description to parseTodo throws UserInputException.
     */
    @Test
    public void parseTodo_emptyArgument_throwsUserInputException() {
        assertThrows(UserInputException.class, () -> Parser.parseTodo("   "));
    }

    // ==========================================
    // parseDeadline Tests
    // ==========================================

    /**
     * Tests that valid description and date arguments create a Deadline instance with matching fields.
     */
    @Test
    public void parseDeadline_validArguments_returnsDeadlineTask() throws UserInputException {
        Task task = Parser.parseDeadline("submit assignment /by 2026-10-15");
        assertInstanceOf(Deadline.class, task);
        Deadline deadline = (Deadline) task;
        assertEquals("submit assignment", deadline.getDescription());
        assertEquals(LocalDate.of(2026, 10, 15), deadline.getBy());
    }

    /**
     * Tests that passing a null argument to parseDeadline throws UserInputException.
     */
    @Test
    public void parseDeadline_nullArgument_throwsUserInputException() {
        assertThrows(UserInputException.class, () -> Parser.parseDeadline(null));
    }

    /**
     * Tests that missing the /by keyword in parseDeadline throws UserInputException.
     */
    @Test
    public void parseDeadline_missingByKeyword_throwsUserInputException() {
        assertThrows(UserInputException.class, () -> Parser.parseDeadline("submit assignment 2026-10-15"));
    }

    /**
     * Tests that providing an empty description in parseDeadline throws UserInputException.
     */
    @Test
    public void parseDeadline_emptyDescription_throwsUserInputException() {
        assertThrows(UserInputException.class, () -> Parser.parseDeadline("/by 2026-10-15"));
    }

    /**
     * Tests that providing an empty date in parseDeadline throws UserInputException.
     */
    @Test
    public void parseDeadline_emptyDate_throwsUserInputException() {
        assertThrows(UserInputException.class, () -> Parser.parseDeadline("submit assignment /by "));
    }

    /**
     * Tests that providing an invalid date format in parseDeadline throws UserInputException.
     */
    @Test
    public void parseDeadline_invalidDateFormat_throwsUserInputException() {
        assertThrows(UserInputException.class, () -> Parser.parseDeadline("submit assignment /by tomorrow"));
    }

    // ==========================================
    // parseEvent Tests
    // ==========================================

    /**
     * Tests that valid description and start/end dates create an Event instance with matching fields.
     */
    @Test
    public void parseEvent_validArguments_returnsEventTask() throws UserInputException {
        Task task = Parser.parseEvent("hackathon /from 2026-11-01 /to 2026-11-03");
        assertInstanceOf(Event.class, task);
        Event event = (Event) task;
        assertEquals("hackathon", event.getDescription());
        assertEquals(LocalDate.of(2026, 11, 1), event.getFrom());
        assertEquals(LocalDate.of(2026, 11, 3), event.getTo());
    }

    /**
     * Tests that passing a null argument to parseEvent throws UserInputException.
     */
    @Test
    public void parseEvent_nullArgument_throwsUserInputException() {
        assertThrows(UserInputException.class, () -> Parser.parseEvent(null));
    }

    /**
     * Tests that missing the /from keyword in parseEvent throws UserInputException.
     */
    @Test
    public void parseEvent_missingFromKeyword_throwsUserInputException() {
        assertThrows(UserInputException.class, () -> Parser.parseEvent("hackathon /to 2026-11-03"));
    }

    /**
     * Tests that missing the /to keyword in parseEvent throws UserInputException.
     */
    @Test
    public void parseEvent_missingToKeyword_throwsUserInputException() {
        assertThrows(UserInputException.class, () -> Parser.parseEvent("hackathon /from 2026-11-01"));
    }

    /**
     * Tests that providing an empty description in parseEvent throws UserInputException.
     */
    @Test
    public void parseEvent_emptyDescription_throwsUserInputException() {
        assertThrows(UserInputException.class, () -> Parser.parseEvent("/from 2026-11-01 /to 2026-11-03"));
    }

    /**
     * Tests that providing empty start or end dates in parseEvent throws UserInputException.
     */
    @Test
    public void parseEvent_emptyDates_throwsUserInputException() {
        assertThrows(UserInputException.class, () -> Parser.parseEvent("hackathon /from /to "));
    }

    /**
     * Tests that providing unparseable date strings in parseEvent throws UserInputException.
     */
    @Test
    public void parseEvent_invalidDates_throwsUserInputException() {
        assertThrows(UserInputException.class, () -> Parser.parseEvent("hackathon /from today /to tomorrow"));
    }
}
