package shai.task;

import java.time.LocalDateTime;

/** Represents an upcoming reminder and the task that owns it. */
public final class Reminder {
    /** The default reminder lead time for deadlines and events. */
    public static final long DEFAULT_MINUTES_BEFORE = 24 * 60;

    /** The value used when a task's reminder is disabled. */
    public static final long DISABLED = -1;

    /** The largest supported reminder lead time in minutes. */
    public static final long MAX_MINUTES_BEFORE = Integer.MAX_VALUE;

    private final int taskIndex;
    private final Task task;
    private final LocalDateTime reminderTime;

    /** Creates a reminder for a task at the supplied task-list index and time. */
    public Reminder(int taskIndex, Task task, LocalDateTime reminderTime) {
        if (taskIndex < 0) {
            throw new IllegalArgumentException("A reminder task index must not be negative.");
        }
        if (task == null) {
            throw new IllegalArgumentException("A reminder must have a task.");
        }
        if (reminderTime == null) {
            throw new IllegalArgumentException("A reminder must have a reminder time.");
        }
        this.taskIndex = taskIndex;
        this.task = task;
        this.reminderTime = reminderTime;
    }

    /** Returns the zero-based index of the owning task. */
    public int getTaskIndex() {
        return taskIndex;
    }

    /** Returns the task that owns this reminder. */
    public Task getTask() {
        return task;
    }

    /** Returns when this reminder should be shown. */
    public LocalDateTime getReminderTime() {
        return reminderTime;
    }

    /** Returns whether a reminder lead-time value is valid. */
    public static boolean isValidMinutesBefore(long minutesBefore) {
        return minutesBefore == DISABLED
                || minutesBefore >= 0 && minutesBefore <= MAX_MINUTES_BEFORE;
    }
}
