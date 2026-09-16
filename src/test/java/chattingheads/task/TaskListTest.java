package chattingheads.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;

import chattingheads.exception.InvalidInputException;
import chattingheads.exception.InvalidTaskNumberException;
import chattingheads.exception.InvalidTaskTypeException;

public class TaskListTest {

    @Test
    public void isEmpty_newTaskList_returnsTrue() {
        assertTrue(new TaskList().isEmpty());
    }

    @Test
    public void add_validTask_taskAdded() {
        TaskList taskList = new TaskList();
        Task task = new Todo("test");

        taskList.add(task);

        assertEquals(1, taskList.size());
        assertEquals(task, taskList.get(0));
        assertFalse(taskList.isEmpty());
    }

    @Test
    public void delete_validIndex_correctTaskDeleted() throws InvalidTaskNumberException {
        TaskList taskList = new TaskList();
        Task task = new Todo("test");
        Task task1 = new Todo("test1");
        taskList.add(task);
        taskList.add(task1);

        Task removed = taskList.delete(0);

        assertEquals(task, removed);
        assertEquals(1, taskList.size());
        assertEquals(task1, taskList.get(0));
    }

    @Test
    public void markTask_validIndex_correctTaskMarked() throws InvalidTaskNumberException {
        TaskList taskList = new TaskList();
        Task task = new Todo("test");
        taskList.add(task);

        taskList.mark(0);

        assertTrue(task.isDone());
    }

    @Test
    public void unmarkTask_validIndex_correctTaskUnmarked() throws InvalidTaskNumberException {
        TaskList taskList = new TaskList();
        Task task = new Todo("test");
        task.mark();
        taskList.add(task);

        taskList.unmark(0);

        assertFalse(task.isDone());
    }

    @Test
    public void markTask_invalidUpperIndex_throwsInvalidTaskNumberException() {
        TaskList taskList = new TaskList();
        taskList.add(new Todo("test"));

        assertThrows(InvalidTaskNumberException.class, () -> taskList.mark(1));
    }

    @Test
    public void markTask_negativeIndex_throwsInvalidTaskNumberException() {
        TaskList taskList = new TaskList();
        taskList.add(new Todo("test"));

        assertThrows(InvalidTaskNumberException.class, () -> taskList.mark(-1));
    }

    @Test
    public void unmarkTask_invalidIndex_throwsInvalidTaskNumberException() {
        TaskList taskList = new TaskList();
        assertThrows(InvalidTaskNumberException.class, () -> taskList.unmark(0));
    }

    @Test
    public void delete_invalidIndex_throwsInvalidTaskNumberException() {
        TaskList taskList = new TaskList();
        assertThrows(InvalidTaskNumberException.class, () -> taskList.delete(0));
    }

    @Test
    public void findIndices_matchingSubstring_returnsOriginalIndices() {
        TaskList taskList = new TaskList();
        taskList.add(new Todo("read book"));
        taskList.add(new Todo("buy milk"));
        taskList.add(new Todo("read notes"));

        List<Integer> indices = taskList.findIndices("read");

        assertEquals(List.of(0, 2), indices);
    }

    @Test
    public void findIndices_noMatchingTasks_returnsEmptyList() {
        TaskList taskList = new TaskList();
        taskList.add(new Todo("read book"));

        assertTrue(taskList.findIndices("sleep").isEmpty());
    }

    @Test
    public void findIndices_keywordWithinWord_returnsMatchingIndex() {
        TaskList taskList = new TaskList();
        taskList.add(new Todo("read textbook"));

        assertEquals(List.of(0), taskList.findIndices("text"));
    }

    @Test
    public void rescheduleDeadline_validDeadline_deadlineChanged()
            throws InvalidTaskNumberException, InvalidTaskTypeException {
        TaskList taskList = new TaskList();
        Deadline deadline = new Deadline("submit report", LocalDateTime.of(2026, 8, 28, 18, 0));
        taskList.add(deadline);

        taskList.rescheduleDeadline(0, LocalDateTime.of(2026, 8, 30, 20, 0));

        assertEquals("D,submit report,false,2026-08-30T20:00", deadline.toCsv());
    }

    @Test
    public void rescheduleDeadline_wrongTaskType_throwsInvalidTaskTypeException() {
        TaskList taskList = new TaskList();
        taskList.add(new Todo("test"));

        assertThrows(InvalidTaskTypeException.class, () -> taskList.rescheduleDeadline(
                0,
                LocalDateTime.of(2026, 8, 30, 20, 0)
        ));
    }

    @Test
    public void rescheduleDeadline_invalidIndex_throwsInvalidTaskNumberException() {
        TaskList taskList = new TaskList();

        assertThrows(InvalidTaskNumberException.class, () -> taskList.rescheduleDeadline(
                0,
                LocalDateTime.of(2026, 8, 30, 20, 0)
        ));
    }

    @Test
    public void rescheduleEvent_validEvent_eventChanged()
            throws InvalidTaskNumberException, InvalidTaskTypeException, InvalidInputException {
        TaskList taskList = new TaskList();
        Event event = new Event(
                "meeting",
                LocalDateTime.of(2026, 8, 28, 18, 0),
                LocalDateTime.of(2026, 8, 28, 20, 0));
        taskList.add(event);

        taskList.rescheduleEvent(
                0,
                LocalDateTime.of(2026, 8, 30, 10, 0),
                LocalDateTime.of(2026, 8, 30, 12, 0));

        assertEquals("E,meeting,false,2026-08-30T10:00,2026-08-30T12:00", event.toCsv());
    }

    @Test
    public void rescheduleEvent_wrongTaskType_throwsInvalidTaskTypeException() {
        TaskList taskList = new TaskList();
        taskList.add(new Todo("test"));

        assertThrows(InvalidTaskTypeException.class, () -> taskList.rescheduleEvent(
                0,
                LocalDateTime.of(2026, 8, 30, 10, 0),
                LocalDateTime.of(2026, 8, 30, 12, 0)
        ));
    }

    @Test
    public void rescheduleEvent_invalidIndex_throwsInvalidTaskNumberException() {
        TaskList taskList = new TaskList();

        assertThrows(InvalidTaskNumberException.class, () -> taskList.rescheduleEvent(
                0,
                LocalDateTime.of(2026, 8, 30, 10, 0),
                LocalDateTime.of(2026, 8, 30, 12, 0)
        ));
    }

    @Test
    public void rescheduleEvent_invalidTimeRange_throwsInvalidInputExceptionAndKeepsOriginalTimes()
            throws InvalidInputException {
        TaskList taskList = new TaskList();
        Event event = new Event(
                "meeting",
                LocalDateTime.of(2026, 8, 28, 18, 0),
                LocalDateTime.of(2026, 8, 28, 20, 0));
        taskList.add(event);

        assertThrows(InvalidInputException.class, () -> taskList.rescheduleEvent(
                0,
                LocalDateTime.of(2026, 8, 30, 12, 0),
                LocalDateTime.of(2026, 8, 30, 10, 0)
        ));
        assertEquals("E,meeting,false,2026-08-28T18:00,2026-08-28T20:00", event.toCsv());
    }
}
