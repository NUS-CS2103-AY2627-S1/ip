package tianyi.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import tianyi.TianyiException;
import tianyi.task.Deadline;
import tianyi.task.Event;
import tianyi.task.Task;
import tianyi.task.ToDo;

/**
 * Tests parsing and validation of task-creation command arguments.
 */
public class TaskParserTest {
    private final TaskParser parser = new TaskParser();

    @Test
    public void parse_todoArgument_returnsTodoWithOriginalDescription()
            throws TianyiException {
        Task task = parser.parse(CommandType.TODO, "Buy Milk");

        assertInstanceOf(ToDo.class, task);
        assertEquals("T | 0 | Buy Milk", task.getData());
    }

    @Test
    public void parse_deadlineArgument_returnsDeadlineWithDateAndTime()
            throws TianyiException {
        Task task = parser.parse(
                CommandType.DEADLINE, "submit report /by 2-12-2019 18:00");

        assertInstanceOf(Deadline.class, task);
        assertEquals("D | 0 | submit report | 2-12-2019 18:00", task.getData());
    }

    @Test
    public void parse_eventArgument_returnsEventWithDateRange()
            throws TianyiException {
        Task task = parser.parse(
                CommandType.EVENT, "workshop /from 2-12-2019 /to 3-12-2019 16:00");

        assertInstanceOf(Event.class, task);
        assertEquals("E | 0 | workshop | 2-12-2019 | 3-12-2019 16:00", task.getData());
    }

    @Test
    public void parse_emptyArgument_exceptionThrown() {
        assertParseFails(CommandType.TODO, "",
                "The argument of [todo] cannot be empty.\n"
                        + "Try: todo borrow book");
        assertParseFails(CommandType.DEADLINE, "",
                "The argument of [deadline] cannot be empty.\n"
                        + "Try: deadline return book /by 2-12-2019 18:00");
        assertParseFails(CommandType.EVENT, "",
                "The argument of [event] cannot be empty.\n"
                        + "Try: event meeting /from 2-12-2019 14:00 "
                        + "/to 2-12-2019 16:00");
    }

    @Test
    public void parse_argumentContainingStorageSeparator_exceptionThrown() {
        assertParseFails(CommandType.TODO, "read | write",
                "The argument of [todo] cannot contain \"|\".\n"
                        + "Try: todo borrow book");
        assertParseFails(CommandType.DEADLINE, "return | renew book /by 2-12-2019",
                "The argument of [deadline] cannot contain \"|\".\n"
                        + "Try: deadline return book /by 2-12-2019 18:00");
        assertParseFails(CommandType.EVENT,
                "team | client meeting /from 2-12-2019 14:00 /to 2-12-2019 16:00",
                "The argument of [event] cannot contain \"|\".\n"
                        + "Try: event meeting /from 2-12-2019 14:00 "
                        + "/to 2-12-2019 16:00");
    }

    @Test
    public void parse_malformedDeadlineArgument_exceptionThrown() {
        String example = "Try: deadline return book /by 2-12-2019 18:00";

        assertParseFails(CommandType.DEADLINE, "return book",
                "[deadline] must contain /by.\n" + example);
        assertParseFails(CommandType.DEADLINE, "/by 2-12-2019",
                "The description of [deadline] cannot be empty.\n" + example);
        assertParseFails(CommandType.DEADLINE, "return book /by",
                "The by date of [deadline] cannot be empty.\n" + example);
        assertParseFails(CommandType.DEADLINE, "return book /by 31-2-2019",
                "Invalid [deadline] date or time. "
                        + "Please use d-M-yyyy with optional HH:mm.\n" + example);
        assertParseFails(CommandType.DEADLINE,
                "return book /by 2-12-2019 /by 3-12-2019",
                "[deadline] accepts /by only once.\n" + example);
    }

    @Test
    public void parse_malformedEventArgument_exceptionThrown() {
        String example = "Try: event meeting /from 2-12-2019 14:00 /to 2-12-2019 16:00";

        assertParseFails(CommandType.EVENT, "meeting",
                "[event] must contain /from.\n" + example);
        assertParseFails(CommandType.EVENT, "/from 2-12-2019 /to 3-12-2019",
                "The description of [event] cannot be empty.\n" + example);
        assertParseFails(CommandType.EVENT, "meeting /from",
                "[event] must contain /to.\n" + example);
        assertParseFails(CommandType.EVENT, "meeting /from /to 3-12-2019",
                "The from date of [event] cannot be empty.\n" + example);
        assertParseFails(CommandType.EVENT, "meeting /from 2-12-2019 /to",
                "The to date of [event] cannot be empty.\n" + example);
        assertParseFails(CommandType.EVENT, "meeting /from invalid /to 3-12-2019",
                "Invalid [event] date or time. "
                        + "Please use d-M-yyyy with optional HH:mm.\n" + example);
        assertParseFails(CommandType.EVENT,
                "meeting /from 1-12-2019 /from 2-12-2019 /to 3-12-2019",
                "[event] accepts /from only once.\n" + example);
        assertParseFails(CommandType.EVENT,
                "meeting /from 1-12-2019 /to 2-12-2019 /to 3-12-2019",
                "[event] accepts /to only once.\n" + example);
        assertParseFails(CommandType.EVENT, "meeting /to 3-12-2019 /from 2-12-2019",
                "In [event], /from must appear before /to.\n" + example);
    }

    @Test
    public void parse_eventWithInvalidTimeRange_exceptionThrown() {
        String example = "Try: event meeting /from 2-12-2019 14:00 /to 2-12-2019 16:00";
        String expectedMessage = "The end of [event] must be later than its start.\n"
                + "For an event on the same day, please specify both times.\n"
                + example;

        assertParseFails(CommandType.EVENT,
                "meeting /from 3-12-2019 /to 2-12-2019", expectedMessage);
        assertParseFails(CommandType.EVENT,
                "meeting /from 2-12-2019 18:00 /to 2-12-2019 16:00", expectedMessage);
        assertParseFails(CommandType.EVENT,
                "meeting /from 2-12-2019 18:00 /to 2-12-2019 18:00", expectedMessage);
        assertParseFails(CommandType.EVENT,
                "meeting /from 2-12-2019 /to 2-12-2019", expectedMessage);
    }

    @Test
    public void parse_nonTaskCommand_exceptionThrown() {
        assertParseFails(CommandType.LIST, "2-12-2019",
                "[list] does not create a task.");
    }

    private void assertParseFails(CommandType type, String argument, String expectedMessage) {
        TianyiException exception = assertThrows(
                TianyiException.class, () -> parser.parse(type, argument));

        assertEquals(expectedMessage, exception.getMessage());
    }
}
