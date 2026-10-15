package rei.task;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Represents a task with a description, completion status, and task type.
 */
public class Task {
    private static final String TAG_PATTERN = "#[A-Za-z0-9][A-Za-z0-9_-]*";

    private boolean isDone;
    private final String description;
    private final TaskType taskType;
    private final List<String> tags;

    /**
     * Creates an incomplete todo task with the specified description.
     *
     * @param description Description of the task.
     */
    public Task(String description) {
        this(description, TaskType.TODO);
    }

    /**
     * Creates a task with the specified type.
     *
     * @param description Description of the task.
     * @param taskType Category of the task.
     */
    protected Task(String description, TaskType taskType) {
        this.description = description;
        this.isDone = false;
        this.taskType = taskType;
        this.tags = new ArrayList<>();
    }

    /**
     * Returns the icon representing the task's completion status.
     *
     * @return {@code X} when completed, or a space when incomplete.
     */
    public String getStatusIcon() {
        return (isDone ? "X" : " ");
    }
    /**
     * Returns the task description.
     *
     * @return Description entered by the user.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the short display code for this task's type.
     *
     * @return Task type display code.
     */
    public String getTaskType() {
        return taskType.getDisplayCode();
    }

    /** Returns whether this task has been completed. */
    public boolean isDone() {
        return isDone;
    }

    /** Returns whether the description contains the keyword, ignoring letter case. */
    public boolean hasKeyword(String keyword) {
        if (keyword.startsWith("#")) {
            return hasTag(keyword);
        }
        return description.toLowerCase(Locale.ENGLISH)
                .contains(keyword.toLowerCase(Locale.ENGLISH));
    }

    /**
     * Adds a valid tag unless the task already has it.
     *
     * @param tag Tag beginning with {@code #}.
     * @return {@code true} when the tag was added, or {@code false} for a duplicate.
     * @throws IllegalArgumentException If the tag has an invalid format.
     */
    public boolean addTag(String tag) {
        if (!tag.matches(TAG_PATTERN)) {
            throw new IllegalArgumentException("Invalid tag format");
        }
        String normalizedTag = tag.toLowerCase(Locale.ENGLISH);
        if (tags.contains(normalizedTag)) {
            return false;
        }
        tags.add(normalizedTag);
        return true;
    }

    /** Returns whether this task has the specified tag, ignoring letter case. */
    public boolean hasTag(String tag) {
        return tags.contains(tag.toLowerCase(Locale.ENGLISH));
    }

    /** Returns this task's tags in the order they were added. */
    public List<String> getTags() {
        return List.copyOf(tags);
    }

    /** Marks this task as completed. */
    public void markAsDone() {
        isDone = true;
    }

    /** Marks this task as incomplete. */
    public void markAsUndone() {
        isDone = false;
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        if (tags.isEmpty()) {
            return description;
        }
        return description + " " + String.join(" ", tags);
    }
}
