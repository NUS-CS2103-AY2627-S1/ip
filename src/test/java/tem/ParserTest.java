package tem;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

/**
 * Tests for {@link Parser}.
 */
public class ParserTest {

    @Test
    public void parseTask_todoCommand_returnsTodo() throws TemException {
        Task task = Parser.parseTask("todo read book");
        assertTrue(task instanceof Todo);
        assertEquals("read book", task.getDescription());
        assertFalse(task.isDone());
    }

    @Test
    public void parseTask_deadlineCommand_returnsDeadlineWithDate() throws TemException {
        Task task = Parser.parseTask("deadline return book /by 2019-10-15");
        assertTrue(task instanceof Deadline);
        Deadline deadline = (Deadline) task;
        assertEquals("return book", deadline.getDescription());
        assertEquals(LocalDate.of(2019, 10, 15), deadline.getBy());
    }

    @Test
    public void parseTask_invalidDeadlineDate_throws() {
        TemException exception = assertThrows(TemException.class, () ->
                Parser.parseTask("deadline return book /by Sunday"));
        assertEquals("Use a date like 2019-10-15 after /by.", exception.getMessage());
    }

    @Test
    public void parseTask_extraWhitespace_normalizesCommand() throws TemException {
        Task task = Parser.parseTask("  todo   read   book  ");

        assertEquals("read book", task.getDescription());
    }

    @Test
    public void parseTask_duplicateDeadlineMarker_throws() {
        TemException exception = assertThrows(TemException.class, () ->
                Parser.parseTask("deadline submit report /by 2019-10-15 /by 2019-10-16"));

        assertEquals("Use /by only once in a deadline.", exception.getMessage());
    }

    @Test
    public void parseTask_duplicateEventMarker_throws() {
        TemException exception = assertThrows(TemException.class, () ->
                Parser.parseTask("event meeting /from 2pm /from 3pm /to 4pm"));

        assertEquals("Use /from and /to only once each in an event.", exception.getMessage());
    }

    @Test
    public void parseTask_eventMarkersOutOfOrder_throws() {
        TemException exception = assertThrows(TemException.class, () ->
                Parser.parseTask("event meeting /to 4pm /from 2pm"));

        assertEquals("Place /from before /to in an event.", exception.getMessage());
    }

    @Test
    public void parseTask_emptyCommand_throws() {
        TemException exception = assertThrows(TemException.class, () -> Parser.parseTask(""));
        assertEquals("Please enter a command.", exception.getMessage());
    }

    @Test
    public void parseTaskIndex_validNumber_returnsZeroBasedIndex() throws TemException {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        tasks.add(new Todo("return book"));

        assertEquals(0, Parser.parseTaskIndex("mark 1", tasks, "mark as done"));
        assertEquals(1, Parser.parseTaskIndex("delete 2", tasks, "delete"));
    }

    @Test
    public void parseTaskIndex_outOfRange_throws() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));

        TemException exception = assertThrows(TemException.class, () ->
                Parser.parseTaskIndex("mark 2", tasks, "mark as done"));
        assertEquals("Choose a task number from 1 to 1.", exception.getMessage());
    }

    @Test
    public void parseTaskIndex_noTasks_throwsHelpfulMessage() {
        TemException exception = assertThrows(TemException.class, () ->
                Parser.parseTaskIndex("mark 1", new TaskList(), "mark as done"));

        assertEquals("There are no tasks to mark as done.", exception.getMessage());
    }

    @Test
    public void parseTaskIndex_multipleArguments_throwsHelpfulMessage() {
        TaskList tasks = new TaskList(new Todo("read book"));

        TemException exception = assertThrows(TemException.class, () ->
                Parser.parseTaskIndex("mark 1 2", tasks, "mark as done"));

        assertEquals("Please provide only one task number to mark as done.", exception.getMessage());
    }

    @Test
    public void parseFindKeyword_validCommand_returnsKeyword() throws TemException {
        assertEquals("book", Parser.parseFindKeyword("find book"));
    }

    @Test
    public void parseFindKeyword_missingKeyword_throws() {
        TemException exception = assertThrows(TemException.class, () -> Parser.parseFindKeyword("find"));
        assertEquals("A find command needs a keyword. Try: find book", exception.getMessage());
    }
}
