package chud.task;

import java.time.LocalDate;

/** A task with a description and a due date/time. */
public class Deadline extends Task {
    protected TaskDateTime by;

    /** Creates a new, not-yet-done deadline with the given description and due date/time. */
    public Deadline(String description, TaskDateTime by) {
        super(description);
        this.by = by;
    }

    @Override
    public TaskType getTaskType() {
        return TaskType.DEADLINE;
    }

    @Override
    public boolean occursOn(LocalDate date) {
        return by.getDate().equals(date);
    }

    @Override
    public LocalDate getSortDate() {
        return by.getDate();
    }

    @Override
    public String toString() {
        return super.toString() + " (by: " + by + ")";
    }

    @Override
    public String toFileString() {
        return super.toFileString() + " | " + by.toStorageString();
    }
}
