package whimsybot.task;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Represents a task in Whimsy Bot's task list.
 */
public class Task {
    private final String description;
    private boolean isDone;
    private final Set<String> tags;

    /**
     * Creates an incomplete task with the given description.
     *
     * @param description the text describing the task
     */
    public Task(String description) {
        this.description = description;
        this.isDone = false;
        this.tags = new LinkedHashSet<>();
    }

    /**
     * Returns the symbol used to show whether this task is complete.
     *
     * @return {@code "X"} when complete, otherwise a space
     */
    public String getStatusIcon() {
        return isDone ? "X" : " ";
    }

    /** Returns the task description for persistence and display-related operations. */
    public String getDescription() {
        return description;
    }

    /** Returns whether this task has been marked as complete. */
    public boolean isDone() {
        return isDone;
    }

    /** Adds one or more tags, preserving the order in which tags were added.
     *
     * @param tagsToAdd the tags to add
     */
    public void addTag(String... tagsToAdd) {
        for (String tag : tagsToAdd) {
            tags.add(tag);
        }
    }

    /** Removes one or more tags if they are present.
     *
     * @param tagsToRemove the tags to remove
     */
    public void removeTag(String... tagsToRemove) {
        for (String tag : tagsToRemove) {
            tags.remove(tag);
        }
    }

    /** Returns this task's tags. */
    public Set<String> getTags() {
        return Collections.unmodifiableSet(tags);
    }

    /** Marks this task as complete. */
    public void markAsDone() {
        isDone = true;
    }

    /**
     * Marks this task as incomplete.
     */
    public void unmarkAsDone() {
        isDone = false;
    }

    /**
     * Returns the common, human-readable portion of every task.
     *
     * @return this task's completion status and description
     */
    @Override
    public String toString() {
        String tagText = tags.stream().map(tag -> "[#" + tag + "]").reduce("", String::concat);
        return "[" + getStatusIcon() + "] " + description + tagText;
    }
}
