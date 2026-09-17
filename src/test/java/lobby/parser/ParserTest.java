package lobby.parser;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import lobby.exception.LobbyException;
import lobby.task.Deadline;
import lobby.task.Event;
import lobby.task.Todo;

/**
 * Tests command parsing and validation in {@link Parser}.
 */
public class ParserTest {
    private final Parser parser = new Parser();

    /**
     * Verifies that each supported command word maps to its command type.
     */
    @Test
    public void parseCommand_knownCommands_returnsMatchingCommands() {
        assertAll(() ->
                assertEquals(Parser.Command.BYE, parser.parseCommand("bye")), () ->
                assertEquals(Parser.Command.LIST, parser.parseCommand("list")), () ->
                assertEquals(Parser.Command.MARK, parser.parseCommand("mark")), () ->
                assertEquals(Parser.Command.UNMARK, parser.parseCommand("unmark")), () ->
                assertEquals(Parser.Command.TODO, parser.parseCommand("todo")), () ->
                assertEquals(Parser.Command.DEADLINE, parser.parseCommand("deadline")), () ->
                assertEquals(Parser.Command.EVENT, parser.parseCommand("event")), () ->
                assertEquals(Parser.Command.DELETE, parser.parseCommand("delete")), () ->
                assertEquals(Parser.Command.FIND, parser.parseCommand("find")));
    }

    /**
     * Verifies that command matching ignores letter case and trailing arguments.
     */
    @Test
    public void parseCommand_mixedCaseWithArguments_returnsMatchingCommand() {
        assertEquals(Parser.Command.TODO, parser.parseCommand("ToDo read book"));
    }

    /**
     * Verifies that unsupported and empty command text maps to {@code UNKNOWN}.
     */
    @Test
    public void parseCommand_unknownOrEmptyCommand_returnsUnknown() {
        assertAll(() ->
                assertEquals(Parser.Command.UNKNOWN, parser.parseCommand("search book")), () ->
                assertEquals(Parser.Command.UNKNOWN, parser.parseCommand("")));
    }

    /**
     * Verifies that find preserves multiword keywords and trims surrounding whitespace.
     *
     * @throws LobbyException if a tested command unexpectedly fails validation
     */
    @Test
    public void parseFind_validCommand_returnsTrimmedKeywords() throws LobbyException {
        assertEquals("read book", parser.parseFind("find   read book   "));
    }

    @Test
    public void parseFind_missingCriteria_exceptionThrown() {
        LobbyException exception = assertThrows(LobbyException.class, () ->
                parser.parseFind("find   "));

        assertEquals("A search needs keywords. Try: find <keywords>.",
                exception.getMessage());
    }

    @Test
    public void parseFind_statusText_preservesLiteralKeywords() throws LobbyException {
        assertEquals("/status done", parser.parseFind("find /status done"));
    }

    @Test
    public void parseTaskNumber_integerArgument_returnsInteger() {
        assertAll(() ->
                assertEquals(3, parser.parseTaskNumber("mark 3", "mark")), () ->
                assertEquals(0, parser.parseTaskNumber("delete   0  ", "delete")), () ->
                assertEquals(-2, parser.parseTaskNumber("unmark -2", "unmark")));
    }

    /**
     * Verifies that a missing task number produces a helpful validation error.
     */
    @Test
    public void parseTaskNumber_missingArgument_exceptionThrown() {
        LobbyException exception = assertThrows(LobbyException.class, () ->
                parser.parseTaskNumber("mark", "mark"));

        assertEquals("Please use mark followed by a task number.", exception.getMessage());
    }

    /**
     * Verifies that non-integer task numbers are rejected.
     */
    @Test
    public void parseTaskNumber_nonIntegerArgument_exceptionThrown() {
        assertAll(() ->
                assertThrows(LobbyException.class, () ->
                        parser.parseTaskNumber("mark first", "mark")), () ->
                assertThrows(LobbyException.class, () ->
                        parser.parseTaskNumber("mark 1.5", "mark")), () ->
                assertThrows(LobbyException.class, () ->
                        parser.parseTaskNumber("mark 2147483648", "mark")));
    }

    /**
     * Verifies that a valid to-do command creates a trimmed, incomplete task.
     *
     * @throws LobbyException if the valid command unexpectedly fails validation
     */
    @Test
    public void parseTodo_validCommand_returnsTrimmedTodo() throws LobbyException {
        Todo todo = parser.parseTodo("todo   read a book   ");

        assertEquals("read a book", todo.getDescription());
        assertFalse(todo.isDone());
        assertEquals("T | 0 | read a book", todo.toDataString());
    }

