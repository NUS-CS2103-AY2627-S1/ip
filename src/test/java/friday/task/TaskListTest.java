package friday.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;

import friday.FridayException;

/**
 * Tests task list operations that change or expose the stored tasks.
 */
class TaskListTest {

    /**
     * Tests that adding multiple tasks updates the task count.
     */
    @Test
    void addAndSize_multipleTasks_tracksNumberOfTasks() {
        TaskList tasks = new TaskList();

        tasks.add(new Todo("read book"));
        tasks.add(new Todo("write notes"));

        assertEquals(2, tasks.size());
    }

    /**
     * Tests that deleting a task removes and returns only the selected task.
     *
     * @throws FridayException if task access fails unexpectedly
     */
    @Test
    void delete_validIndex_removesAndReturnsOnlySelectedTask() throws FridayException {
        TaskList tasks = new TaskList();
        Todo firstTask = new Todo("read book");
        Todo secondTask = new Todo("write notes");
        Todo thirdTask = new Todo("submit work");
        tasks.add(firstTask);
        tasks.add(secondTask);
        tasks.add(thirdTask);

        Task deletedTask = tasks.delete(1);

        assertSame(secondTask, deletedTask);
        assertEquals(2, tasks.size());
        assertSame(firstTask, tasks.get(0));
        assertSame(thirdTask, tasks.get(1));
    }

    /**
     * Tests that finding by keyword returns tasks with matching descriptions.
     *
     * @throws FridayException if task access fails unexpectedly
     */
    @Test
    void find_matchingKeyword_returnsTasksWithKeywordInDescription() throws FridayException {
        TaskList tasks = new TaskList();
        Todo firstTask = new Todo("read book");
        Todo secondTask = new Todo("write notes");
        Todo thirdTask = new Todo("return book");
        tasks.add(firstTask);
        tasks.add(secondTask);
        tasks.add(thirdTask);

        TaskList matchingTasks = tasks.find("book");

        assertEquals(2, matchingTasks.size());
        assertSame(firstTask, matchingTasks.get(0));
        assertSame(thirdTask, matchingTasks.get(1));
    }

    /**
     * Tests that the list returned by asList cannot modify the task list.
     */
    @Test
    void asList_returnedListCannotModifyTaskList() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));

        List<Task> readOnlyTasks = tasks.asList();

        assertThrows(UnsupportedOperationException.class, () -> readOnlyTasks.add(new Todo("write notes")));
        assertEquals(1, tasks.size());
    }

    /**
     * Tests that sorting uses date and time values and places undated tasks last.
     *
     * @throws FridayException if task creation fails unexpectedly
     */
    @Test
    void sortByReminderDateTime_mixedTasks_sortsDatedTasksFirst() throws FridayException {
        TaskList tasks = new TaskList();
        Deadline laterDeadline = new Deadline("later task", "5/12/2025 1800");
        Todo todo = new Todo("floating task");
        Deadline earlierDeadline = new Deadline("earlier task", "2/12/2025 1800");
        tasks.add(laterDeadline);
        tasks.add(todo);
        tasks.add(earlierDeadline);

        tasks.sortByReminderDateTime();

        assertSame(earlierDeadline, tasks.get(0));
        assertSame(laterDeadline, tasks.get(1));
        assertSame(todo, tasks.get(2));
    }

    /**
     * Tests that upcoming reminders include only dated tasks inside the period.
     *
     * @throws FridayException if task access or creation fails unexpectedly
     */
    @Test
    void getUpcomingReminders_mixedTasks_returnsOnlyTasksWithinPeriod() throws FridayException {
        TaskList tasks = new TaskList();
        Deadline dueSoon = new Deadline("due soon", "2/12/2025 1800");
        Deadline dueLater = new Deadline("due later", "5/12/2025 1800");
        tasks.add(dueLater);
        tasks.add(new Todo("floating task"));
        tasks.add(dueSoon);

        TaskList reminders = tasks.getUpcomingReminders(
                LocalDateTime.of(2025, 12, 1, 0, 0),
                LocalDateTime.of(2025, 12, 3, 0, 0));

        assertEquals(1, reminders.size());
        assertSame(dueSoon, reminders.get(0));
    }

    @Test
    void markAndUnmark_validIndex_updatesSelectedTask() throws FridayException {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));

        assertEquals("X", tasks.mark(0).getStatusIcon());
        assertEquals(" ", tasks.unmark(0).getStatusIcon());
    }

    @Test
    void getDeleteAndRepeat_invalidIndex_throwFridayException() {
        TaskList tasks = new TaskList();

        assertThrows(FridayException.class, () -> tasks.get(-1));
        assertThrows(FridayException.class, () -> tasks.get(0));
        assertThrows(FridayException.class, () -> tasks.delete(0));
        assertThrows(FridayException.class, () -> tasks.setRepeatFrequency(0, RepeatFrequency.DAILY));
    }

    @Test
    void getTasksWithReminders_mixedTasks_returnsOnlyDatedTasks() throws FridayException {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        Deadline deadline = new Deadline("submit report", "2/12/2025 1800");
        tasks.add(deadline);

        TaskList reminders = tasks.getTasksWithReminders();

        assertEquals(1, reminders.size());
        assertSame(deadline, reminders.get(0));
    }
}
