package ducky;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Represents a task with a description and completion status.
 */
public class Task {
    private final String description;
    private final List<String> tags;
    private boolean isDone;

    /**
     * Creates an incomplete task with the given description.
     *
     * @param description the description of the task.
     */
    public Task(String description) {
        this(description, List.of());
    }

    /**
     * Creates an incomplete task with the given description and tags.
     * Duplicate tags are removed while their original order is retained.
     *
     * @param description the description of the task.
     * @param tags the tags associated with the task, without {@code #} prefixes.
     */
    public Task(String description, List<String> tags) {
        assert description != null : "A task must have a description";
        assert tags != null : "A task must have a tag collection";
        this.description = description;
        this.tags = List.copyOf(new LinkedHashSet<>(tags));
        this.isDone = false;
    }

    /**
     * Returns the symbol representing whether this task is complete.
     *
     * @return {@code X} for a completed task or a space otherwise.
     */
    public String getStatusIcon() {
        return isDone ? "X" : " ";
    }

    /**
     * Returns the task description.
     *
     * @return the task description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the tags associated with this task.
     *
     * @return an immutable list of tags without {@code #} prefixes.
     */
    public List<String> getTags() {
        return tags;
    }

    /**
     * Returns whether this task is completed.
     *
     * @return {@code true} if the task is completed.
     */
    public boolean isDone() {
        return isDone;
    }

    /**
     * Marks this task as completed.
     */
    public void markAsDone() {
        this.isDone = true;
    }

    /**
     * Marks this task as incomplete.
     */
    public void unmark() {
        this.isDone = false;
    }

    /**
     * Returns the task description together with its completion status.
     *
     * @return the formatted task description.
     */
    @Override
    public String toString() {
        String formattedTags = tags.stream()
                .map(tag -> "#" + tag)
                .collect(Collectors.joining(" "));
        String tagSuffix = formattedTags.isEmpty() ? "" : " " + formattedTags;
        return String.format("[%s] %s%s", getStatusIcon(), description, tagSuffix);
    }
}
