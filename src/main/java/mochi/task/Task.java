package mochi.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import mochi.MochiException;

/**
 * Represents a task with a type, description, and done status.
 * This is the base class for all task types.
 */
public class Task {
    protected final TaskType type;
    protected String description;
    protected boolean isDone;

    protected Task(TaskType type, String description) {
        this.type = type;
        this.description = description;
        this.isDone = false;
    }

    /**
     * Returns the status icon of this task: "X" if done, " " otherwise.
     *
     * @return status icon string
     */
    public String getStatusIcon() {
        return (isDone ? "X" : " ");
    }

    /** Marks this task as done. */
    public void markAsDone() {
        this.isDone = true;
    }

    /** Marks this task as not done. */
    public void markAsNotDone() {
        this.isDone = false;
    }

    /**
     * Returns a human-readable representation of this task.
     *
     * @return formatted task string with status icon and description
     */
    @Override
    public String toString() {
        return "[" + getStatusIcon() + "] " + description;
    }

    /**
     * Returns a pipe-delimited string for saving this task to file.
     *
     * @return file-format string representation
     */
    public String toFileString() {
        return type.getSymbol() + " | " + (isDone ? "1" : "0") + " | " + description;
    }

    /**
     * Parses a line from the saved file and returns the corresponding Task.
     */
    public static Task fromFileString(String line) throws MochiException {
        String[] parts = line.split("\\|", 3);
        if (parts.length < 3) {
            throw new MochiException("Corrupted task data.");
        }

        String typeStr = parts[0].trim();
        String doneStr = parts[1].trim();
        String remaining = parts[2].trim();

        boolean isDone;
        try {
            isDone = Integer.parseInt(doneStr) == 1;
        } catch (NumberFormatException e) {
            throw new MochiException("Corrupted task data.");
        }

        switch (typeStr) {
        case "T":
            Todo todo = new Todo(remaining);
            if (isDone) {
                todo.markAsDone();
            }
            return todo;
        case "D":
            String[] deadlineParts = remaining.split("\\|", 2);
            if (deadlineParts.length < 2) {
                throw new MochiException("Corrupted deadline data.");
            }
            LocalDate deadlineDate;
            try {
                deadlineDate = LocalDate.parse(deadlineParts[1].trim(), DateTimeFormatter.ISO_LOCAL_DATE);
            } catch (DateTimeParseException e) {
                throw new MochiException("Corrupted deadline data.");
            }
            Deadline deadline = new Deadline(deadlineParts[0].trim(), deadlineDate);
            if (isDone) {
                deadline.markAsDone();
            }
            return deadline;
        case "E":
            String[] eventParts = remaining.split("\\|", 3);
            if (eventParts.length < 3) {
                throw new MochiException("Corrupted event data.");
            }
            LocalDate eventFrom;
            LocalDate eventTo;
            try {
                eventFrom = LocalDate.parse(eventParts[1].trim(), DateTimeFormatter.ISO_LOCAL_DATE);
                eventTo = LocalDate.parse(eventParts[2].trim(), DateTimeFormatter.ISO_LOCAL_DATE);
            } catch (DateTimeParseException e) {
                throw new MochiException("Corrupted event data.");
            }
            Event event = new Event(eventParts[0].trim(), eventFrom, eventTo);
            if (isDone) {
                event.markAsDone();
            }
            return event;
        default:
            return null;
        }
    }
}
