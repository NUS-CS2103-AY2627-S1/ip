package probe.task;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a task with a description and completion status.
 */
public abstract class Task {
    protected String description;
    protected boolean isDone;
    protected final List<Tag> tags;

    /**
     * Creates an incomplete task with the specified description.
     *
     * @param description Description of the task.
     */
    public Task(String description) {
        this.description = description;
        this.isDone = false;
        this.tags = new ArrayList<>();
    }

    /**
     * Returns {@code X} for a completed task or a blank space otherwise.
     *
     * @return Status icon for the task.
     */
    public String getStatusIcon() {
        return (isDone ? "X" : " "); // mark done task with X
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
    public void markAsUndone() {
        this.isDone = false;
    }

    /**
     * Returns the task description.
     *
     * @return Description of the task.
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Returns the task in the format used by persistent storage.
     *
     * @return Storage representation of the task.
     */
    public abstract String toFileFormat();

    /**
     * Adds one or more tags to this task.
     *
     * @param tagNames Tag names to add, with or without a leading {@code #}.
     */
    public void addTag(String... tagNames) {
        for (String tagName : tagNames) {
            Tag newTag = new Tag(tagName);
            if (!tags.contains(newTag)) {
                tags.add(newTag);
            }
        }
    }

    /**
     * Removes one or more tags from this task by name.
     *
     * @param tagNames Tag names to remove, with or without a leading {@code #}.
     */
    public void deleteTag(String... tagNames) {
        for (String tagName : tagNames) {
            tags.remove(new Tag(tagName));
        }
    }

    /**
     * Removes every tag assigned to this task.
     */
    public void clearTags() {
        tags.clear();
    }

    /**
     * Returns the tags assigned to this task.
     *
     * @return An unmodifiable view of this task's tags.
     */
    public List<Tag> getTags() {
        return List.copyOf(tags);
    }

    /**
     * Returns the tags in the comma-separated format used by storage.
     *
     * @return Comma-separated tag names, or an empty string if there are no tags.
     */
    public String tagsToFileFormat() {
        return tags.stream()
                .map(Tag::getName)
                .reduce((first, second) -> first + "," + second)
                .orElse("");
    }

    /**
     * Returns a display-friendly representation of this task.
     *
     * @return Display representation of the task.
     */
    @Override
    public String toString() {
        String tagText = tags.stream()
                .map(Tag::toString)
                .reduce((first, second) -> first + " " + second)
                .map(value -> " " + value)
                .orElse("");
        return "[" + getStatusIcon() + "] " + this.description + tagText;
    }
}
