package panda.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import panda.exception.EmptyDescriptionException;
import panda.exception.EmptySearchTermException;
import panda.exception.InvalidCommandException;
import panda.exception.InvalidDateException;
import panda.exception.InvalidTaskNumberException;
import panda.exception.InvalidUpdateException;
import panda.exception.MissingDateTimeException;
import panda.exception.PandaException;
import panda.task.Deadline;
import panda.task.Event;
import panda.task.Todo;

/**
 * Tests how {@link Parser} converts user input into structured commands.
 */
class ParserTest {
    private final Parser parser = new Parser();

    @Test
    void parse_todoCommand_returnsTodoWithCompleteDescription() throws PandaException {
        Parser.ParsedCommand result = parser.parse("todo read a Java book");

        assertEquals(Command.TODO, result.command());
        Todo todo = assertInstanceOf(Todo.class, result.task());
        assertEquals("read a Java book", todo.getName());
        assertNull(result.taskNumber());
        assertNull(result.filterDate());
    }

    @Test
    void parse_numberedCommands_returnsCommandAndTaskNumber() throws PandaException {
        Parser.ParsedCommand mark = parser.parse("mark 2");
        Parser.ParsedCommand unmark = parser.parse("unmark 15");
        Parser.ParsedCommand delete = parser.parse("delete 1");

        assertEquals(Command.MARK, mark.command());
        assertEquals(2, mark.taskNumber());
        assertEquals(Command.UNMARK, unmark.command());
        assertEquals(15, unmark.taskNumber());
        assertEquals(Command.DELETE, delete.command());
        assertEquals(1, delete.taskNumber());
    }

    @Test
    void parse_updateNameCommand_returnsTaskNumberAndUpdatedName() throws PandaException {
        Parser.ParsedCommand result = parser.parse(
                "update 2 /name discuss report /by Friday");

        assertEquals(Command.UPDATE, result.command());
        assertEquals(2, result.taskNumber());
        assertEquals("discuss report /by Friday", result.updateDetails().updatedName());
        assertNull(result.updateDetails().updatedDeadlineDateTime());
        assertNull(result.updateDetails().updatedStartDateTime());
        assertNull(result.updateDetails().updatedEndDateTime());
        assertNull(result.task());
        assertNull(result.filterDate());
        assertNull(result.searchTerm());
    }

    @Test
    void parse_updateTimingCommands_returnsCompleteDateTimeText() throws PandaException {
        Parser.ParsedCommand deadlineResult = parser.parse(
                "update 2 /by 2026-09-15 18:00");
        Parser.ParsedCommand eventResult = parser.parse(
                "update 3 /from 2026-09-16 14:00 /to 2026-09-16 17:00");

        assertEquals("2026-09-15 18:00",
                deadlineResult.updateDetails().updatedDeadlineDateTime());
        assertNull(deadlineResult.updateDetails().updatedName());
        assertEquals("2026-09-16 14:00",
                eventResult.updateDetails().updatedStartDateTime());
        assertEquals("2026-09-16 17:00",
                eventResult.updateDetails().updatedEndDateTime());
    }

    @Test
    void parse_combinedUpdateCommands_returnsNameAndTimingDetails() throws PandaException {
        Parser.ParsedCommand deadlineResult = parser.parse(
                "update 2 /by 2026-09-15 18:00 /name submit final report");
        Parser.ParsedCommand eventResult = parser.parse(
                "update 3 /from 2026-09-16 14:00 /to 2026-09-16 17:00 "
                        + "/name project consultation");

        assertEquals("submit final report", deadlineResult.updateDetails().updatedName());
        assertEquals("2026-09-15 18:00",
                deadlineResult.updateDetails().updatedDeadlineDateTime());
        assertEquals("project consultation", eventResult.updateDetails().updatedName());
        assertEquals("2026-09-16 14:00",
                eventResult.updateDetails().updatedStartDateTime());
        assertEquals("2026-09-16 17:00",
                eventResult.updateDetails().updatedEndDateTime());
    }

    @Test
    void parse_listCommands_returnsOptionalDateFilter() throws PandaException {
        Parser.ParsedCommand unfiltered = parser.parse("list");
        Parser.ParsedCommand filtered = parser.parse("list 2026-08-26");

        assertEquals(Command.LIST, unfiltered.command());
        assertNull(unfiltered.filterDate());
        assertEquals(Command.LIST, filtered.command());
        assertEquals(LocalDate.of(2026, 8, 26), filtered.filterDate());
    }

    @Test
    void parse_findCommand_returnsCompleteSearchTerm() throws PandaException {
        Parser.ParsedCommand result = parser.parse("find Java book");

        assertEquals(Command.FIND, result.command());
        assertEquals("Java book", result.searchTerm());
        assertNull(result.task());
        assertNull(result.taskNumber());
        assertNull(result.filterDate());
    }

    @Test
    void parse_datedTaskCommands_returnsCorrectTaskTypesAndDetails()
            throws PandaException {
        Parser.ParsedCommand deadlineResult = parser.parse(
                "deadline submit report /by 2026-09-01 18:00");
        Parser.ParsedCommand eventResult = parser.parse(
                "event project meeting /from 2026-09-02 14:00 /to 2026-09-02 16:00");

        assertEquals(Command.DEADLINE, deadlineResult.command());
        Deadline deadline = assertInstanceOf(Deadline.class, deadlineResult.task());
        assertEquals("submit report", deadline.getName());
        assertEquals("submit report (by: Sep 01 2026 18:00)", deadline.getDisplayText());

        assertEquals(Command.EVENT, eventResult.command());
        Event event = assertInstanceOf(Event.class, eventResult.task());
        assertEquals("project meeting", event.getName());
        assertEquals("project meeting (from: Sep 02 2026 14:00 to: Sep 02 2026 16:00)",
                event.getDisplayText());
    }

    @Test
    void parse_missingOrMalformedArguments_throwsFocusedExceptions() {
        assertThrows(EmptyDescriptionException.class, () -> parser.parse("todo"));
        assertThrows(EmptySearchTermException.class, () -> parser.parse("find"));
        assertThrows(InvalidTaskNumberException.class, () -> parser.parse("mark bamboo"));
        assertThrows(InvalidTaskNumberException.class, () -> parser.parse("update bamboo /name book"));
        assertThrows(InvalidUpdateException.class, () -> parser.parse("update 1"));
        assertThrows(InvalidUpdateException.class, () -> parser.parse("update 1 /name"));
        assertThrows(InvalidUpdateException.class, () -> parser.parse("update 1 /by"));
        assertThrows(InvalidUpdateException.class, () -> parser.parse("update 1 /from tomorrow"));
        assertThrows(InvalidUpdateException.class, () ->
                parser.parse("update 1 /by 2026-09-15 18:00 /name"));
        assertThrows(InvalidUpdateException.class, () ->
                parser.parse("update 1 /from 2026-09-16 14:00 /to 2026-09-16 17:00 /name"));
        assertThrows(InvalidDateException.class, () -> parser.parse("list 2025-02-29"));
        assertThrows(MissingDateTimeException.class, () ->
                parser.parse("deadline submit report")
        );
        assertThrows(MissingDateTimeException.class, () ->
                parser.parse("event meeting /from 2026-09-02 14:00")
        );
    }

    @Test
    void parse_unknownCommand_throwsInvalidCommandException() {
        assertThrows(InvalidCommandException.class, () -> parser.parse("feed panda"));
    }
}
