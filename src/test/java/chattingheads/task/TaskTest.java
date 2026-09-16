package chattingheads.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import chattingheads.exception.InvalidInputException;

public class TaskTest {

    @Test
    public void fromCsv_validTodo_returnsCorrectTodo() throws InvalidInputException {
        Task task = Task.fromCsv("T,read book,false");

        assertInstanceOf(Todo.class, task);
        assertEquals("read book", task.getDescription());
        assertFalse(task.isDone());
    }

    @Test
    public void fromCsv_validMarkedTodo_returnsMarkedTodo() throws InvalidInputException {
        Task task = Task.fromCsv("T,read book,true");
        assertTrue(task.isDone());
    }

    @Test
    public void fromCsv_validDeadline_returnsCorrectDeadline() throws InvalidInputException {
        Task task = Task.fromCsv("D,submit report,false,2026-08-28T18:00");

        assertInstanceOf(Deadline.class, task);
        assertEquals("D,submit report,false,2026-08-28T18:00", task.toCsv());
    }

    @Test
    public void fromCsv_validEvent_returnsCorrectEvent() throws InvalidInputException {
        Task task = Task.fromCsv("E,meeting,true,2026-08-28T18:00,2026-08-28T20:00");

        assertInstanceOf(Event.class, task);
        assertEquals("E,meeting,true,2026-08-28T18:00,2026-08-28T20:00", task.toCsv());
    }

    @Test
    public void fromCsv_todoDescriptionContainsComma_preservesDescription() throws InvalidInputException {
        Task task = Task.fromCsv("T,buy milk,bread,false");
        assertEquals("buy milk,bread", task.getDescription());
    }

    @Test
    public void fromCsv_deadlineDescriptionContainsComma_preservesDescription() throws InvalidInputException {
        Task task = Task.fromCsv("D,submit report,draft,false,2026-08-28T18:00");
        assertEquals("submit report,draft", task.getDescription());
    }

    @Test
    public void fromCsv_eventDescriptionContainsComma_preservesDescription() throws InvalidInputException {
        Task task = Task.fromCsv("E,team,meeting,false,2026-08-28T18:00,2026-08-28T20:00");
        assertEquals("team,meeting", task.getDescription());
    }

    @Test
    public void fromCsv_unknownTaskType_throwsInvalidInputException() {
        assertThrows(InvalidInputException.class, () -> Task.fromCsv("X,test,false"));
    }

    @Test
    public void fromCsv_todoTooFewFields_throwsInvalidInputException() {
        assertThrows(InvalidInputException.class, () -> Task.fromCsv("T,test"));
    }

    @Test
    public void fromCsv_deadlineTooFewFields_throwsInvalidInputException() {
        assertThrows(InvalidInputException.class, () -> Task.fromCsv("D,test,false"));
    }

    @Test
    public void fromCsv_eventTooFewFields_throwsInvalidInputException() {
        assertThrows(InvalidInputException.class, () -> Task.fromCsv("E,test,false,2026-08-28T18:00"));
    }

    @Test
    public void fromCsv_blankDescription_throwsInvalidInputException() {
        assertThrows(InvalidInputException.class, () -> Task.fromCsv("T,   ,false"));
    }

    @Test
    public void fromCsv_invalidBoolean_throwsInvalidInputException() {
        assertThrows(InvalidInputException.class, () -> Task.fromCsv("T,test,yes"));
    }

    @Test
    public void fromCsv_invalidDeadlineDateTime_throwsInvalidInputException() {
        assertThrows(InvalidInputException.class, () -> Task.fromCsv("D,test,false,not-a-date"));
    }

    @Test
    public void fromCsv_invalidEventDateTime_throwsInvalidInputException() {
        assertThrows(InvalidInputException.class, () -> Task.fromCsv(
                "E,test,false,2026-08-28T18:00,not-a-date"
        ));
    }

    @Test
    public void fromCsv_eventEndNotAfterStart_throwsInvalidInputException() {
        assertThrows(InvalidInputException.class, () -> Task.fromCsv(
                "E,test,false,2026-08-28T20:00,2026-08-28T18:00"
        ));
    }

    @Test
    public void mark_unmarkedTask_taskBecomesDone() {
        Task task = new Todo("test");
        task.mark();
        assertTrue(task.isDone());
    }

    @Test
    public void unmark_markedTask_taskBecomesNotDone() {
        Task task = new Todo("test", true);
        task.unmark();
        assertFalse(task.isDone());
    }

    @Test
    public void getDescription_validTask_returnsDescription() {
        Task task = new Todo("read book");
        assertEquals("read book", task.getDescription());
    }
}
