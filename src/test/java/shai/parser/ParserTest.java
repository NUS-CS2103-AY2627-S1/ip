package shai.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import shai.command.AddCommand;
import shai.command.Command;
import shai.command.DeleteCommand;
import shai.command.ExitCommand;
import shai.command.FindCommand;
import shai.command.ListCommand;
import shai.command.MarkCommand;
import shai.command.ReminderCommand;
import shai.command.UnmarkCommand;
import shai.exception.ShaiException;
import shai.storage.Storage;
import shai.task.Deadline;
import shai.task.Event;
import shai.task.TaskList;
import shai.task.ToDo;
import shai.ui.Ui;

/** Tests command selection, argument extraction, and parser validation. */
class ParserTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void parse_knownCommands_returnsMatchingCommandTypes() throws ShaiException {
        Parser parser = new Parser();

        assertInstanceOf(ExitCommand.class, parser.parse("bye", 0));
        assertInstanceOf(ListCommand.class, parser.parse("list", 0));
        assertInstanceOf(FindCommand.class, parser.parse("find book", 0));
        assertInstanceOf(MarkCommand.class, parser.parse("mark 1", 1));
        assertInstanceOf(UnmarkCommand.class, parser.parse("unmark 1", 1));
        assertInstanceOf(DeleteCommand.class, parser.parse("delete 1", 1));
        assertInstanceOf(ReminderCommand.class, parser.parse("remind", 0));
        assertInstanceOf(ReminderCommand.class, parser.parse("remind 1 /before 3h", 1));
        assertInstanceOf(ReminderCommand.class, parser.parse("remind 1 /off", 1));
        assertInstanceOf(AddCommand.class, parser.parse("todo buy milk", 0));
        assertInstanceOf(AddCommand.class, parser.parse("deadline return book /by 2019-12-01", 0));
        assertInstanceOf(AddCommand.class,
                parser.parse("event meeting /from 2019-12-01 1400 /to 2019-12-01 1600", 0));
    }

    @Test
    void parse_invalidArguments_reportUsefulErrors() {
        Parser parser = new Parser();

        assertThrows(ShaiException.class, () -> parser.parse(null, 0));
        assertThrows(IllegalArgumentException.class, () -> parser.parse("list", -1));
    }

    @Test
    void parse_deadlineCommand_createsDeadlineWithParsedDate() throws ShaiException {
        Parser parser = new Parser();
        TaskList tasks = new TaskList();
        Command command = parser.parse("deadline return book /by 2019-12-01", 0);

        command.execute(tasks, new Ui(), storage());

        Deadline deadline = assertInstanceOf(Deadline.class, tasks.get(0));
        assertEquals("return book", deadline.getDescription());
        assertEquals(LocalDateTime.of(2019, 12, 1, 0, 0), deadline.getBy());
    }

    @Test
    void parse_eventCommand_createsEventWithParsedTimes() throws ShaiException {
        Parser parser = new Parser();
        TaskList tasks = new TaskList();
        Command command = parser.parse(
                "event team meeting /from 2019-12-01 1400 /to 2019-12-01 1600", 0);

        command.execute(tasks, new Ui(), storage());

        Event event = assertInstanceOf(Event.class, tasks.get(0));
        assertEquals("team meeting", event.getDescription());
        assertEquals(LocalDateTime.of(2019, 12, 1, 14, 0), event.getFrom());
        assertEquals(LocalDateTime.of(2019, 12, 1, 16, 0), event.getTo());
    }

    @Test
    void parse_markCommand_usesOneBasedTaskNumber() throws ShaiException {
        Parser parser = new Parser();
        TaskList tasks = new TaskList();
        tasks.add(new ToDo("first"));
        tasks.add(new ToDo("second"));
        Command command = parser.parse("mark 2", tasks.size());

        command.execute(tasks, new Ui(), storage());

        assertFalse(tasks.get(0).isDone());
        assertTrue(tasks.get(1).isDone());
    }

    @Test
    void parse_invalidCommands_throwsUsefulErrors() {
        Parser parser = new Parser();

        assertParseError(parser, "blah", 0, "Turnover. Check your command, King.");
        assertParseError(parser, "find", 0, "Please provide a keyword after find.");
        assertParseError(parser, "todo", 0, "Hold up - I need a description for that todo.");
        assertParseError(parser, "mark 2", 1, "That task number is not in your list yet.");
        assertParseError(parser, "deadline report /by 2019-02-30", 0,
                "Invalid date/time. Use yyyy-MM-dd HHmm, for example 2019-12-02 1800.");
        assertParseError(parser, "remind 1", 1,
                "Call the reminder play like this: remind; remind <task number> /before <duration>; or "
                        + "remind <task number> /off, King.");
        assertParseError(parser, "remind 1 /before", 1,
                "That reminder play needs a duration, King. Try: remind 1 /before 2h.");
        assertParseError(parser, "remind 1 /before later", 1,
                "That reminder duration is out of bounds, King. Use 30m, 1h, or 1d.");
        assertParseError(parser,
                "event team meeting /from 2019-12-01 1600 /to 2019-12-01 1400", 0,
                "An event must end after it starts.");
    }

    @Test
    void parse_ambiguousSyntax_isRejected() {
        Parser parser = new Parser();

        assertParseError(parser, "todo  buy milk", 0,
                "Use a single space between command parameters, King.");
        assertParseError(parser, "deadline report /by2026-01-01", 0,
                "A deadline needs a date after /by. Try: deadline submit report /by 2019-12-01.");
        assertParseError(parser, "deadline report /by 2026-01-01 /by 2026-01-02", 0,
                "A deadline must contain exactly one /by parameter.");
        assertParseError(parser, "remind 1 /before2h", 1,
                "Call the reminder play like this: remind; remind <task number> /before <duration>; or "
                        + "remind <task number> /off, King.");
        assertParseError(parser, "mark +1", 1,
                "The task number after mark must be a whole number.");
    }

    @Test
    void parse_reminderWithClock_usesSuppliedClock() throws ShaiException {
        Clock clock = Clock.fixed(Instant.parse("2026-09-11T12:00:00Z"), ZoneId.of("UTC"));
        Parser parser = new Parser(clock);
        TaskList tasks = new TaskList();
        Command command = parser.parse("deadline submit report /by 2026-09-15 1700", 0);
        command.execute(tasks, new Ui(), storage());

        parser.parse("remind 1 /before 3h", tasks.size()).execute(tasks, new Ui(), storage());

        Deadline deadline = assertInstanceOf(Deadline.class, tasks.get(0));
        assertEquals(180, deadline.getReminderMinutesBefore());
    }

    @Test
    void parse_invalidTaskNumbers_rejectsMalformedAndOutOfRangeValues() {
        Parser parser = new Parser();

        assertParseError(parser, "delete one", 1,
                "The task number after delete must be a whole number.");
        assertParseError(parser, "delete 0", 1, "That task number is not in your list yet.");
        assertParseError(parser, "delete -1", 1,
                "The task number after delete must be a whole number.");
        assertParseError(parser, "delete 2", 1, "That task number is not in your list yet.");
        assertParseError(parser, "delete 2147483648", 1,
                "The task number after delete must be a whole number.");
    }

    @Test
    void parse_reminderDurations_acceptsZeroAndMaximumButRejectsOverflow() throws ShaiException {
        Parser parser = new Parser();

        assertInstanceOf(ReminderCommand.class, parser.parse("remind 1 /before 0m", 1));
        assertInstanceOf(ReminderCommand.class, parser.parse("remind 1 /before 2147483647m", 1));
        assertParseError(parser, "remind 1 /before 2147483648m", 1,
                "That reminder duration is too large, King.");
        assertParseError(parser, "remind 1 /before 35791395h", 1,
                "That reminder duration is too large, King.");
    }

    private Storage storage() {
        return new Storage(temporaryDirectory.resolve("tasks.txt").toString());
    }

    private static void assertParseError(Parser parser, String input, int taskCount,
                                         String expectedMessage) {
        ShaiException exception = assertThrows(ShaiException.class, () -> parser.parse(input, taskCount));

        assertEquals(expectedMessage, exception.getMessage());
    }
}
