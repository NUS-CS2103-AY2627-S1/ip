package dave.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import dave.command.Command;
import dave.exception.DaveCommandException;
import dave.task.Deadline;
import dave.task.Event;
import dave.task.Todo;

/**
 * Unit tests for the {@link Parser} class.
 */
public class ParserTest {

    @Test
    public void parseCommand_recognizedCommands_returnsMatchingEnum() {
        assertEquals(Command.TODO, Parser.parseCommand("todo read book"));
        assertEquals(Command.DEADLINE, Parser.parseCommand("deadline submit essay /by 2026-10-01"));
        assertEquals(Command.EVENT, Parser.parseCommand("event meeting /from 2026-10-01 /to 2026-10-02"));
        assertEquals(Command.LIST, Parser.parseCommand("list"));
        assertEquals(Command.FIND, Parser.parseCommand("find book"));
        assertEquals(Command.MARK, Parser.parseCommand("mark 2"));
        assertEquals(Command.UNMARK, Parser.parseCommand("unmark 2"));
        assertEquals(Command.DELETE, Parser.parseCommand("delete 3"));
        assertEquals(Command.SNOOZE, Parser.parseCommand("snooze 2"));
        assertEquals(Command.BYE, Parser.parseCommand("bye"));
    }

    @Test
    public void parseCommand_unknownCommand_returnsUnknownEnum() {
        assertEquals(Command.UNKNOWN, Parser.parseCommand("foobar"));
        assertEquals(Command.UNKNOWN, Parser.parseCommand(""));
    }

    @Test
    public void parseTodo_validDescription_returnsTodo() {
        Todo todo = Parser.parseTodo("read documentation");
        assertEquals("read documentation", todo.getDescription());
    }

    @Test
    public void parseTodo_emptyDescription_throwsDaveCommandException() {
        assertThrows(DaveCommandException.class, () -> Parser.parseTodo(""));
    }

    @Test
    public void parseTodo_reservedPipeChar_throwsDaveCommandException() {
        DaveCommandException e = assertThrows(DaveCommandException.class, () ->
                Parser.parseTodo("read book | chapter 1"));
        assertTrue(e.getMessage().contains("cannot contain the '|' character"));
    }

    @Test
    public void parseDeadline_validInput_returnsDeadline() {
        Deadline deadline = Parser.parseDeadline("submit report /by 2026-11-20");
        assertEquals("submit report", deadline.getDescription());
    }

    @Test
    public void parseDeadline_multipleSpacesAroundBy_returnsDeadline() {
        Deadline deadline = Parser.parseDeadline("submit report   /by    2026-11-20");
        assertEquals("submit report", deadline.getDescription());
    }

    @Test
    public void parseDeadline_missingByDelimiter_throwsDaveCommandException() {
        assertThrows(DaveCommandException.class, () -> Parser.parseDeadline("submit report"));
    }

    @Test
    public void parseDeadline_emptyDescription_throwsDaveCommandException() {
        assertThrows(DaveCommandException.class, () -> Parser.parseDeadline(" /by 2026-11-20"));
    }

    @Test
    public void parseDeadline_duplicateByFlag_throwsDaveCommandException() {
        DaveCommandException e = assertThrows(DaveCommandException.class, () ->
                Parser.parseDeadline("submit report /by 2026-10-01 /by 2026-10-02"));
        assertTrue(e.getMessage().contains("cannot be specified multiple times"));
    }

    @Test
    public void parseDeadline_reservedPipeChar_throwsDaveCommandException() {
        DaveCommandException e = assertThrows(DaveCommandException.class, () ->
                Parser.parseDeadline("submit report | part 1 /by 2026-10-01"));
        assertTrue(e.getMessage().contains("cannot contain the '|' character"));
    }

    @Test
    public void parseEvent_validInput_returnsEvent() {
        Event event = Parser.parseEvent("project meeting /from 2026-11-20 14:00 /to 2026-11-20 16:00");
        assertEquals("project meeting", event.getDescription());
    }

    @Test
    public void parseEvent_toBeforeFrom_returnsEvent() {
        Event event = Parser.parseEvent("project meeting /to 2026-11-20 16:00 /from 2026-11-20 14:00");
        assertEquals("project meeting", event.getDescription());
        assertEquals(LocalDateTime.of(2026, 11, 20, 14, 0), event.getFrom());
        assertEquals(LocalDateTime.of(2026, 11, 20, 16, 0), event.getTo());
    }

    @Test
    public void parseEvent_multipleSpacesAroundDelimiters_returnsEvent() {
        Event event = Parser.parseEvent("meeting   /from   2026-11-20 14:00   /to   2026-11-20 16:00");
        assertEquals("meeting", event.getDescription());
    }

    @Test
    public void parseEvent_missingDelimiters_throwsDaveCommandException() {
        assertThrows(DaveCommandException.class, () -> Parser.parseEvent("project meeting /from 2026-11-20"));
        assertThrows(DaveCommandException.class, () -> Parser.parseEvent("project meeting /to 2026-11-20"));
    }

    @Test
    public void parseEvent_duplicateFromFlag_throwsDaveCommandException() {
        DaveCommandException e = assertThrows(DaveCommandException.class, () ->
                Parser.parseEvent("meeting /from 2026-10-01 /to 2026-10-02 /from 2026-10-03"));
        assertTrue(e.getMessage().contains("cannot be specified multiple times"));
    }

