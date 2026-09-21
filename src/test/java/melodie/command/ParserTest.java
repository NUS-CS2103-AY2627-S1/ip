package melodie.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import org.junit.jupiter.api.Test;

import melodie.MelodieException;
import melodie.task.Deadline;
import melodie.task.Event;
import melodie.task.Task;
import melodie.task.Todo;

public class ParserTest {
    private static final String DATE_TIME_ERROR_MESSAGE =
            "Please enter a valid date and time :(\n"
                    + "    Formats: d/M/yyyy HHmm, today HHmm, tomorrow HHmm, "
                    + "or <weekday> HHmm\n"
                    + "    Examples: 2/12/2019 1800, tomorrow 0900, Mon 1400";
    private static final Clock WEDNESDAY_CLOCK =
            Clock.fixed(Instant.parse("2026-09-09T12:00:00Z"), ZoneOffset.UTC);

    @Test
    public void parseTask_validTodo_returnsTodo() throws MelodieException {
        // Arrange
        Parser parser = new Parser();
        ParsedCommand parsedCommand = parser.parse("todo play Yunara jungle");

        // Act
        Task actualTask = parser.parseTask(parsedCommand);

        // Assert
        assertInstanceOf(Todo.class, actualTask);
        assertEquals("play Yunara jungle", actualTask.getDescription());
        assertFalse(actualTask.isCompleted());
        assertEquals("[T][ ] play Yunara jungle", actualTask.toString());
    }

    @Test
    public void parseTask_validDeadline_returnsFormattedDeadline() throws MelodieException {
        Parser parser = new Parser();
        ParsedCommand parsedCommand = parser.parse("deadline lose 500 games /by 8/12/2026 2359");

        Task actualTask = parser.parseTask(parsedCommand);

        assertInstanceOf(Deadline.class, actualTask);
        assertEquals("lose 500 games", actualTask.getDescription());
        assertFalse(actualTask.isCompleted());
        assertEquals("[D][ ] lose 500 games (by: Dec 08 2026, 11:59 PM)", actualTask.toString());
    }

    @Test
    public void parseTask_validEvent_returnsFormattedEvent() throws MelodieException {
        Parser parser = new Parser();
        ParsedCommand parsedCommand =
                parser.parse("event pull C6R1 Nicole /from 16/12/2026 1100 /to 6/1/2027 2359");

        Task actualTask = parser.parseTask(parsedCommand);

        assertInstanceOf(Event.class, actualTask);
        assertEquals("pull C6R1 Nicole", actualTask.getDescription());
        assertFalse(actualTask.isCompleted());
        assertEquals("[E][ ] pull C6R1 Nicole (from: Dec 16 2026, 11:00 AM "
                + "to: Jan 06 2027, 11:59 PM)", actualTask.toString());
    }

    @Test
    public void parseTask_todayAndTomorrow_returnsResolvedEvent() throws MelodieException {
        Parser parser = new Parser(WEDNESDAY_CLOCK);
        ParsedCommand parsedCommand =
                parser.parse("event deploy release /from today 2300 /to tomorrow 0100");

        Task actualTask = parser.parseTask(parsedCommand);

        assertInstanceOf(Event.class, actualTask);
        assertEquals("[E][ ] deploy release (from: Sep 09 2026, 11:00 PM "
                + "to: Sep 10 2026, 1:00 AM)", actualTask.toString());
    }

    @Test
    public void parseTask_abbreviatedWeekday_returnsNextOccurrence() throws MelodieException {
        Parser parser = new Parser(WEDNESDAY_CLOCK);
        ParsedCommand parsedCommand = parser.parse("deadline submit report /by Mon 0900");

        Task actualTask = parser.parseTask(parsedCommand);

        assertInstanceOf(Deadline.class, actualTask);
        assertEquals("[D][ ] submit report (by: Sep 14 2026, 9:00 AM)", actualTask.toString());
    }

    @Test
    public void parseTask_weekdayMatchingToday_returnsFollowingWeek() throws MelodieException {
        Clock mondayClock = Clock.fixed(
                Instant.parse("2026-09-14T12:00:00Z"), ZoneOffset.UTC);
        Parser parser = new Parser(mondayClock);
        ParsedCommand parsedCommand = parser.parse("deadline submit report /by mOnDaY 0900");

        Task actualTask = parser.parseTask(parsedCommand);

        assertEquals("[D][ ] submit report (by: Sep 21 2026, 9:00 AM)", actualTask.toString());
    }

    @Test
    public void parse_mixedCaseCommand_returnsMatchingCommand() throws MelodieException {
        Parser parser = new Parser();

        ParsedCommand actualCommand = parser.parse("ToDo int before elder drag");

        assertEquals(Command.TODO, actualCommand.getCommand());
        assertEquals("int before elder drag", actualCommand.getArguments());
    }

    @Test
    public void parseTask_invalidDate_throwsMelodieException() {
        Parser parser = new Parser();
        ParsedCommand parsedCommand = new ParsedCommand(
                Command.DEADLINE, "farm 600 Intertwined Fate /by 67/67/2067 1800");

        MelodieException exception =
                assertThrows(MelodieException.class, () -> parser.parseTask(parsedCommand));
        // The lambda defers execution so assertThrows can capture the exception.
        assertEquals(DATE_TIME_ERROR_MESSAGE, exception.getMessage());
    }

    @Test
    public void parseTask_unknownNaturalDate_throwsMelodieException() {
        Parser parser = new Parser(WEDNESDAY_CLOCK);
        ParsedCommand parsedCommand = new ParsedCommand(
                Command.DEADLINE, "submit report /by someday 1800");

        MelodieException exception =
                assertThrows(MelodieException.class, () -> parser.parseTask(parsedCommand));
        assertEquals(DATE_TIME_ERROR_MESSAGE, exception.getMessage());
    }

    @Test
    public void parseTask_invalidNaturalTime_throwsMelodieException() {
        Parser parser = new Parser(WEDNESDAY_CLOCK);
        ParsedCommand parsedCommand = new ParsedCommand(
                Command.DEADLINE, "submit report /by tomorrow 2500");

        MelodieException exception =
                assertThrows(MelodieException.class, () -> parser.parseTask(parsedCommand));
        assertEquals(DATE_TIME_ERROR_MESSAGE, exception.getMessage());
    }

    @Test
    public void parseTask_eventEndsBeforeStart_throwsMelodieException() {
        Parser parser = new Parser();
        ParsedCommand parsedCommand = new ParsedCommand(
                Command.EVENT,
                "try not to get F for CS2013T /from 10/2/2027 2300 /to 10/2/2027 1800");

        MelodieException exception =
                assertThrows(MelodieException.class, () -> parser.parseTask(parsedCommand));
        assertEquals("The event cannot end before it starts :(", exception.getMessage());
    }

    @Test
    public void parseTask_missingDescription_throwsMelodieException() {
        Parser parser = new Parser();
        ParsedCommand parsedCommand = new ParsedCommand(Command.TODO, "");

        MelodieException exception =
                assertThrows(MelodieException.class, () -> parser.parseTask(parsedCommand));
        assertEquals("You can't leave the description of a todo empty :(", exception.getMessage());
    }
}
