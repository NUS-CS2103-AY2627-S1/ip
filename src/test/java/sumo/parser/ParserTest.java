package sumo.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import sumo.command.ExitCommand;
import sumo.command.FindCommand;
import sumo.command.ListCommand;
import sumo.command.OnCommand;
import sumo.command.SortCommand;
import sumo.exception.SumoException;
import sumo.parser.Parser.CommandType;
import sumo.parser.Parser.ParsedCommand;
import sumo.task.Deadline;
import sumo.task.Event;
import sumo.task.Todo;

/** Tests command recognition, validation, and task construction in {@link Parser}. */
public class ParserTest {
    private final Parser parser = new Parser();

    /** Verifies commands that take no arguments. */
    @Test
    public void parse_commandsWithoutArguments_correctCommandTypesReturned() throws SumoException {
        assertInstanceOf(ExitCommand.class, parser.parse("bye", 0));
        assertInstanceOf(ListCommand.class, parser.parse("list", 0));
        assertInstanceOf(SortCommand.class, parser.parse("sort", 0));
        assertInstanceOf(FindCommand.class, parser.parse("find book", 0));
    }

    /** Verifies that todo input produces an add command containing a todo. */
    @Test
    public void parse_todoCommand_addCommandContainsTodo() throws SumoException {
        ParsedCommand command = assertInstanceOf(ParsedCommand.class,
                parser.parse("todo read a book", 0));

        assertEquals(CommandType.ADD, command.getType());
        assertEquals(-1, command.getTaskIndex());
        Todo todo = assertInstanceOf(Todo.class, command.getTask());
        assertEquals("read a book", todo.getDescription());
    }

    /** Verifies parsing of a date-only deadline. */
    @Test
    public void parse_deadlineWithDate_dateOnlyDeadlineReturned() throws SumoException {
        ParsedCommand command = assertInstanceOf(ParsedCommand.class,
                parser.parse("deadline submit report /by 3/2/2026", 0));

        Deadline deadline = assertInstanceOf(Deadline.class, command.getTask());
        assertEquals(LocalDateTime.of(2026, 2, 3, 0, 0), deadline.getBy());
        assertEquals("D | 0 | submit report | 2026-02-03", deadline.toDataString());
    }

    /** Verifies parsing of a deadline with a time. */
    @Test
    public void parse_deadlineWithTime_dateTimeDeadlineReturned() throws SumoException {
        ParsedCommand command = assertInstanceOf(ParsedCommand.class,
                parser.parse("deadline submit report /by 2026-02-03 0915", 0));

        Deadline deadline = assertInstanceOf(Deadline.class, command.getTask());
        assertEquals(LocalDateTime.of(2026, 2, 3, 9, 15), deadline.getBy());
        assertEquals("D | 0 | submit report | 2026-02-03T09:15", deadline.toDataString());
    }

    /** Verifies that event endpoints retain their individual precision. */
    @Test
    public void parse_eventWithMixedDateTimes_eventRetainsEachInputFormat() throws SumoException {
        ParsedCommand command = assertInstanceOf(ParsedCommand.class,
                parser.parse("event camp /from 2026-02-03 /to 4/2/2026 1730", 0));

        Event event = assertInstanceOf(Event.class, command.getTask());
        assertEquals(LocalDateTime.of(2026, 2, 3, 0, 0), event.getFrom());
        assertEquals(LocalDateTime.of(2026, 2, 4, 17, 30), event.getTo());
        assertEquals("E | 0 | camp | 2026-02-03 | 2026-02-04T17:30", event.toDataString());
    }

    /** Verifies conversion from user-facing task numbers to list indexes. */
    @Test
    public void parse_indexedCommands_validTaskNumberConvertedToZeroBasedIndex() throws SumoException {
        assertIndexedCommand("mark 2", CommandType.MARK);
        assertIndexedCommand("unmark 2", CommandType.UNMARK);
        assertIndexedCommand("delete 2", CommandType.DELETE);
    }

    /** Verifies both supported date formats for the on command. */
    @Test
    public void parse_onWithSupportedDateFormats_onCommandsReturned() throws SumoException {
        assertInstanceOf(OnCommand.class, parser.parse("on 2026-02-03", 0));
        assertInstanceOf(OnCommand.class, parser.parse("on 3/2/2026", 0));
    }

    /** Verifies rejection of commands with missing required arguments. */
    @Test
    public void parse_blankRequiredArguments_exceptionThrown() {
        assertThrows(SumoException.class, () -> parser.parse("todo", 0));
        assertThrows(SumoException.class, () -> parser.parse("deadline /by 2026-02-03", 0));
        assertThrows(SumoException.class, () -> parser.parse("event camp /from 2026-02-03 /to", 0));
        assertThrows(SumoException.class, () -> parser.parse("mark", 1));
        assertThrows(SumoException.class, () -> parser.parse("on", 0));
        assertThrows(SumoException.class, () -> parser.parse("find", 0));
        assertThrows(SumoException.class, () -> parser.parse("sort extra", 0));
    }

    /** Verifies rejection of invalid dates and times. */
    @Test
    public void parse_invalidDatesAndTimes_exceptionThrown() {
        assertThrows(SumoException.class, () -> parser.parse("deadline report /by 2025-02-29", 0));
        assertThrows(SumoException.class, () -> parser.parse("deadline report /by 2026-02-03 2400", 0));
        assertThrows(SumoException.class, () -> parser.parse("event camp /from tomorrow /to 2026-02-03", 0));
    }

    /** Verifies rejection of invalid task numbers. */
    @Test
    public void parse_invalidTaskNumbers_exceptionThrown() {
        assertThrows(SumoException.class, () -> parser.parse("mark zero", 2));
        assertThrows(SumoException.class, () -> parser.parse("mark 0", 2));
        assertThrows(SumoException.class, () -> parser.parse("mark 3", 2));
    }

