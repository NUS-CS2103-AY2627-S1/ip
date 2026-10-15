package shai.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.jupiter.api.Test;

/** Tests task-list storage and collection operations. */
class TaskListTest {
    @Test
    void add_task_appendsTaskAndUpdatesSize() {
        TaskList taskList = new TaskList();
        Task task = new ToDo("buy milk");

        taskList.add(task);

        assertEquals(1, taskList.size());
        assertSame(task, taskList.get(0));
    }

    @Test
    void taskList_nullTasks_reportBrokenCollectionContract() {
        assertThrows(IllegalArgumentException.class, () -> new TaskList((List<Task>) null));

        TaskList taskList = new TaskList();
        assertThrows(IllegalArgumentException.class, () -> taskList.add(null));
        assertThrows(IllegalArgumentException.class, () -> taskList.find(null));
    }

    @Test
    void remove_taskAtIndex_returnsTaskAndShiftsRemainingTasks() {
        Task first = new ToDo("first");
        Task second = new ToDo("second");
        TaskList taskList = new TaskList(List.of(first, second));

        Task removed = taskList.remove(0);

        assertSame(first, removed);
        assertEquals(1, taskList.size());
        assertSame(second, taskList.get(0));
    }

    @Test
    void constructor_sourceListChanges_doesNotChangeTaskList() {
        List<Task> source = new ArrayList<>();
        source.add(new ToDo("buy milk"));

        TaskList taskList = new TaskList(source);
        source.clear();

        assertEquals(1, taskList.size());
    }

    @Test
    void iterator_removeCalled_cannotModifyTaskList() {
        TaskList taskList = new TaskList(List.of(new ToDo("buy milk")));
        Iterator<Task> iterator = taskList.iterator();
        iterator.next();

        assertThrows(UnsupportedOperationException.class, iterator::remove);
        assertEquals(1, taskList.size());
    }

    @Test
    void find_keywordMatchesIgnoringCase_returnsMatchingTasksInOrder() {
        TaskList taskList = new TaskList(List.of(
                new ToDo("read book"),
                new ToDo("buy milk"),
                new ToDo("return BOOK")));

        TaskList matchingTasks = taskList.find("book");

        assertEquals(2, matchingTasks.size());
        assertEquals("read book", matchingTasks.get(0).getDescription());
        assertEquals("return BOOK", matchingTasks.get(1).getDescription());
        assertEquals(3, taskList.size());
    }

    @Test
    void find_noDescriptionMatches_returnsEmptyTaskList() {
        TaskList taskList = new TaskList(List.of(new ToDo("buy milk")));

        assertEquals(0, taskList.find("book").size());
    }

    @Test
    void findUpcomingReminders_filtersAndSortsPendingTimedTasks() {
        LocalDateTime now = LocalDateTime.of(2026, 9, 11, 12, 0);
        Deadline first = new Deadline("first", LocalDateTime.of(2026, 9, 12, 12, 0));
        Deadline outsideWindow = new Deadline("outside", LocalDateTime.of(2026, 9, 20, 12, 0));
        Event second = new Event("second", LocalDateTime.of(2026, 9, 14, 10, 0),
                LocalDateTime.of(2026, 9, 14, 11, 0));
        Deadline disabled = new Deadline("disabled", LocalDateTime.of(2026, 9, 12, 12, 0));
        disabled.setReminderMinutesBefore(Reminder.DISABLED);
        Deadline completed = new Deadline("completed", LocalDateTime.of(2026, 9, 12, 12, 0));
        completed.markAsDone();
        TaskList taskList = new TaskList(List.of(first, outsideWindow, second, disabled, completed));

        List<Reminder> reminders = taskList.findUpcomingReminders(now);

        assertEquals(2, reminders.size());
        assertEquals(0, reminders.get(0).getTaskIndex());
        assertEquals(2, reminders.get(1).getTaskIndex());
    }

