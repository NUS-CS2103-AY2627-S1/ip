package goat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

/**
 * Tests command parsing and validation.
 */
public class ParserTest {
    @Test
    public void parse_commandWithArgument_separatesCommandWordAndArgument() throws GoatException {
        ParsedCommand command = Parser.parse("  deadline read book /by 2026-09-30  ");

        assertEquals(CommandType.DEADLINE, command.type());
        assertEquals("deadline", command.commandWord());
        assertEquals("read book /by 2026-09-30", command.argument());
    }

    @Test
    public void parse_sortCommand_recognizesSortType() throws GoatException {
        ParsedCommand command = Parser.parse("sort");

        assertEquals(CommandType.SORT, command.type());
        assertEquals("", command.argument());
    }

    @Test
    public void parse_emptyInput_throwsHelpfulException() {
        GoatException exception = assertThrows(GoatException.class, () -> Parser.parse("   "));

        assertEquals("Please enter a command.", exception.getMessage());
    }

    @Test
    public void parseDeadline_validInput_createsFormattedDeadline() throws GoatException {
        Deadline deadline = Parser.parseDeadline("submit report /by 2026-10-15");

        assertEquals("[D][ ] submit report (by: Oct 15 2026)", deadline.toString());
        assertEquals("D | 0 | submit report | 2026-10-15", deadline.toFileString());
    }

    @Test
    public void parseDeadline_impossibleDate_throwsHelpfulException() {
        GoatException exception = assertThrows(
                GoatException.class, () -> Parser.parseDeadline("submit report /by 2026-02-30"));

        assertEquals("The deadline date must be a valid date in yyyy-MM-dd format.",
                exception.getMessage());
    }

    @Test
    public void parseEvent_missingEndDate_throwsHelpfulException() {
        GoatException exception = assertThrows(
                GoatException.class, () -> Parser.parseEvent("meeting /from 2026-09-01 /to"));

        assertEquals("An event needs an end date after /to.", exception.getMessage());
    }

    @Test
    public void parseTodo_repeatedWhitespace_normalizesDescription() throws GoatException {
        Todo todo = Parser.parseTodo("read    the   book");

        assertEquals("[T][ ] read the book", todo.toString());
    }

    @Test
    public void parseDeadline_repeatedByMarker_throwsHelpfulException() {
        GoatException exception = assertThrows(GoatException.class, () ->
                Parser.parseDeadline("submit /by 2026-10-15 /by 2026-10-16"));

        assertEquals("Use exactly one /by: deadline <description> /by <yyyy-MM-dd>",
                exception.getMessage());
    }

    @Test
    public void parseEvent_repeatedFromMarker_throwsHelpfulException() {
        GoatException exception = assertThrows(GoatException.class, () ->
                Parser.parseEvent(
                        "meeting /from 2026-09-01 /from 2026-09-02 /to 2026-09-03"));

        assertEquals("Use exactly one /from and one /to: "
                + "event <description> /from <start date> /to <end date>",
                exception.getMessage());
    }

    @Test
    public void parseEvent_sameStartAndEndDate_throwsHelpfulException() {
        GoatException exception = assertThrows(GoatException.class, () ->
                Parser.parseEvent("meeting /from 2026-09-01 /to 2026-09-01"));

        assertEquals("An event must start before it ends.", exception.getMessage());
    }

    @Test
    public void parseEvent_startAfterEndDate_throwsHelpfulException() {
        GoatException exception = assertThrows(GoatException.class, () ->
                Parser.parseEvent("meeting /from 2026-09-02 /to 2026-09-01"));

        assertEquals("An event must start before it ends.", exception.getMessage());
    }

    @Test
    public void requireNoArgument_argumentPresent_throwsHelpfulException() throws GoatException {
        ParsedCommand command = Parser.parse("list now");

        GoatException exception = assertThrows(
                GoatException.class, () -> Parser.requireNoArgument(command));

        assertEquals("I don't recognise that command. Try todo, deadline, event, list, "
                + "find, sort, mark, unmark, delete, or bye.", exception.getMessage());
    }
}
