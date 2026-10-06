package caesar.task;

import caesar.exception.CaesarException;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TaskListTest {

    @Test
    public void testNewListIsEmpty() {
        TaskList tasks = new TaskList();

        assertTrue(tasks.isEmpty());
        assertEquals(0, tasks.size());
        assertTrue(tasks.areAllDone());
    }

    @Test
    public void testAddGetDeleteAndInsertUseOneBasedPositions() throws CaesarException {
        TaskList tasks = new TaskList();
        ToDo first = new ToDo("First");
        ToDo second = new ToDo("Second");
        ToDo inserted = new ToDo("Inserted");

        tasks.add(first);
        tasks.add(second);

        assertSame(first, tasks.get(1));
        assertSame(second, tasks.delete(2));

        tasks.insert(1, inserted);

        assertSame(inserted, tasks.get(1));
        assertSame(first, tasks.get(2));
        assertEquals(2, tasks.size());
    }

    @Test
    public void testInvalidTaskNumbersAreRejected() throws CaesarException {
        TaskList tasks = new TaskList();
        tasks.add(new ToDo("Task"));

        assertThrows(CaesarException.class, () -> tasks.get(0));
        assertThrows(CaesarException.class, () -> tasks.get(2));
        assertThrows(CaesarException.class, () -> tasks.delete(0));
        assertThrows(CaesarException.class, () -> tasks.insert(3, new ToDo("Task")));
    }

    @Test
    public void testConstructorCopiesInitialTasks() throws CaesarException {
        ArrayList<Task> initialTasks = new ArrayList<>();
        ToDo task = new ToDo("Original");
        initialTasks.add(task);

        TaskList tasks = new TaskList(initialTasks);
        initialTasks.clear();

        assertEquals(1, tasks.size());
        assertSame(task, tasks.get(1));
    }

    @Test
    public void testConstructorRejectsMoreThanMaximumTasks() {
        ArrayList<Task> initialTasks = new ArrayList<>();
        for (int i = 0; i < 101; i++) {
            initialTasks.add(new ToDo("Task " + i));
        }

        CaesarException exception = assertThrows(
                CaesarException.class,
                () -> new TaskList(initialTasks)
        );

        assertEquals("Task file contains more than 100 tasks.", exception.getMessage());
    }

    @Test
    public void testMarkAndUnmarkChangeTaskStatus() throws CaesarException {
        TaskList tasks = new TaskList();
        tasks.add(new ToDo("Task"));

        tasks.mark(1);
        assertTrue(tasks.get(1).isDone());
        assertTrue(tasks.areAllDone());

        tasks.unmark(1);
        assertFalse(tasks.get(1).isDone());
        assertFalse(tasks.areAllDone());
    }

    @Test
    public void testSortedByStatusDoesNotChangeOriginalOrder() throws CaesarException {
        TaskList tasks = new TaskList();
        ToDo completed = new ToDo("Completed");
        ToDo pending = new ToDo("Pending");
        completed.markAsDone();
        tasks.add(completed);
        tasks.add(pending);

        ArrayList<Task> sortedTasks = tasks.sortedByStatus();

        assertSame(pending, sortedTasks.get(0));
        assertSame(completed, sortedTasks.get(1));
        assertSame(completed, tasks.get(1));
        assertSame(pending, tasks.get(2));
    }

    @Test
    public void testReminderTasksSeparateMissedAndUpcomingPendingDatedTasks() throws CaesarException {
        LocalDate today = LocalDate.of(2026, 1, 10);
        Deadline missed = new Deadline("Submit report", "Jan 9 2026");
        Event upcomingEvent = new Event("Project meeting", "Jan 12 2026", "Jan 13 2026");
        Deadline dueToday = new Deadline("Pay bill", "Jan 10 2026");
        Deadline outsideWindow = new Deadline("Plan holiday", "Jan 14 2026");
        Deadline completedMissed = new Deadline("Old completed task", "Jan 8 2026");
        completedMissed.markAsDone();

        TaskList tasks = new TaskList();
        tasks.add(missed);
        tasks.add(upcomingEvent);
        tasks.add(dueToday);
        tasks.add(outsideWindow);
        tasks.add(completedMissed);
        tasks.add(new ToDo("Read a book"));

        TaskList.ReminderTasks reminders = tasks.getReminderTasks(today);

        assertEquals(1, reminders.getMissedTasks().size());
        assertSame(missed, reminders.getMissedTasks().get(0));
        assertEquals(2, reminders.getUpcomingTasks().size());
        assertSame(upcomingEvent, reminders.getUpcomingTasks().get(0));
        assertSame(dueToday, reminders.getUpcomingTasks().get(1));
    }

    @Test
    public void testReminderIncludesPendingSeptemberDeadlineAsMissed() throws CaesarException {
        TaskList tasks = new TaskList();
        Deadline deadline = new Deadline("cs", "Sep 1 2026");
        tasks.add(deadline);

        TaskList.ReminderTasks reminders = tasks.getReminderTasks(LocalDate.of(2026, 9, 18));

        assertEquals(1, reminders.getMissedTasks().size());
        assertSame(deadline, reminders.getMissedTasks().get(0));
    }

    @Test
    public void testClearMissedTasksRemovesOnlyPendingOverdueDatedTasks() throws CaesarException {
        LocalDate today = LocalDate.of(2026, 1, 10);
        Deadline missed = new Deadline("Submit report", "Jan 9 2026");
        Deadline upcoming = new Deadline("Pay bill", "Jan 12 2026");
        Deadline completedMissed = new Deadline("Old completed task", "Jan 8 2026");
        completedMissed.markAsDone();

        TaskList tasks = new TaskList();
        tasks.add(missed);
        tasks.add(upcoming);
        tasks.add(completedMissed);
        tasks.add(new ToDo("Read a book"));

        List<TaskList.RemovedTask> removedTasks = tasks.clearMissedTasks(today);

        assertEquals(1, removedTasks.size());
        assertEquals(3, tasks.size());
        assertSame(upcoming, tasks.get(1));
        assertSame(completedMissed, tasks.get(2));
        assertSame(missed, removedTasks.get(0).getTask());

        tasks.restoreRemovedTasks(removedTasks);
        assertSame(missed, tasks.get(1));
        assertSame(upcoming, tasks.get(2));
    }

    /** Verifies that finding tasks preserves matching tasks and their order. */
    @Test
    public void testFindReturnsTasksContainingDescription() throws CaesarException {
        TaskList tasks = new TaskList();
        ToDo matchingTask = new ToDo("Read a book");
        tasks.add(matchingTask);
        tasks.add(new ToDo("Buy groceries"));
        ToDo secondMatchingTask = new ToDo("book a holiday");
        tasks.add(secondMatchingTask);

        TaskList foundTasks = tasks.find("BOOK");

        assertEquals(2, foundTasks.size());
        assertSame(matchingTask, foundTasks.get(1));
        assertSame(secondMatchingTask, foundTasks.get(2));
    }

    /** Verifies that finding an absent description reports an error. */
    @Test
    public void testFindRejectsDescriptionWithNoMatches() throws CaesarException {
        TaskList tasks = new TaskList();
        tasks.add(new ToDo("Read a book"));

        assertThrows(CaesarException.class, () -> tasks.find("exercise"));
    }
    @Test
    public void testInsertKeepsTaskAtRequestedOneBasedPosition() throws CaesarException {
        TaskList tasks = new TaskList();
        ToDo first = new ToDo("First");
        ToDo second = new ToDo("Second");
        ToDo inserted = new ToDo("Inserted");
        tasks.add(first);
        tasks.add(second);

        tasks.insert(2, inserted);

        assertSame(first, tasks.get(1));
        assertSame(inserted, tasks.get(2));
        assertSame(second, tasks.get(3));
    }
}
