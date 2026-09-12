package chin.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import chin.task.Deadline;
import chin.task.Event;
import chin.task.Task;
import chin.task.Todo;

public class ParserTest {

    @Test
    public void parseNewTask_todo_returnsTodo() throws ChinException {
        Task t = Parser.parseNewTask(Command.TODO, "todo read book");
        assertTrue(t instanceof Todo);
        assertEquals("[T][ ] read book", t.toString());
    }

    @Test
    public void parseNewTask_todoEmpty_throws() {
        assertThrows(ChinException.class, () -> Parser.parseNewTask(Command.TODO, "todo "));
    }

    @Test
    public void parseNewTask_deadlineValidDate_returnsDeadline() throws ChinException {
        Task t = Parser.parseNewTask(Command.DEADLINE, "deadline return /by 2020-01-01");
        assertTrue(t instanceof Deadline);
        assertEquals("[D][ ] return (by: Jan 1 2020)", t.toString());
    }

    @Test
    public void parseNewTask_deadlineMissingBy_throws() {
        assertThrows(ChinException.class, () ->
                Parser.parseNewTask(Command.DEADLINE, "deadline return"));
    }

    @Test
    public void parseNewTask_deadlineBadDate_throws() {
        assertThrows(ChinException.class, () ->
                Parser.parseNewTask(Command.DEADLINE, "deadline return /by tomorrow"));
    }

    @Test
    public void parseNewTask_eventValid_returnsEvent() throws ChinException {
        Task t = Parser.parseNewTask(Command.EVENT, "event meet /from 2pm /to 4pm");
        assertTrue(t instanceof Event);
        assertEquals("[E][ ] meet (from: 2pm to: 4pm)", t.toString());
    }

    @Test
    public void parseIndex_valid_returnsZeroBased() throws ChinException {
        assertEquals(0, Parser.parseIndex("1", 3));
        assertEquals(2, Parser.parseIndex("3", 3));
    }

    @Test
    public void parseIndex_outOfRange_throws() {
        assertThrows(ChinException.class, () -> Parser.parseIndex("0", 3));
        assertThrows(ChinException.class, () -> Parser.parseIndex("4", 3));
    }

    @Test
    public void parseIndex_notInteger_throws() {
        assertThrows(ChinException.class, () -> Parser.parseIndex("abc", 3));
    }
}
