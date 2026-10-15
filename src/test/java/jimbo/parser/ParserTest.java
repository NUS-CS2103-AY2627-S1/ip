package jimbo.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import jimbo.exception.JimboException;
import jimbo.task.Deadline;
import jimbo.task.Event;
import jimbo.task.Task;
import jimbo.task.TaskList;
import jimbo.task.Todo;

/**
 * Tests {@link Parser}, which turns raw user input into {@link Task}s and
 * task-list indices. Each parse method is exercised on its happy path plus
 * every distinct validation failure implied by its branches.
 */
public class ParserTest {
    private final Parser parser = new Parser();

    // ---- parseTodo ----

    @Test
    public void parseTodo_validDescription_returnsTodo() throws JimboException {
        Task task = parser.parseTodo("read book");

        assertTrue(task instanceof Todo);
        assertEquals("read book", task.getDescription());
        assertEquals("T | 0 | read book", task.toSaveFormat());
    }

    @Test
    public void parseTodo_emptyDescription_exceptionThrown() {
        assertThrows(JimboException.class, () -> parser.parseTodo(""));
    }

    // ---- parseDeadline ----

    @Test
    public void parseDeadline_validInput_returnsDeadline() throws JimboException {
        Task task = parser.parseDeadline("return book /by 2/12/2019 1800");

        assertTrue(task instanceof Deadline);
        assertEquals("return book", task.getDescription());
        assertEquals("D | 0 | return book | 2019-12-02T18:00:00", task.toSaveFormat());
    }

    @Test
    public void parseDeadline_emptyDescription_exceptionThrown() {
        assertThrows(JimboException.class, () -> parser.parseDeadline(""));
    }

    @Test
    public void parseDeadline_missingByKeyword_exceptionThrown() {
        assertThrows(JimboException.class, () -> parser.parseDeadline("return book"));
    }

    @Test
    public void parseDeadline_blankDescriptionBeforeBy_exceptionThrown() {
        assertThrows(JimboException.class, () -> parser.parseDeadline(" /by 2/12/2019 1800"));
    }

    @Test
    public void parseDeadline_blankByTime_exceptionThrown() {
        assertThrows(JimboException.class, () -> parser.parseDeadline("return book /by "));
    }

    @Test
    public void parseDeadline_invalidByFormat_exceptionThrown() {
        assertThrows(JimboException.class, () -> parser.parseDeadline("return book /by tomorrow"));
    }

    // ---- parseEvent ----

    @Test
    public void parseEvent_validInput_returnsEvent() throws JimboException {
        Task task = parser.parseEvent("project meeting /from 2/12/2019 1400 /to 2/12/2019 1600");

        assertTrue(task instanceof Event);
        assertEquals("project meeting", task.getDescription());
        assertEquals("E | 0 | project meeting | 2019-12-02T14:00:00 | 2019-12-02T16:00:00", task.toSaveFormat());
    }

    @Test
    public void parseEvent_emptyDescription_exceptionThrown() {
        assertThrows(JimboException.class, () -> parser.parseEvent(""));
    }

    @Test
    public void parseEvent_missingFromKeyword_exceptionThrown() {
        assertThrows(JimboException.class, () -> parser.parseEvent("project meeting"));
    }

    @Test
    public void parseEvent_blankDescriptionBeforeFrom_exceptionThrown() {
        assertThrows(JimboException.class, () -> parser.parseEvent(" /from Mon 2pm /to Mon 4pm"));
    }

    @Test
    public void parseEvent_missingToKeyword_exceptionThrown() {
        assertThrows(JimboException.class, () -> parser.parseEvent("project meeting /from Mon 2pm"));
    }

    @Test
    public void parseEvent_blankFromTime_exceptionThrown() {
        assertThrows(JimboException.class, () -> parser.parseEvent("project meeting /from  /to Mon 4pm"));
    }

    @Test
    public void parseEvent_blankToTime_exceptionThrown() {
        assertThrows(JimboException.class, () -> parser.parseEvent("project meeting /from Mon 2pm /to "));
    }

    // ---- parseFind ----

    @Test
    public void parseFind_validKeyword_returnsKeyword() throws JimboException {
        assertEquals("book", parser.parseFind("book"));
    }

    @Test
    public void parseFind_emptyKeyword_exceptionThrown() {
        assertThrows(JimboException.class, () -> parser.parseFind(""));
    }

    // ---- parseTaskIndex ----

    @Test
    public void parseTaskIndex_validIndex_returnsZeroBasedIndex() throws JimboException {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("task 1"));
        tasks.add(new Todo("task 2"));

        assertEquals(0, parser.parseTaskIndex(tasks, "1", "mark"));
    }

    @Test
    public void parseTaskIndex_blankIndexArg_exceptionThrown() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("task 1"));

        assertThrows(JimboException.class, () -> parser.parseTaskIndex(tasks, "  ", "mark"));
    }

    @Test
    public void parseTaskIndex_nonNumericIndexArg_exceptionThrown() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("task 1"));

        assertThrows(JimboException.class, () -> parser.parseTaskIndex(tasks, "abc", "mark"));
    }

    @Test
    public void parseTaskIndex_indexTooLow_exceptionThrown() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("task 1"));

        assertThrows(JimboException.class, () -> parser.parseTaskIndex(tasks, "0", "mark"));
    }

    @Test
    public void parseTaskIndex_indexTooHigh_exceptionThrown() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("task 1"));

        assertThrows(JimboException.class, () -> parser.parseTaskIndex(tasks, "5", "mark"));
    }
}
