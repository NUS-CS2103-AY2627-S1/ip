package shai.task;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * Owns the tasks currently managed by Shai.
 *
 * <p>This class provides the task-list operations used by command handling so
 * callers do not need to manage the underlying collection directly.</p>
 */
public class TaskList implements Iterable<Task> {
    private static final long UPCOMING_WINDOW_DAYS = 7;
    private static final long AUTOMATIC_REMINDER_WINDOW_HOURS = 24;

    private final List<Task> tasks;

    /** Creates an empty task list. */
    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    /**
     * Creates a task list containing the supplied tasks.
     *
     * @param tasks tasks to copy into this list
     */
    public TaskList(List<Task> tasks) {
        if (tasks == null) {
            throw new IllegalArgumentException("The source task list must not be null.");
        }
        if (!tasks.stream().allMatch(Objects::nonNull)) {
            throw new IllegalArgumentException("A task list must not contain null tasks.");
        }
        this.tasks = new ArrayList<>(tasks);
    }

    /** Adds a task to the end of the list. */
    public void add(Task task) {
        requireTask(task);
        tasks.add(task);
    }

    /**
     * Adds a task at the supplied index.
     *
     * @param index insertion index
     * @param task task to add
     */
    public void add(int index, Task task) {
        requireTask(task);
        tasks.add(index, task);
    }

    /** Gets a task by its zero-based index. */
    public Task get(int index) {
        return tasks.get(index);
    }

    /** Removes and returns a task by its zero-based index. */
    public Task remove(int index) {
        return tasks.remove(index);
    }

    /** Returns the number of tasks in the list. */
    public int size() {
        return tasks.size();
    }

    /** Returns a new list containing tasks whose descriptions contain a keyword. */
    public TaskList find(String keyword) {
        if (keyword == null) {
            throw new IllegalArgumentException("The search keyword must not be null.");
        }
        String normalizedKeyword = keyword.toLowerCase(Locale.ROOT);
        List<Task> matchingTasks = tasks.stream()
                .filter(task -> task.getDescription().toLowerCase(Locale.ROOT)
                        .contains(normalizedKeyword))
                .toList();
        return new TaskList(matchingTasks);
    }

    /**
     * Returns incomplete reminders scheduled within the next seven days,
     * including missed reminders for tasks that are still upcoming.
     *
     * @param now the current time used as the start of the window
     * @return reminders sorted by reminder time and then task-list index
     */
    public List<Reminder> findUpcomingReminders(LocalDateTime now) {
        if (now == null) {
            throw new IllegalArgumentException("The current time must not be null.");
        }
        LocalDateTime windowEnd = now.plusDays(UPCOMING_WINDOW_DAYS);
        List<Reminder> reminders = new ArrayList<>();
        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);
            LocalDateTime targetTime = getTargetTime(task);
            LocalDateTime reminderTime = getReminderTime(task);
            if (task.isDone() || targetTime == null || reminderTime == null) {
                continue;
            }
            boolean isReminderUpcoming = !reminderTime.isBefore(now);
            boolean isUpcomingTaskWithMissedReminder = reminderTime.isBefore(now)
                    && targetTime.isAfter(now) && !targetTime.isAfter(windowEnd);
            if (targetTime.isBefore(now) || reminderTime.isAfter(windowEnd)
                    || (!isReminderUpcoming && !isUpcomingTaskWithMissedReminder)) {
                continue;
            }
            reminders.add(new Reminder(i, task, reminderTime));
        }
        return reminders.stream()
                .sorted(Comparator.comparing(Reminder::getReminderTime)
                        .thenComparingInt(Reminder::getTaskIndex))
                .toList();
    }

    /**
     * Returns incomplete reminders that are due within the next day.
     *
     * <p>A reminder whose scheduled time has passed is included when its task
     * is still in the future, so the user can see a notice after restarting
     * Shai without receiving a notice for an already overdue task.</p>
     *
     * @param now the current time used as the end of the overdue portion
     * @return reminders sorted by reminder time and then task-list index
     */
    public List<Reminder> findRemindersDueSoon(LocalDateTime now) {
        if (now == null) {
            throw new IllegalArgumentException("The current time must not be null.");
        }
        LocalDateTime windowEnd = now.plusHours(AUTOMATIC_REMINDER_WINDOW_HOURS);
        List<Reminder> reminders = new ArrayList<>();
        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);
            LocalDateTime targetTime = getTargetTime(task);
            LocalDateTime reminderTime = getReminderTime(task);
            if (task.isDone() || targetTime == null || reminderTime == null
                    || !targetTime.isAfter(now) || reminderTime.isAfter(windowEnd)) {
                continue;
            }
            reminders.add(new Reminder(i, task, reminderTime));
        }
        return reminders.stream()
                .sorted(Comparator.comparing(Reminder::getReminderTime)
                        .thenComparingInt(Reminder::getTaskIndex))
                .toList();
    }

    /** Returns the target time for a task type that can have a reminder. */
    private static LocalDateTime getTargetTime(Task task) {
        if (task instanceof Deadline deadline) {
            return deadline.getBy();
        }
        if (task instanceof Event event) {
            return event.getFrom();
        }
        return null;
    }

    /** Returns the scheduled reminder time for a task type that can have one. */
    private static LocalDateTime getReminderTime(Task task) {
        if (task instanceof Deadline deadline
                && deadline.getReminderMinutesBefore() != Reminder.DISABLED) {
            return deadline.getBy().minusMinutes(deadline.getReminderMinutesBefore());
        }
        if (task instanceof Event event
                && event.getReminderMinutesBefore() != Reminder.DISABLED) {
            return event.getFrom().minusMinutes(event.getReminderMinutesBefore());
        }
        return null;
    }

    /** Ensures that a task list never stores a null task. */
    private static void requireTask(Task task) {
        if (task == null) {
            throw new IllegalArgumentException("A task list must not contain null tasks.");
        }
    }

    /**
     * Returns a read-only iterator over the current tasks.
     *
     * @return an iterator that cannot modify this task list
     */
    @Override
    public Iterator<Task> iterator() {
        return Collections.unmodifiableList(tasks).iterator();
    }
}
