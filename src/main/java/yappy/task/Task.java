package yappy.task;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Locale;

/**
 * Represents a task the user has asked the chatbot to remember.
 * Subclasses add the task type and any extra timing information.
 */
public abstract class Task {
    /**
     * Fields are protected so subclasses can format task-specific display text
     * without needing extra getter methods at this early project stage.
     */
    protected final TaskType taskType;
    protected final String description;
    protected boolean isDone;

    /**
     * Creates a task with the given description.
     * A newly created task always starts out as not done.
     */
    public Task(TaskType taskType, String description) {
        this.taskType = taskType;
        this.description = description;
        this.isDone = false;
    }

    /** Returns "X" if this task is done, or a blank space if it is not. */
    public String getStatusIcon() {
        return isDone ? "X" : " ";
    }

    /** Marks this task as done. */
    public void markAsDone() {
        this.isDone = true;
    }

    /** Marks this task as not done yet. */
    public void markAsNotDone() {
        this.isDone = false;
    }

    /** Returns whether this task has been completed. */
    public boolean isDone() {
        return isDone;
    }

    /**
     * Returns whether this task's description contains the keyword, ignoring letter case.
     */
    public boolean containsKeyword(String keyword) {
        return description.toLowerCase(Locale.ROOT).contains(keyword.toLowerCase(Locale.ROOT));
    }

    /**
     * Encodes text so separators and line breaks can be stored safely in one data-file record.
     */
    protected String encode(String text) {
        return Base64.getEncoder().encodeToString(text.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Returns a stable representation of this task for saving to disk.
     */
    public abstract String toDataString();

    /**
     * Returns the task status and description, e.g. "[X] read book".
     * Subclasses prepend their type icon and append any extra details.
     * Java calls toString() automatically when a Task is used where a
     * String is expected, such as in string concatenation.
     */
    @Override
    public String toString() {
        return "[" + taskType.getIcon() + "][" + getStatusIcon() + "] " + description;
    }
}
