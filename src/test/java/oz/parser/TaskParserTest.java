package oz.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import oz.exception.OzException;
import oz.task.Deadline;
import oz.task.Event;
import oz.task.RecurringEvent;
import oz.task.Task;
import oz.task.ToDo;

/**
 * Tests task-creation command parsing.
 */
public class TaskParserTest {

    @Test
    public void parseTodo_validArguments_returnsTodo() throws OzException {
        Task task = TaskParser.parseTodo("read book");

        assertInstanceOf(ToDo.class, task);
        assertEquals("[T][ ] read book", task.toString());
    }

    @Test
    public void parseDeadline_validArguments_returnsDeadline() throws OzException {
        Task task = TaskParser.parseDeadline("return book /by 2026-12-31");

        assertInstanceOf(Deadline.class, task);
        assertEquals("[D][ ] return book (by: Dec 31 2026)", task.toString());
    }

    @Test
    public void parseEvent_validArguments_returnsEvent() throws OzException {
        Task task = TaskParser.parseEvent(
                "project meeting /from 2026-10-10 1400 /to 2026-10-10 1600");

        assertInstanceOf(Event.class, task);
        assertEquals("[E][ ] project meeting (from: Oct 10 2026, 2pm to: Oct 10 2026, 4pm)",
                task.toString());
    }

    @Test
    public void parseRecurringEvent_validArguments_returnsRecurringEvent() throws OzException {
        Task task = TaskParser.parseRecurringEvent(
                "tutorial /on 2026-10-02 /start 1000 /end 1200 /every 2 weeks "
                        + "/until 2026-12-31");

        assertInstanceOf(RecurringEvent.class, task);
        assertEquals("[R] tutorial (from: Oct 02 2026, 10am to: Oct 02 2026, 12pm; "
                + "repeats: every 2 weeks until Dec 31 2026)", task.toString());
    }

    @Test
    public void parseTask_duplicateRequiredFlag_exceptionThrown() {
        OzException exception = assertThrows(OzException.class, () ->
                TaskParser.parseEvent(
                        "meeting /from 2026-10-10 /from 2026-10-11 /to 2026-10-12"));

        assertEquals("Duplicate '/from' parameter detected.", exception.getMessage());
    }
}
