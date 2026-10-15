package nexus;

import java.time.LocalDate;

/**
 * Represents a task and its completion status.
 */
public class Task {
    private final String description;
    private boolean done;
    private LocalDate snoozedUntil;

    /** Creates a new task that is initially not done. */
    public Task(String description) {
        if (description == null || description.isBlank() || description.contains("\n")
                || description.contains("\r")) {
            throw new IllegalArgumentException("Task description must not be empty or contain line breaks.");
        }
        this.description = description;
        this.done = false;
        this.snoozedUntil = null;
    }

    /** Returns the task description. */
    public String getDescription() {
        return description;
    }

    /** Returns X for a completed task and a blank space otherwise. */
    public String getStatusIcon() {
        return done ? "X" : " ";
    }

    /** Marks this task as done. */
    public void markAsDone() {
        done = true;
    }

    /** Marks this task as not done. */
    public void markAsNotDone() {
        done = false;
    }

    /** Snoozes this task until the supplied date. */
    public void snoozeUntil(LocalDate date) {
        snoozedUntil = date;
    }

    /** Returns whether this task is hidden by an active snooze. */
    public boolean isSnoozed(LocalDate date) {
        return snoozedUntil != null && snoozedUntil.isAfter(date);
    }

    /** Returns the category of this task. */
    public TaskType getTaskType() {
        return TaskType.TODO;
    }

    /** Returns the complete display representation of this task. */
    @Override
    public String toString() {
        return "[" + getTaskType().getIcon() + "][" + getStatusIcon() + "] " + description;
    }
}
