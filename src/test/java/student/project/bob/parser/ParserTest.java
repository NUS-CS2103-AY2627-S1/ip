package student.project.bob.parser;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import student.project.bob.exception.BobException;
import student.project.bob.model.Deadline;
import student.project.bob.model.Event;
import student.project.bob.model.Todo;

/**
 * Tests command, task, and argument parsing behavior.
 */
public class ParserTest {
    private static final String UNKNOWN_COMMAND_MESSAGE =
            "I do not recognise that command. Try todo, deadline, event, list, mark, unmark, delete, upcoming, on, "
                    + "overdue, find, undo, or bye.";

    /**
     * Verifies that every supported command prefix is classified correctly.
     */
    @Test
    public void parseCommand_supportedCommands_returnsExpectedTypes() throws BobException {
        Parser parser = new Parser();

        assertAll(
                () -> assertEquals(Command.Type.BYE, parser.parseCommand("bye").getType()),
                () -> assertEquals(
                        Command.Type.LIST, parser.parseCommand("list").getType()),
                () -> assertEquals(
                        Command.Type.UPCOMING, parser.parseCommand("upcoming 3").getType()),
                () -> assertEquals(
                        Command.Type.ON, parser.parseCommand("on 2026-01-01").getType()),
                () -> assertEquals(
                        Command.Type.OVERDUE, parser.parseCommand("overdue").getType()),
                () -> assertEquals(
                        Command.Type.FIND, parser.parseCommand("find book").getType()),
                () -> assertEquals(
                        Command.Type.MARK, parser.parseCommand("mark 1").getType()),
                () -> assertEquals(
                        Command.Type.UNMARK, parser.parseCommand("unmark 1").getType()),
                () -> assertEquals(
                        Command.Type.DELETE, parser.parseCommand("delete 1").getType()),
                () -> assertEquals(
                        Command.Type.UNDO, parser.parseCommand("undo").getType()),
                () -> assertEquals(
                        Command.Type.UNDO, parser.parseCommand("undo extra").getType()),
                () -> assertEquals(
                        Command.Type.TASK, parser.parseCommand("todo read book").getType()),
                () -> assertEquals(
                        Command.Type.TASK,
                        parser.parseCommand("deadline report /by 2026-01-01").getType()),
                () -> assertEquals(
                        Command.Type.TASK,
                        parser.parseCommand("event meeting /from 2026-01-01 /to 2026-01-02")
                                .getType()));
    }

    /**
     * Verifies that an unsupported command produces the documented error.
     */
    @Test
    public void parseCommand_unknownCommand_throwsBobException() {
        Parser parser = new Parser();

        BobException exception = assertThrows(BobException.class, () -> parser.parseCommand("unknown"));

        assertEquals(UNKNOWN_COMMAND_MESSAGE, exception.getMessage());
    }

    /**
     * Verifies that todo, deadline, and event commands create the corresponding task types.
     */
    @Test
    public void parseTask_validCommands_returnsExpectedTasks() throws BobException {
        Parser parser = new Parser();
        Todo todo = assertInstanceOf(Todo.class, parser.parseTask("todo read book"));
        Deadline deadline = assertInstanceOf(Deadline.class, parser.parseTask("deadline submit report /by 2026-01-01"));
        Event event = assertInstanceOf(
                Event.class, parser.parseTask("event team sync /from 2026-01-01T09:00 /to 2026-01-01T10:00"));

        assertAll(
                () -> assertEquals("read book", todo.getDescription()),
                () -> assertEquals("[T][ ] read book", todo.toString()),
                () -> assertEquals(LocalDate.of(2026, 1, 1), deadline.getBy()),
                () -> assertEquals("submit report", deadline.getDescription()),
                () -> assertEquals("team sync", event.getDescription()),
                () -> assertEquals("2026-01-01T09:00", event.getFrom().toString()),
                () -> assertEquals("2026-01-01T10:00", event.getTo().toString()));
    }

