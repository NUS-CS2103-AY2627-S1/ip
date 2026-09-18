package com.benthecat.kachow.task;

import java.time.LocalDate;

/**
 * Represents a task with a description and completion status.
 */
public abstract class Task {
    private final String description;
    private boolean isDone;

    /**
     * Creates an incomplete task with the given description.
     *
     * @param description Text describing the task.
     */
    public Task(String description) {
        this(description, false);
    }

    /**
     * Creates a task with the given description and completion status.
     * This constructor is used when restoring a task from storage.
     *
     * @param description Text describing the task.
     * @param isDone Whether the task has been completed.
     */
    public Task(String description, boolean isDone) {
        this.description = normalizeDescription(description);
        this.isDone = isDone;
    }

    /**
     * Validates and normalizes a description so it fits safely in one stored record.
     *
     * @param description User-entered or stored description.
     * @return Description with surrounding whitespace removed and internal whitespace collapsed.
     * @throws IllegalArgumentException If the description is empty or contains reserved characters.
     */
    public static String normalizeDescription(String description) {
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("This racer needs a non-empty description.");
        }
        if (description.indexOf('|') >= 0 || description.codePoints().anyMatch(character -> (
                Character.isISOControl(character) && character != '\t')
                        || character == 0x2028 || character == 0x2029)) {
            throw new IllegalArgumentException(
                    "Descriptions cannot contain |, line breaks, or control characters.");
        }
        String normalizedDescription = description.replaceAll("(?U)\\s+", " ").strip();
        if (normalizedDescription.isEmpty()) {
            throw new IllegalArgumentException("This racer needs a non-empty description.");
        }
        return normalizedDescription;
    }

    /**
     * Compares task type, description, and date values, ignoring completion and description case.
     *
     * @param other Task to compare with this task.
     * @return Whether both tasks describe the same work.
     */
    public boolean hasSameDetails(Task other) {
        if (other == null || getClass() != other.getClass()
                || !description.equalsIgnoreCase(other.description)) {
            return false;
        }
        return switch (this) {
            case Todo todo -> true;
            case Deadline deadline -> deadline.getDueDateTime().equals(((Deadline) other).getDueDateTime());
            case Event event -> event.getFrom().equals(((Event) other).getFrom())
                    && event.getTo().equals(((Event) other).getTo());
            default -> false;
        };
    }

    /**
     * Copies this task with a new completion status without mutating the original.
     *
     * @param isDone Completion status for the copy.
     * @return Independent task with the same details.
     */
    public Task withDoneStatus(boolean isDone) {
        return switch (this) {
            case Todo todo -> new Todo(description, isDone);
            case Deadline deadline -> new Deadline(description, deadline.getDueDateTime(), isDone);
            case Event event -> new Event(description, event.getFrom(), event.getTo(), isDone);
            default -> throw new IllegalArgumentException("Unsupported task type: " + getClass().getName());
        };
    }

    /**
     * Marks this task as complete.
     */
    public void markAsDone() {
        isDone = true;
    }

    /**
     * Marks this task as incomplete.
     */
    public void markAsNotDone() {
        isDone = false;
    }

    /**
     * Returns the task description without display formatting.
     *
     * @return Task description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Reports whether the task has been completed.
     *
     * @return {@code true} when the task is complete.
     */
    public boolean isDone() {
        return isDone;
    }

    /**
     * Reports whether this task has a deadline or event occurrence on the given date.
     * Tasks without date parameters do not occur on any calendar date.
     *
     * @param date Calendar date to check.
     * @return {@code true} when this task occurs on the date.
     */
    public abstract boolean occursOn(LocalDate date);

    /**
     * Returns the task description prefixed with its completion icon.
     *
     * @return Formatted status and description.
     */
    public String getStatusText() {
        return (isDone ? "[X] " : "[ ] ") + description;
    }
}
