package task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import joebiden.exception.JoeBidenException;
import joebiden.task.Deadline;
import joebiden.task.Event;
import joebiden.task.Task;
import joebiden.task.TaskList;
import joebiden.task.Todo;

public class TaskListTest {

    @Test
    public void deleteTask_validNumber_removesCorrectTask()
            throws JoeBidenException {
        TaskList tasks = new TaskList();
        tasks.addTask(new Todo("read book"));
        tasks.addTask(new Todo("sleep"));

        Task removed = tasks.deleteTask(1);

        assertEquals("read book", removed.getName());
        assertEquals(1, tasks.size());
        assertEquals("sleep", tasks.getTask(1).getName());
    }

    @Test
    public void deleteTask_invalidNumber_throwsException() {
        TaskList tasks = new TaskList();
        tasks.addTask(new Todo("read book"));

        assertThrows(JoeBidenException.class, () -> tasks.deleteTask(5));
    }

    @Test
    public void getTask_validNumber_returnsCorrectTask()
            throws JoeBidenException {
        TaskList tasks = new TaskList();
        tasks.addTask(new Todo("read book"));
        tasks.addTask(new Todo("sleep"));

        Task task = tasks.getTask(2);

        assertEquals("sleep", task.getName());
    }

    @Test
    public void getTask_invalidNumber_throwsException() {
        TaskList tasks = new TaskList();
        tasks.addTask(new Todo("read book"));

        assertThrows(JoeBidenException.class, () -> tasks.getTask(2));
    }

    @Test
    public void markTask_validNumber_marksTaskDone()
            throws JoeBidenException {
        TaskList tasks = new TaskList();
        tasks.addTask(new Todo("read book"));

        tasks.markTask(1);

        assertTrue(tasks.getTask(1).isDone());
    }

    @Test
    public void markTask_invalidNumber_throwsException() {
        TaskList tasks = new TaskList();

        assertThrows(JoeBidenException.class, () -> tasks.markTask(1));
    }

    @Test
    public void unmarkTask_markedTask_marksTaskNotDone()
            throws JoeBidenException {
        TaskList tasks = new TaskList();
        tasks.addTask(new Todo("read book"));

        tasks.markTask(1);
        tasks.unmarkTask(1);

        assertFalse(tasks.getTask(1).isDone());
    }

    @Test
    public void unmarkTask_invalidNumber_throwsException() {
        TaskList tasks = new TaskList();

        assertThrows(JoeBidenException.class, () -> tasks.unmarkTask(1));
    }

    @Test
    public void listTasks_multipleTasks_returnsAllTasks() {
        TaskList tasks = new TaskList();
        tasks.addTask(new Todo("read book"));
        tasks.addTask(new Todo("sleep"));

        String result = tasks.listTasks();

        assertTrue(result.contains("read book"));
        assertTrue(result.contains("sleep"));
    }

    @Test
    public void findTasks_matchingKeyword_returnsMatchingTask() {
        TaskList tasks = new TaskList();
        tasks.addTask(new Todo("read book"));
        tasks.addTask(new Todo("buy milk"));

        String result = tasks.findTasks("book");

        assertTrue(result.contains("read book"));
        assertFalse(result.contains("buy milk"));
    }

    @Test
    public void findTasks_caseInsensitive_returnsMatchingTask() {
        TaskList tasks = new TaskList();
        tasks.addTask(new Todo("Read Book"));

        String result = tasks.findTasks("BOOK");

        assertTrue(result.contains("Read Book"));
    }

    @Test
    public void getTomorrowReminders_deadlineTomorrow_returnsReminder() {
        TaskList tasks = new TaskList();
        LocalDateTime tomorrow = LocalDate.now()
                .plusDays(1)
                .atTime(23, 59);

        tasks.addTask(new Deadline("submit assignment", tomorrow));

        String reminders = tasks.getTomorrowReminders();

        assertTrue(reminders.contains("submit assignment"));
    }

    @Test
    public void getTomorrowReminders_eventTomorrow_returnsReminder() {
        TaskList tasks = new TaskList();
        LocalDateTime tomorrow = LocalDate.now()
                .plusDays(1)
                .atTime(14, 0);

        Event event = new Event(
                "project meeting",
                tomorrow,
                tomorrow.plusHours(1)
        );

        tasks.addTask(event);

        String reminders = tasks.getTomorrowReminders();

        assertTrue(reminders.contains("project meeting"));
    }

    @Test
    public void getTomorrowReminders_noTasksTomorrow_returnsEmptyString() {
        TaskList tasks = new TaskList();
        LocalDateTime futureDate = LocalDate.now()
                .plusDays(5)
                .atTime(23, 59);

        tasks.addTask(new Deadline("future assignment", futureDate));

        assertEquals("", tasks.getTomorrowReminders());
    }

    @Test
    public void getTomorrowReminders_completedTaskTomorrow_notIncluded()
            throws JoeBidenException {
        TaskList tasks = new TaskList();
        LocalDateTime tomorrow = LocalDate.now()
                .plusDays(1)
                .atTime(23, 59);

        tasks.addTask(new Deadline("submit assignment", tomorrow));
        tasks.markTask(1);

        assertEquals("", tasks.getTomorrowReminders());
    }
}