    @Test
    public void parseEvent_duplicateToFlag_throwsDaveCommandException() {
        DaveCommandException e = assertThrows(DaveCommandException.class, () ->
                Parser.parseEvent("meeting /from 2026-10-01 /to 2026-10-02 /to 2026-10-03"));
        assertTrue(e.getMessage().contains("cannot be specified multiple times"));
    }

    @Test
    public void parseEvent_reservedPipeChar_throwsDaveCommandException() {
        DaveCommandException e = assertThrows(DaveCommandException.class, () ->
                Parser.parseEvent("meeting | urgent /from 2026-10-01 14:00 /to 2026-10-01 15:00"));
        assertTrue(e.getMessage().contains("cannot contain the '|' character"));
    }

    @Test
    public void parseIndex_validNumber_returnsZeroBasedIndex() {
        assertEquals(0, Parser.parseIndex("1"));
        assertEquals(4, Parser.parseIndex("5"));
    }

    @Test
    public void parseIndex_zero_throwsDaveCommandException() {
        DaveCommandException e = assertThrows(DaveCommandException.class, () -> Parser.parseIndex("0"));
        assertEquals("Pardon, Sire! That is an invalid scroll number.", e.getMessage());
    }

    @Test
    public void parseIndex_negative_throwsDaveCommandException() {
        DaveCommandException e = assertThrows(DaveCommandException.class, () -> Parser.parseIndex("-1"));
        assertEquals("Pardon, Sire! That is an invalid scroll number.", e.getMessage());
    }

    @Test
    public void parseIndex_multipleTokens_throwsDaveCommandException() {
        DaveCommandException e = assertThrows(DaveCommandException.class, () -> Parser.parseIndex("1 2"));
        assertEquals("Pardon, Sire! That is an invalid scroll number.", e.getMessage());
    }

    @Test
    public void parseIndex_nonInteger_throwsDaveCommandException() {
        assertThrows(DaveCommandException.class, () -> Parser.parseIndex("abc"));
        assertThrows(DaveCommandException.class, () -> Parser.parseIndex(""));
    }

    @Test
    public void parseFind_validKeyword_returnsTrimmedKeyword() {
        assertEquals("book", Parser.parseFind("book"));
        assertEquals("read book", Parser.parseFind("  read book  "));
    }

    @Test
    public void parseFind_emptyKeyword_throwsDaveCommandException() {
        assertThrows(DaveCommandException.class, () -> Parser.parseFind(""));
        assertThrows(DaveCommandException.class, () -> Parser.parseFind("   "));
    }

    @Test
    public void parseSnooze_defaultDuration_returnsOneDayRelativeSnooze() {
        SnoozeRequest request = Parser.parseSnooze("2");
        assertEquals(1, request.getIndex());
        assertTrue(request.isRelative());

        Deadline deadline = new Deadline("submit essay", LocalDateTime.of(2026, 10, 1, 12, 0));
        request.applyTo(deadline);
        assertEquals(LocalDateTime.of(2026, 10, 2, 12, 0), deadline.getBy());
    }

    @Test
    public void parseSnooze_relativeOffset_returnsRelativeSnooze() {
        SnoozeRequest request = Parser.parseSnooze("3 5 days");
        assertEquals(2, request.getIndex());
        assertTrue(request.isRelative());

        Deadline deadline = new Deadline("read chapter", LocalDateTime.of(2026, 10, 1, 10, 0));
        request.applyTo(deadline);
        assertEquals(LocalDateTime.of(2026, 10, 6, 10, 0), deadline.getBy());
    }

    @Test
    public void parseSnooze_targetDateTime_returnsTargetSnooze() {
        SnoozeRequest request = Parser.parseSnooze("1 /to 2026-11-15 14:00");
        assertEquals(0, request.getIndex());
        assertFalse(request.isRelative());

        Deadline deadline = new Deadline("submit thesis", LocalDateTime.of(2026, 10, 1, 12, 0));
        request.applyTo(deadline);
        assertEquals(LocalDateTime.of(2026, 11, 15, 14, 0), deadline.getBy());
    }

    @Test
    public void parseSnooze_eventBoundaries_returnsEventSnooze() {
        SnoozeRequest request = Parser.parseSnooze("1 /from 2026-11-01 10:00 /to 2026-11-01 12:00");
        assertEquals(0, request.getIndex());
        assertFalse(request.isRelative());

        Event event = new Event("conference",
                LocalDateTime.of(2026, 10, 1, 10, 0),
                LocalDateTime.of(2026, 10, 1, 12, 0));
        request.applyTo(event);
        assertEquals(LocalDateTime.of(2026, 11, 1, 10, 0), event.getFrom());
        assertEquals(LocalDateTime.of(2026, 11, 1, 12, 0), event.getTo());
    }

    @Test
    public void parseSnooze_emptyArguments_throwsDaveCommandException() {
        assertThrows(DaveCommandException.class, () -> Parser.parseSnooze(""));
        assertThrows(DaveCommandException.class, () -> Parser.parseSnooze("   "));
    }

    @Test
    public void parseSnooze_invalidFormat_throwsDaveCommandException() {
        assertThrows(DaveCommandException.class, () -> Parser.parseSnooze("1 invalid text here"));
        assertThrows(DaveCommandException.class, () -> Parser.parseSnooze("1 /to"));
        assertThrows(DaveCommandException.class, () -> Parser.parseSnooze("1 /from 2026-10-01"));
    }
}
