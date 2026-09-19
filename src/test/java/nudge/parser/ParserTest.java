package nudge.parser;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import nudge.exception.NudgeException;
import nudge.task.Deadline;
import nudge.task.Event;
import nudge.task.Todo;

/**
 * Tests the parsing and validation performed by {@link Parser}.
 */
class ParserTest {
    @Test
    void parseCommandType_sortCommandWithSurroundingWhitespace_returnsSort() {
        assertEquals(CommandType.SORT, Parser.parseCommandType("  sort  "));
    }

    @Test
    void parseCommandType_sortWithoutCommandBoundary_returnsUnknown() {
        assertEquals(CommandType.UNKNOWN, Parser.parseCommandType("sortdeadline"));
    }

    @Test
    void parseCommandType_uppercaseSort_returnsUnknown() {
        assertEquals(CommandType.UNKNOWN, Parser.parseCommandType("Sort"));
    }

    @Test
    void validateSortCommand_withoutArguments_doesNotThrow() {
        assertDoesNotThrow(() -> Parser.validateSortCommand("  sort  "));
    }

    @Test
    void validateSortCommand_withArguments_exceptionThrown() {
        NudgeException exception = assertThrows(
                NudgeException.class, () -> Parser.validateSortCommand("sort deadline"));

        assertEquals("`sort` does not take any arguments. Try: sort", exception.getMessage());
    }

    @Test
    void parseTaskIndex_validTaskNumber_returnsZeroBasedIndex() throws NudgeException {
        assertEquals(1, Parser.parseTaskIndex("mark 2", "mark"));
    }

    @Test
    void parseTaskIndex_missingTaskNumber_exceptionThrown() {
        NudgeException exception = assertThrows(NudgeException.class, () -> Parser.parseTaskIndex("mark", "mark"));

        assertEquals("`mark` needs a task number. Try: mark NUMBER", exception.getMessage());
    }

    @Test
    void parseTaskIndex_nonNumericTaskNumber_exceptionThrown() {
        NudgeException exception = assertThrows(
                NudgeException.class, () -> Parser.parseTaskIndex("mark two", "mark"));

        assertEquals("The task number must be a whole number. Try: mark NUMBER",
                exception.getMessage());
    }

    @Test
    void parseFindKeyword_validKeyword_returnsKeyword() throws NudgeException {
        assertEquals("book", Parser.parseFindKeyword("find book"));
    }

    @Test
    void parseFindKeyword_missingKeyword_exceptionThrown() {
        NudgeException exception = assertThrows(NudgeException.class, () -> Parser.parseFindKeyword("find"));

        assertEquals("`find` needs a keyword. Try: find KEYWORD", exception.getMessage());
    }

    @Test
    void parseCommandType_tabSeparator_returnsTodo() {
        assertEquals(CommandType.TODO, Parser.parseCommandType("todo\tread book"));
    }

    @Test
    void parseTodo_validCommand_returnsTodo() throws NudgeException {
        Todo todo = Parser.parseTodo("todo read book");

        assertEquals("read book", todo.getDescription());
    }

    @Test
    void parseDeadline_validCommand_returnsDeadline() throws NudgeException {
        Deadline deadline = Parser.parseDeadline("deadline return book /by 2026-09-20");

        assertEquals("return book", deadline.getDescription());
        assertEquals(LocalDate.of(2026, 9, 20), deadline.getDueDate());
    }

    @Test
    void parseDeadline_repeatedDelimiter_exceptionThrown() {
        NudgeException exception = assertThrows(NudgeException.class, () ->
                Parser.parseDeadline("deadline report /by 2026-09-20 /by 2026-09-21"));

        assertEquals("A deadline should contain `/by` once. Try: "
                + "deadline DESCRIPTION /by yyyy-MM-dd", exception.getMessage());
    }

    @Test
    void parseEvent_validCommand_returnsEvent() throws NudgeException {
        Event event = Parser.parseEvent(
                "event project meeting /from 2026-09-20 /to 2026-09-21");

        assertEquals("project meeting", event.getDescription());
        assertEquals(LocalDate.of(2026, 9, 20), event.getStartDate());
        assertEquals(LocalDate.of(2026, 9, 21), event.getEndDate());
    }

    @Test
    void parseEvent_endBeforeStart_exceptionThrown() {
        NudgeException exception = assertThrows(NudgeException.class, () ->
                Parser.parseEvent("event trip /from 2026-09-21 /to 2026-09-20"));

        assertEquals("The event start date must be before the end date.",
                exception.getMessage());
    }
}