    /**
     * Verifies that a to-do without a description is rejected.
     */
    @Test
    public void parseTodo_emptyDescription_exceptionThrown() {
        LobbyException exception = assertThrows(LobbyException.class, () ->
                parser.parseTodo("todo    "));

        assertEquals("A to-do needs a description. Try: todo <description>.",
                exception.getMessage());
    }

    /**
     * Verifies that a valid deadline command creates the expected task.
     *
     * @throws LobbyException if the valid command unexpectedly fails validation
     */
    @Test
    public void parseDeadline_validCommand_returnsDeadline() throws LobbyException {
        Deadline deadline = parser.parseDeadline("deadline return book /by 2026-09-30");

        assertEquals("return book", deadline.getDescription());
        assertFalse(deadline.isDone());
        assertEquals("D | 0 | return book | 2026-09-30", deadline.toDataString());
        assertEquals("[D][ ] return book (by: Sep 30 2026)", deadline.toString());
    }

    /**
     * Verifies that deadline parsing accepts extra whitespace and uppercase markers.
     *
     * @throws LobbyException if the valid command unexpectedly fails validation
     */
    @Test
    public void parseDeadline_extraWhitespaceAndUppercaseMarker_returnsTrimmedDeadline()
            throws LobbyException {
        Deadline deadline = parser.parseDeadline(
                "deadline   submit report   /BY   2024-02-29   ");

        assertEquals("submit report", deadline.getDescription());
        assertEquals("D | 0 | submit report | 2024-02-29", deadline.toDataString());
    }

    /**
     * Verifies that a deadline without a {@code /by} marker is rejected.
     */
    @Test
    public void parseDeadline_missingByMarker_exceptionThrown() {
        LobbyException exception = assertThrows(LobbyException.class, () ->
                parser.parseDeadline("deadline return book 2026-09-30"));

        assertEquals(
                "A deadline needs a /by time. Try: deadline <description> /by <when>.",
                exception.getMessage());
    }

    /**
     * Verifies that {@code /by} is not recognized when embedded in another word.
     */
    @Test
    public void parseDeadline_byWithinWord_exceptionThrown() {
        assertThrows(LobbyException.class, () ->
                parser.parseDeadline("deadline return/by 2026-09-30"));
    }

    /**
     * Verifies that a deadline requires a description before {@code /by}.
     */
    @Test
    public void parseDeadline_emptyDescription_exceptionThrown() {
        LobbyException exception = assertThrows(LobbyException.class, () ->
                parser.parseDeadline("deadline /by 2026-09-30"));

        assertEquals("A deadline needs a description before /by.", exception.getMessage());
    }

    /**
     * Verifies that a deadline requires a date after {@code /by}.
     */
    @Test
    public void parseDeadline_emptyDate_exceptionThrown() {
        LobbyException exception = assertThrows(LobbyException.class, () ->
                parser.parseDeadline("deadline return book /by"));

        assertEquals("A deadline needs a time after /by.", exception.getMessage());
    }

    /**
     * Verifies that deadline dates must use the supported date format.
     */
    @Test
    public void parseDeadline_wrongDateFormat_exceptionThrown() {
        LobbyException exception = assertThrows(LobbyException.class, () ->
                parser.parseDeadline("deadline return book /by 30-09-2026"));

        assertEquals(
                "Please enter the deadline date as yyyy-MM-dd, for example 2019-10-15.",
                exception.getMessage());
    }

    /**
     * Verifies that calendar dates that do not exist are rejected.
     */
    @Test
    public void parseDeadline_impossibleDate_exceptionThrown() {
        assertThrows(LobbyException.class, () ->
                parser.parseDeadline("deadline return book /by 2025-02-29"));
    }

    /**
     * Verifies that a valid event command creates the expected task.
     *
     * @throws LobbyException if the valid command unexpectedly fails validation
     */
    @Test
    public void parseEvent_validCommand_returnsEvent() throws LobbyException {
        Event event = parser.parseEvent(
                "event project meeting /from Monday 2pm /to Monday 3pm");

        assertEquals("project meeting", event.getDescription());
        assertFalse(event.isDone());
        assertEquals("E | 0 | project meeting | Monday 2pm | Monday 3pm",
                event.toDataString());
        assertEquals("[E][ ] project meeting (from: Monday 2pm to: Monday 3pm)",
                event.toString());
    }

