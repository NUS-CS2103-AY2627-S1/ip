package tianyi.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import tianyi.task.Deadline;
import tianyi.task.Event;
import tianyi.task.Task;
import tianyi.task.ToDo;

/**
 * Tests conversion of stored task records into task objects.
 */
public class DataParserTest {
    private final DataParser parser = new DataParser();

    @Test
    public void parse_incompleteTodo_returnsIncompleteTodo()
            throws StorageException {
        Task task = parser.parse("T | 0 | read book");

        assertInstanceOf(ToDo.class, task);
        assertEquals("T | 0 | read book", task.getData());
    }

    @Test
    public void parse_completedTodo_returnsCompletedTodo()
            throws StorageException {
        Task task = parser.parse("T | 1 | read book");

        assertInstanceOf(ToDo.class, task);
        assertEquals("[T][X] read book", task.toString());
    }

    @Test
    public void parse_deadlineWithDateAndTime_returnsDeadline()
            throws StorageException {
        Task task = parser.parse("D | 0 | submit report | 2-12-2019 18:00");

        assertInstanceOf(Deadline.class, task);
        assertEquals("D | 0 | submit report | 2-12-2019 18:00", task.getData());
    }

    @Test
    public void parse_completedEvent_returnsCompletedEvent()
            throws StorageException {
        Task task = parser.parse(
                "E | 1 | workshop | 2-12-2019 09:00 | 3-12-2019 17:00");

        assertInstanceOf(Event.class, task);
        assertEquals("E | 1 | workshop | 2-12-2019 09:00 | 3-12-2019 17:00",
                task.getData());
    }

    @Test
    public void parse_variableWhitespaceAroundSeparators_returnsTask()
            throws StorageException {
        Task task = parser.parse("D|0  |  submit report| 2-12-2019");

        assertEquals("D | 0 | submit report | 2-12-2019", task.getData());
    }

    @Test
    public void parse_tooFewFields_exceptionThrown() {
        assertParseFails("T | 0", "Invalid task data: T | 0");
    }

    @Test
    public void parse_unknownTaskType_exceptionThrown() {
        assertParseFails("X | 0 | unknown", "Unknown task type: X | 0 | unknown");
    }

    @Test
    public void parse_wrongFieldCountForKnownType_exceptionThrown() {
        assertParseFails("T | 0 | todo | extra",
                "Invalid todo data: T | 0 | todo | extra");
        assertParseFails("D | 0 | deadline",
                "Invalid deadline data: D | 0 | deadline");
        assertParseFails("E | 0 | event | 2-12-2019",
                "Invalid event data: E | 0 | event | 2-12-2019");
    }

    @Test
    public void parse_invalidTaskStatus_exceptionThrown() {
        assertParseFails("T | 2 | read book", "Invalid task status: 2");
    }

    @Test
    public void parse_invalidDeadlineDate_exceptionThrown() {
        assertParseFails("D | 0 | submit report | 31-2-2019",
                "Invalid date and time in deadline data: 31-2-2019");
    }

    @Test
    public void parse_invalidEventDate_exceptionThrown() {
        String data = "E | 0 | workshop | invalid | 3-12-2019";

        assertParseFails(data, "Invalid date and time in event data: " + data);
    }

    @Test
    public void parse_eventWithInvalidTimeRange_exceptionThrown() {
        String reversedData = "E | 0 | workshop | 3-12-2019 | 2-12-2019";
        String equalData = "E | 0 | workshop | 2-12-2019 09:00 | 2-12-2019 09:00";

        assertParseFails(reversedData,
                "Event start must be before its end in event data: " + reversedData);
        assertParseFails(equalData,
                "Event start must be before its end in event data: " + equalData);
    }

    private void assertParseFails(String data, String expectedMessage) {
        StorageException exception = assertThrows(
                StorageException.class, () -> parser.parse(data));

        assertEquals(expectedMessage, exception.getMessage());
    }
}
