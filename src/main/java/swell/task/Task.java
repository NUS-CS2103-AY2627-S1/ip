package swell.task;

import java.util.ArrayList;
import java.util.stream.Collectors;

import swell.exception.SwellException;

/**
 * Represents one task in Swell's task list.
 */
public class Task {
    private final TaskType type;
    private final String description;
    private final ArrayList<String> tags;
    private boolean isDone;

    /**
     * Creates a task with the given type and description.
     *
     * @param type task type.
     * @param description task description.
     */
    public Task(TaskType type, String description) {
        this(type, description, new ArrayList<>());
    }

    /**
     * Creates a task with the given type, description, and tags.
     *
     * @param type task type.
     * @param description task description.
     * @param tags task tags.
     */
    public Task(TaskType type, String description, ArrayList<String> tags) {
        this.type = type;
        this.description = description;
        this.tags = new ArrayList<>(tags);
        this.isDone = false;
    }

    /**
     * Marks this task as done.
     */
    public void markAsDone() throws SwellException {
        if (isDone) {
            throw new SwellException("That task is already marked as done.");
        }
        this.isDone = true;
    }

    /**
     * Marks this task as not done.
     */
    public void markAsNotDone() throws SwellException {
        if (!isDone) {
            throw new SwellException("That task is already marked as not done.");
        }
        this.isDone = false;
    }

    /**
     * Returns this task's type.
     *
     * @return task type.
     */
    public TaskType getType() {
        return type;
    }

    /**
     * Returns this task's description.
     *
     * @return task description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns this task's tags.
     *
     * @return copy of task tags.
     */
    public ArrayList<String> getTags() {
        return new ArrayList<>(tags);
    }

    /**
     * Returns whether this task has the given tag.
     *
     * @param tag tag to check.
     * @return true if this task has the tag.
     */
    public boolean hasTag(String tag) {
        return tags.stream().anyMatch(taskTag -> taskTag.equalsIgnoreCase(tag));
    }

    /**
     * Returns whether this task is marked as done.
     *
     * @return true if this task is done.
     */
    public boolean isDone() {
        return isDone;
    }

    /**
     * Returns the status icon used in task display text.
     *
     * @return status icon.
     */
    protected String getStatusIcon() {
        return isDone ? "X" : " ";
    }

    /**
     * Returns the display form of this task.
     *
     * @return display form of this task.
     */
    @Override
    public String toString() {
        return "[" + type.getSymbol() + "][" + getStatusIcon() + "] "
                + description + getTagText();
    }

    private String getTagText() {
        if (tags.isEmpty()) {
            return "";
        }

        return tags.stream()
                .map(tag -> "#" + tag)
                .collect(Collectors.joining(" ", " ", ""));
    }
}
