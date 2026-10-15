package alice.parser;

// Used Codex to create these tests.
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import alice.exception.AliceException;
import alice.task.Deadline;
import alice.task.Event;
import alice.task.Recurrence;
import alice.task.Task;
import alice.task.ToDo;

/**
 * Tests conversion and validation of Alice command input.
 */
class ParserTest {
    private final Parser parser = new Parser();

    /** Verifies that supported and unsupported command words map to their command values. */
    @Test
    void fromCommandWord_supportedAndUnsupportedWords_returnsExpectedCommand() {
        assertEquals(Command.TODO, Command.fromCommandWord("todo"));
        assertEquals(Command.DELETE, Command.fromCommandWord("delete"));
        assertEquals(Command.UNKNOWN, Command.fromCommandWord("remind"));
    }

    /** Verifies that valid task commands create task objects with the supplied details. */
    @Test
    void parseTask_validTaskCommands_createsCorrectTasks() throws AliceException {
        Task todo = parser.parseTask("todo read book");
        Task deadline = parser.parseTask("deadline return book /by 2019-12-02 /r MONTHLY");
        Task event = parser.parseTask(
                "event project meeting /from 2019-12-02 1400 /to 2019-12-02 1600 /r weekly");

        assertEquals("read book", assertInstanceOf(ToDo.class, todo).getDescription());
        Deadline parsedDeadline = assertInstanceOf(Deadline.class, deadline);
        assertEquals(LocalDate.of(2019, 12, 2), parsedDeadline.getBy());
        assertEquals(Recurrence.MONTHLY, parsedDeadline.getRecurrence());
        Event parsedEvent = assertInstanceOf(Event.class, event);
        assertEquals(LocalDateTime.of(2019, 12, 2, 14, 0), parsedEvent.getFrom());
        assertEquals(LocalDateTime.of(2019, 12, 2, 16, 0), parsedEvent.getTo());
        assertEquals(Recurrence.WEEKLY, parsedEvent.getRecurrence());
    }

    /** Verifies that malformed task commands report their specific validation errors. */
    @Test
    void parseTask_unknownOrIncompleteCommand_throwsHelpfulException() {
        AliceException unknownCommand = assertThrows(
                AliceException.class, () -> parser.parseTask("remind me"));
        AliceException incompleteDeadline = assertThrows(
                AliceException.class, () -> parser.parseTask("deadline return book"));
        AliceException backwardsEvent = assertThrows(
                AliceException.class, () -> parser.parseTask(
                        "event meeting /from 2019-12-02 1600 /to 2019-12-02 1400"));

        assertEquals("I don't understand that command.", unknownCommand.getMessage());
        assertEquals("A deadline needs a description and a /by date.", incompleteDeadline.getMessage());
        assertEquals("An event cannot end before it starts.", backwardsEvent.getMessage());
    }

    /** Verifies that a valid one-based task number is returned unchanged. */
    @Test
    void parseTaskNumber_validNumber_returnsOneBasedTaskNumber() throws AliceException {
        assertEquals(2, parser.parseTaskNumber("mark 2", Command.MARK, 3));
    }

    /** Verifies that missing, non-numeric, and out-of-range task numbers are rejected. */
    @Test
    void parseTaskNumber_invalidNumbers_throwHelpfulException() {
        AliceException missingNumber = assertThrows(
                AliceException.class, () -> parser.parseTaskNumber("delete", Command.DELETE, 2));
        AliceException nonNumericNumber = assertThrows(
                AliceException.class, () -> parser.parseTaskNumber("mark two", Command.MARK, 2));
        AliceException outOfRangeNumber = assertThrows(
                AliceException.class, () -> parser.parseTaskNumber("unmark 3", Command.UNMARK, 2));

        assertEquals("Please provide a task number to delete.", missingNumber.getMessage());
        assertEquals("The task number must be a positive whole number.", nonNumericNumber.getMessage());
        assertEquals("There is no task numbered 3.", outOfRangeNumber.getMessage());
    }

    /** Verifies that repeat commands identify both an existing task and a supported interval. */
    @Test
    void parseRepeatCommand_validAndInvalidCommands_returnsDetailsOrThrowsHelpfulException()
            throws AliceException {
        Parser.RepeatDetails repeatDetails = parser.parseRepeatCommand("repeat 2 WEEKLY", 3);
        AliceException invalidInterval = assertThrows(
                AliceException.class, () -> parser.parseRepeatCommand("repeat 2 fortnightly", 3));

        assertEquals(2, repeatDetails.taskNumber());
        assertEquals(Recurrence.WEEKLY, repeatDetails.recurrence());
        assertEquals("Repeat interval must be daily, weekly, monthly, yearly, or none.", invalidInterval.getMessage());
    }

    /** Verifies that date input is parsed or rejected with the expected message. */
    @Test
    void parseDate_validAndInvalidDates_returnsDateOrThrowsHelpfulException() throws AliceException {
        assertEquals(LocalDate.of(2019, 12, 2), parser.parseDate("date 2019-12-02"));

        AliceException exception = assertThrows(
                AliceException.class, () -> parser.parseDate("date 2019-13-02"));
        assertEquals("Please use the date format yyyy-MM-dd.", exception.getMessage());
    }

    @Test
    void parseKeyword_presentOrEmptyKeyword_returnsKeywordOrThrowsHelpfulException() throws AliceException {
        assertEquals("book", parser.parseKeyword("find book"));

        AliceException exception = assertThrows(
                AliceException.class, () -> parser.parseKeyword("find"));
        assertEquals("Please provide a keyword to find.", exception.getMessage());
    }
}
