package jeff.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.ArrayList;

import org.junit.jupiter.api.Test;

public class TaskListTest {

    @Test
    public void find_matchingKeyword_returnsMatchingTasks() {
        TaskList tasks = new TaskList();
        tasks.addTask(new Todo("read book"));
        tasks.addTask(new Todo("buy groceries"));
        tasks.addTask(new Deadline(
                "return book",
                LocalDate.of(2026, 9, 1)));

        ArrayList<Task> matchingTasks = tasks.findTasks("BOOK");

        assertEquals(2, matchingTasks.size());
        assertEquals(
                "read book",
                matchingTasks.get(0).getDescription());
        assertEquals(
                "return book",
                matchingTasks.get(1).getDescription());
    }

    @Test
    public void find_noMatchingKeyword_returnsEmptyList() {
        TaskList tasks = new TaskList();
        tasks.addTask(new Todo("read book"));

        ArrayList<Task> matchingTasks = tasks.findTasks("groceries");

        assertEquals(0, matchingTasks.size());
    }

    @Test
    public void updateTask_todoDescription_preservesStatusAndPosition() {
        TaskList tasks = new TaskList();
        Todo todo = new Todo("read book");
        todo.markAsDone();
        Deadline deadline = new Deadline(
                "return book", LocalDate.of(2026, 9, 20));
        tasks.addTask(todo);
        tasks.addTask(deadline);

        Task updatedTask = tasks.updateTask(
                0, UpdateField.DESCRIPTION, "read novel");

        assertEquals(2, tasks.size());
        assertEquals("read novel", updatedTask.getDescription());
        assertTrue(updatedTask.isDone());
        assertEquals(deadline, tasks.getTask(1));
    }

    @Test
    public void updateTask_deadlineDate_preservesDescription() {
        TaskList tasks = new TaskList();
        tasks.addTask(new Deadline(
                "return book", LocalDate.of(2026, 9, 20)));

        Task updatedTask = tasks.updateTask(
                0, UpdateField.DUE_DATE, "2026-10-01");
        Deadline updatedDeadline = (Deadline) updatedTask;

        assertEquals(
                "return book",
                updatedDeadline.getDescription());
        assertEquals(
                LocalDate.of(2026, 10, 1),
                updatedDeadline.getDueDate());
    }

    @Test
    public void updateTask_eventEndTime_preservesOtherDetails() {
        TaskList tasks = new TaskList();
        tasks.addTask(
                new Event("meeting", "1400", "1600"));

        Task updatedTask = tasks.updateTask(
                0, UpdateField.END_TIME, "1800");
        Event updatedEvent = (Event) updatedTask;

        assertEquals("meeting", updatedEvent.getDescription());
        assertEquals("1400", updatedEvent.getStartTime());
        assertEquals("1800", updatedEvent.getEndTime());
    }

    @Test
    public void updateTask_incompatibleField_exceptionThrown() {
        TaskList tasks = new TaskList();
        tasks.addTask(new Todo("read book"));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class, (
                ) -> tasks.updateTask(
                        0,
                        UpdateField.DUE_DATE,
                        "2026-10-01"));

        assertEquals(
                "A todo can only update /description.",
                exception.getMessage());
    }

    @Test
    public void updateTask_invalidDeadlineDate_exceptionThrown() {
        TaskList tasks = new TaskList();
        tasks.addTask(new Deadline(
                "return book", LocalDate.of(2026, 9, 20)));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class, (
                ) -> tasks.updateTask(
                        0, UpdateField.DUE_DATE, "tomorrow"));

        assertEquals(
                "Please enter the date in YYYY-MM-DD format.",
                exception.getMessage());
    }
}