    /**
     * Verifies that event parsing accepts extra whitespace and uppercase markers.
     *
     * @throws LobbyException if the valid command unexpectedly fails validation
     */
    @Test
    public void parseEvent_extraWhitespaceAndUppercaseMarkers_returnsTrimmedEvent()
            throws LobbyException {
        Event event = parser.parseEvent(
                "event   project meeting   /FROM   2pm   /TO   3pm   ");

        assertEquals("E | 0 | project meeting | 2pm | 3pm", event.toDataString());
    }

    /**
     * Verifies that an event without a {@code /from} marker is rejected.
     */
    @Test
    public void parseEvent_missingFromMarker_exceptionThrown() {
        LobbyException exception = assertThrows(LobbyException.class, () ->
                parser.parseEvent("event meeting 2pm /to 3pm"));

        assertEquals(
                "An event needs a /from start time. Try: event <description> /from <start> /to <end>.",
                exception.getMessage());
    }

    /**
     * Verifies that {@code /from} is not recognized when embedded in another word.
     */
    @Test
    public void parseEvent_fromWithinWord_exceptionThrown() {
        assertThrows(LobbyException.class, () ->
                parser.parseEvent("event meeting/from 2pm /to 3pm"));
    }

    /**
     * Verifies that an event without a {@code /to} marker is rejected.
     */
    @Test
    public void parseEvent_missingToMarker_exceptionThrown() {
        LobbyException exception = assertThrows(LobbyException.class, () ->
                parser.parseEvent("event meeting /from 2pm 3pm"));

        assertEquals(
                "An event needs a /to end time. Try: event <description> /from <start> /to <end>.",
                exception.getMessage());
    }

    /**
     * Verifies that {@code /to} is not recognized when embedded in another word.
     */
    @Test
    public void parseEvent_toWithinWord_exceptionThrown() {
        assertThrows(LobbyException.class, () ->
                parser.parseEvent("event meeting /from 2pm/to 3pm"));
    }

    /**
     * Verifies that an event requires a description before {@code /from}.
     */
    @Test
    public void parseEvent_emptyDescription_exceptionThrown() {
        LobbyException exception = assertThrows(LobbyException.class, () ->
                parser.parseEvent("event /from 2pm /to 3pm"));

        assertEquals("An event needs a description before /from.", exception.getMessage());
    }

    /**
     * Verifies that an event requires a start time after {@code /from}.
     */
    @Test
    public void parseEvent_emptyStartTime_exceptionThrown() {
        LobbyException exception = assertThrows(LobbyException.class, () ->
                parser.parseEvent("event meeting /from /to 3pm"));

        assertEquals("An event needs a start time after /from.", exception.getMessage());
    }

    /**
     * Verifies that an event requires an end time after {@code /to}.
     */
    @Test
    public void parseEvent_emptyEndTime_exceptionThrown() {
        LobbyException exception = assertThrows(LobbyException.class, () ->
                parser.parseEvent("event meeting /from 2pm /to"));

        assertEquals("An event needs an end time after /to.", exception.getMessage());
    }

    @Test
    public void parseMarkers_markerIsPrefixOfWord_rejectsIncompleteCommands() {
        LobbyException deadlineError = assertThrows(LobbyException.class, () ->
                parser.parseDeadline("deadline report /bypass 2026-09-30"));
        LobbyException eventStartError = assertThrows(LobbyException.class, () ->
                parser.parseEvent("event meeting /fromage 2pm /to 3pm"));
        LobbyException eventEndError = assertThrows(LobbyException.class, () ->
                parser.parseEvent("event meeting /from 2pm /tomorrow 3pm"));

        assertEquals("A deadline needs a /by time. Try: deadline <description> /by <when>.",
                deadlineError.getMessage());
        assertEquals("An event needs a /from start time. Try: event <description> /from <start> /to <end>.",
                eventStartError.getMessage());
        assertEquals("An event needs a /to end time. Try: event <description> /from <start> /to <end>.",
                eventEndError.getMessage());
    }

    @Test
    public void parseEvent_tabBoundariesAndMixedCaseMarkers_acceptsMarkers() throws LobbyException {
        Event event = parser.parseEvent("event meeting\t/FrOm\t2pm\t/tO\t3pm");

        assertEquals("E | 0 | meeting | 2pm | 3pm", event.toDataString());
    }
}