    @Test
    void findRemindersDueSoon_includesMissedReminderForFutureTask() {
        LocalDateTime now = LocalDateTime.of(2026, 9, 11, 12, 0);
        Deadline missedReminder = new Deadline("missed", now.plusHours(12));
        Deadline atWindowEnd = new Deadline("at window end", now.plusDays(2));
        Deadline outsideWindow = new Deadline("outside", now.plusDays(3));
        Deadline overdue = new Deadline("overdue", now.minusMinutes(1));
        Deadline completed = new Deadline("completed", now.plusHours(6));
        completed.markAsDone();
        TaskList taskList = new TaskList(List.of(missedReminder, atWindowEnd, outsideWindow, overdue, completed));

        List<Reminder> reminders = taskList.findRemindersDueSoon(now);

        assertEquals(2, reminders.size());
        assertEquals(0, reminders.get(0).getTaskIndex());
        assertEquals(1, reminders.get(1).getTaskIndex());
    }

    @Test
    void findUpcomingReminders_includesExactWindowBoundaries() {
        LocalDateTime now = LocalDateTime.of(2026, 9, 11, 12, 0);
        Deadline atStart = new Deadline("at start", now, 0);
        Deadline atWindowEnd = new Deadline("at window end", now.plusDays(7), 0);
        Deadline afterWindow = new Deadline("after window", now.plusDays(7).plusMinutes(1), 0);
        Deadline completed = new Deadline("completed", now.plusDays(1), 0);
        completed.markAsDone();
        Event event = new Event("event", now.plusDays(2), now.plusDays(2).plusHours(1), 0);
        TaskList taskList = new TaskList(List.of(atStart, atWindowEnd, afterWindow, completed, event));

        List<Reminder> reminders = taskList.findUpcomingReminders(now);

        assertEquals(3, reminders.size());
        assertEquals(0, reminders.get(0).getTaskIndex());
        assertEquals(4, reminders.get(1).getTaskIndex());
        assertEquals(1, reminders.get(2).getTaskIndex());
    }

    @Test
    void findUpcomingReminders_includesMissedReminderForUpcomingTask() {
        LocalDateTime now = LocalDateTime.of(2026, 9, 11, 12, 0);
        Deadline missed = new Deadline("missed", LocalDateTime.of(2026, 9, 12, 0, 0));
        Deadline upcoming = new Deadline("upcoming", LocalDateTime.of(2026, 9, 15, 12, 0));
        Deadline outsideWindow = new Deadline("outside", LocalDateTime.of(2026, 9, 20, 12, 0));
        Deadline overdue = new Deadline("overdue", LocalDateTime.of(2026, 9, 10, 12, 0));
        TaskList taskList = new TaskList(List.of(missed, upcoming, outsideWindow, overdue));

        List<Reminder> reminders = taskList.findUpcomingReminders(now);

        assertEquals(2, reminders.size());
        assertEquals(0, reminders.get(0).getTaskIndex());
        assertEquals(1, reminders.get(1).getTaskIndex());
    }

    @Test
    void findRemindersDueSoon_includesExactEndAndExcludesExactStart() {
        LocalDateTime now = LocalDateTime.of(2026, 9, 11, 12, 0);
        Deadline atStart = new Deadline("at start", now, 0);
        Deadline atWindowEnd = new Deadline("at window end", now.plusHours(24), 0);
        Deadline afterWindow = new Deadline("after window", now.plusHours(24).plusMinutes(1), 0);
        Event event = new Event("event", now.plusHours(12), now.plusHours(13), 0);
        TaskList taskList = new TaskList(List.of(atStart, atWindowEnd, afterWindow, event));

        List<Reminder> reminders = taskList.findRemindersDueSoon(now);

        assertEquals(2, reminders.size());
        assertEquals(3, reminders.get(0).getTaskIndex());
        assertEquals(1, reminders.get(1).getTaskIndex());
    }
}