    /** Verifies rejection of the delimiter reserved for stored task fields. */
    @Test
    public void parse_persistenceDelimiterInTaskData_exceptionThrown() {
        assertThrows(SumoException.class, () -> parser.parse("todo first | second", 0));
        assertThrows(SumoException.class, () -> parser.parse("deadline report /by 2026-02-03 | extra", 0));
    }

    /** Verifies rejection of unrecognized commands. */
    @Test
    public void parse_unknownCommand_exceptionThrown() {
        assertThrows(SumoException.class, () -> parser.parse("remind me", 0));
    }

    @Test
    public void parse_extraWhitespace_normalizedAcrossCommands() throws SumoException {
        assertInstanceOf(ListCommand.class, parser.parse(" \tlist \t", 0));
        assertInstanceOf(ExitCommand.class, parser.parse("\u00a0bye\u00a0", 0));
        assertInstanceOf(SortCommand.class, parser.parse("  sort  ", 0));
        ParsedCommand indexed = assertInstanceOf(ParsedCommand.class, parser.parse(" mark\t  1 ", 1));
        assertEquals(0, indexed.getTaskIndex());
        ParsedCommand command = assertInstanceOf(ParsedCommand.class,
                parser.parse("  event\tcamp   trip \t/from\t2026-02-03   0900 /to   2026-02-03  1000  ", 0));
        assertEquals("E | 0 | camp trip | 2026-02-03T09:00 | 2026-02-03T10:00", command.getTask().toDataString());
    }

    @Test
    public void parse_emptyOrControlCharacters_userFacingError() {
        for (String input : Arrays.asList(null, "", "   ", "\t", "\u00a0", "todo one\ntodo two",
                "todo one\rtwo", "todo one\u0000two", "todo one\u001btwo", "todo one\u2028two")) {
            assertThrows(SumoException.class, () -> parser.parse(input, 0), String.valueOf(input));
        }
    }

    @Test
    public void parse_repeatedMissingOrMisplacedParameters_rejected() {
        for (String input : List.of("deadline /by 2026-02-03", "deadline report /by",
                "deadline report /by /by 2026-02-03", "deadline report /by 2026-02-03 /by 2026-02-04",
                "deadline report /to 2026-02-03 /by 2026-02-04", "deadline report /by2026-02-03",
                "event camp /from /to 2026-02-04", "event camp /to 2026-02-04 /from 2026-02-03",
                "event camp /from 2026-02-03 /from 2026-02-04 /to 2026-02-05",
                "event camp /from 2026-02-03 /to 2026-02-04 /to 2026-02-05",
                "event camp /from 2026-02-03 /to 2026-02-04 /extra value")) {
            assertThrows(SumoException.class, () -> parser.parse(input, 0), input);
        }
    }

    @Test
    public void parse_invalidCalendarValuesAndFormats_rejected() {
        for (String date : List.of("2026-02-30", "29/2/2025", "31/4/2026", "2026-13-01", "0000-01-01",
                "+10000-01-01", "1/1/26", "2026-02-03 2400", "2026-02-03 1260", "2026-02-03 9:30",
                "2026-02-03 0900 extra")) {
            assertThrows(SumoException.class, () -> parser.parse("deadline report /by " + date, 0), date);
            assertThrows(SumoException.class, () -> parser.parse("on " + date, 0), date);
        }
    }

    @Test
    public void parse_equalOrReversedEventEndpoints_rejected() {
        for (String input : List.of("event camp /from 2026-02-03 /to 2026-02-03",
                "event camp /from 2026-02-04 /to 2026-02-03",
                "event camp /from 2026-02-03 0900 /to 2026-02-03 0900",
                "event camp /from 2026-02-03 1000 /to 2026-02-03 0900",
                "event camp /from 2026-02-03 1000 /to 2026-02-03")) {
            assertThrows(SumoException.class, () -> parser.parse(input, 0), input);
        }
    }

    @Test
    public void parse_validLeapDayAndTimeBoundaries_accepted() throws SumoException {
        assertInstanceOf(OnCommand.class, parser.parse("on 29/2/2024", 0));
        ParsedCommand command = assertInstanceOf(ParsedCommand.class,
                parser.parse("event leap day /from 2024-02-29 0000 /to 2024-02-29 2359", 0));
        assertEquals("E | 0 | leap day | 2024-02-29T00:00 | 2024-02-29T23:59", command.getTask().toDataString());
    }

    @Test
    public void parse_malformedAndOverflowingTaskNumbers_rejected() {
        for (String number : List.of("-1", "+1", "1.0", "1 2", "1!", "\u0661", "2147483648",
                "999999999999999999999999", "0", "2")) {
            for (String command : List.of("mark", "unmark", "delete")) {
                assertThrows(SumoException.class, () -> parser.parse(command + " " + number, 1), number);
            }
        }
    }

    @Test
    public void parse_reservedPipes_rejectedAtFieldBoundaries() {
        for (String input : List.of("todo a|b", "deadline report | /by 2026-02-03",
                "event |camp /from 2026-02-03 /to 2026-02-04")) {
            assertThrows(SumoException.class, () -> parser.parse(input, 0), input);
        }
    }

    /** Checks the type and zero-based index of an indexed command. */
    private void assertIndexedCommand(String input, CommandType expectedType) throws SumoException {
        ParsedCommand command = assertInstanceOf(ParsedCommand.class, parser.parse(input, 3));
        assertEquals(expectedType, command.getType());
        assertEquals(1, command.getTaskIndex());
    }
}