    /**
     * Verifies that malformed task commands produce specific validation errors.
     */
    @Test
    public void parseTask_invalidCommands_throwsBobException() {
        Parser parser = new Parser();

        assertAll(
                () -> assertEquals(
                        "A todo needs a description. Example: \"todo read a book\".",
                        assertThrows(BobException.class, () -> parser.parseTask("todo"))
                                .getMessage()),
                () -> assertEquals(
                        "A deadline needs a description and a '/by' detail (in yyyy-MM-dd HH:mm / HH:mm:ss). "
                                + "Example: \"deadline submit report /by 2019-10-15\". You may add a time, such as "
                                + "\"2019-10-15 18:00\" or \"2019-10-15 18:00:30\".",
                        assertThrows(BobException.class, () -> parser.parseTask("deadline report"))
                                .getMessage()),
                () -> assertEquals(
                        "A deadline's '/by' detail must be valid (in yyyy-MM-dd HH:mm / HH:mm:ss). "
                                + "Example: \"deadline submit report /by 2019-10-15 18:00\".",
                        assertThrows(BobException.class, () -> parser.parseTask("deadline report /by not-a-date"))
                                .getMessage()),
                () -> assertEquals(
                        "An event needs a description, '/from', and '/to' details "
                                + "(in yyyy-MM-dd HH:mm / HH:mm:ss). Example: \"event team meeting /from "
                                + "2019-10-15 09:00 /to 2019-10-15 10:00\".",
                        assertThrows(BobException.class, () -> parser.parseTask("event meeting"))
                                .getMessage()),
                () -> assertEquals(
                        "Please provide the event description, '/from' date/time, and '/to' date/time "
                                + "(in yyyy-MM-dd HH:mm / HH:mm:ss). Example: \"event team meeting /from "
                                + "2019-10-15 /to 2019-10-16\".",
                        assertThrows(BobException.class, () -> parser.parseTask("event meeting /from 2026-01-01 /to"))
                                .getMessage()),
                () -> assertEquals(
                        "An event's '/from' and '/to' details must be valid "
                                + "(in yyyy-MM-dd HH:mm / HH:mm:ss). Example: \"event team meeting /from "
                                + "2019-10-15 09:00 /to 2019-10-15 10:00\".",
                        assertThrows(
                                        BobException.class,
                                        () -> parser.parseTask("event meeting /from 2026-02-30 /to 2026-03-01"))
                                .getMessage()),
                () -> assertEquals(
                        UNKNOWN_COMMAND_MESSAGE,
                        assertThrows(BobException.class, () -> parser.parseTask("unknown"))
                                .getMessage()));
    }

    /**
     * Verifies the default, zero, and positive upcoming-day arguments.
     */
    @Test
    public void parseUpcomingDays_validArguments_returnsRequestedDays() throws BobException {
        Parser parser = new Parser();

        assertAll(
                () -> assertEquals(7, parser.parseUpcomingDays("upcoming")),
                () -> assertEquals(0, parser.parseUpcomingDays("upcoming 0")),
                () -> assertEquals(30, parser.parseUpcomingDays("upcoming 30")));
    }

    /**
     * Verifies that malformed upcoming-day arguments produce specific errors.
     */
    @Test
    public void parseUpcomingDays_invalidArguments_throwsBobException() {
        Parser parser = new Parser();

        assertAll(
                () -> assertEquals(
                        "The number of days must be a whole number. Example: \"upcoming 7\".",
                        assertThrows(BobException.class, () -> parser.parseUpcomingDays("upcoming nope"))
                                .getMessage()),
                () -> assertEquals(
                        "The number of days cannot be negative. Example: \"upcoming 7\".",
                        assertThrows(BobException.class, () -> parser.parseUpcomingDays("upcoming -1"))
                                .getMessage()),
                () -> assertEquals(
                        "Use \"upcoming\" or \"upcoming <number of days>\". Examples: \"upcoming\" or \"upcoming 7\".",
                        assertThrows(BobException.class, () -> parser.parseUpcomingDays("upcoming 1 2"))
                                .getMessage()));
    }

    /**
     * Verifies that a valid on command returns its date.
     */
    @Test
    public void parseOnDate_validDate_returnsLocalDate() throws BobException {
        Parser parser = new Parser();

        assertEquals(LocalDate.of(2026, 1, 1), parser.parseOnDate("on    2026-01-01"));
    }

    /**
     * Verifies that missing, invalid, and date-time on arguments are rejected.
     */
    @Test
    public void parseOnDate_invalidArguments_throwsBobException() {
        Parser parser = new Parser();
        String invalidDateMessage = "Please enter a valid date using yyyy-MM-dd. Example: \"on 2019-10-15\".";

        assertAll(
                () -> assertEquals(
                        "Please provide a date. Example: \"on 2019-10-15\".",
                        assertThrows(BobException.class, () -> parser.parseOnDate("on"))
                                .getMessage()),
                () -> assertEquals(
                        invalidDateMessage,
                        assertThrows(BobException.class, () -> parser.parseOnDate("on not-a-date"))
                                .getMessage()),
                () -> assertEquals(
                        invalidDateMessage,
                        assertThrows(BobException.class, () -> parser.parseOnDate("on 2026-01-01T09:00"))
                                .getMessage()));
    }

    /**
     * Verifies that a find command returns its trimmed keyword and rejects a missing keyword.
     */
    @Test
    public void parseFindKeyword_validAndMissingKeyword_validatesCorrectly() throws BobException {
        Parser parser = new Parser();

        assertEquals("book", parser.parseFindKeyword("find    book"));
        assertEquals(
                "Please provide a search keyword. Example: \"find report\".",
                assertThrows(BobException.class, () -> parser.parseFindKeyword("find"))
                        .getMessage());
    }
}
