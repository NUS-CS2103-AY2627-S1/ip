package mrchatbot.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

public class RecurringTaskTest {
    @Test
    public void refresh_beforeReset_preservesCompletionAndDate() {
        RecurringTask task = new RecurringTask("meeting", Recurrence.WEEK, LocalDate.of(2026, 9, 21));
        task.markAsDone();

        assertFalse(task.refresh(LocalDate.of(2026, 9, 20)));
        assertTrue(task.isDone());
        assertEquals(LocalDate.of(2026, 9, 21), task.getNextResetDate());
    }

    @Test
    public void refresh_onReset_startsNextOccurrenceOnlyOnce() {
        RecurringTask task = new RecurringTask("meeting", Recurrence.WEEK, LocalDate.of(2026, 9, 21));
        task.markAsDone();

        assertTrue(task.refresh(LocalDate.of(2026, 9, 21)));
        assertFalse(task.isDone());
        assertEquals(LocalDate.of(2026, 9, 28), task.getNextResetDate());
        task.markAsDone();
        assertFalse(task.refresh(LocalDate.of(2026, 9, 21)));
        assertTrue(task.isDone());
    }

    @Test
    public void refresh_severalMissedWeeks_preservesScheduledWeekday() {
        RecurringTask task = new RecurringTask("meeting", Recurrence.WEEK, LocalDate.of(2026, 9, 21));
        task.markAsDone();

        assertTrue(task.refresh(LocalDate.of(2026, 10, 8)));
        assertFalse(task.isDone());
        assertEquals(LocalDate.of(2026, 10, 12), task.getNextResetDate());
    }

    @Test
    public void refresh_incompleteDailyTaskAcrossLeapDay_advancesSchedule() {
        RecurringTask task = new RecurringTask("exercise", Recurrence.DAY, LocalDate.of(2028, 2, 28));

        assertTrue(task.refresh(LocalDate.of(2028, 2, 29)));
        assertFalse(task.isDone());
        assertEquals(LocalDate.of(2028, 3, 1), task.getNextResetDate());
    }

    @Test
    public void refreshRecurringTasks_multipleExpiredTasks_refreshesEveryTask() {
        RecurringTask first = new RecurringTask("exercise", Recurrence.DAY, LocalDate.of(2026, 9, 21));
        RecurringTask second = new RecurringTask("meeting", Recurrence.WEEK, LocalDate.of(2026, 9, 21));
        Todo todo = new Todo("book");
        first.markAsDone();
        second.markAsDone();
        todo.markAsDone();
        TaskList tasks = new TaskList(first, second, todo);

        assertTrue(tasks.refreshRecurringTasks(LocalDate.of(2026, 9, 22)));
        assertFalse(first.isDone());
        assertFalse(second.isDone());
        assertTrue(todo.isDone());
        assertEquals(3, tasks.size());
        assertFalse(tasks.refreshRecurringTasks(LocalDate.of(2026, 9, 22)));
    }
}
