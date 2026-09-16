package turtley.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.NavigableSet;
import java.util.TreeSet;

import turtley.exception.TurtleyException;

/**
 * Represents a general task in Turtley's task list.
 */
public class Task {

    private static final Comparator<String> TAG_COMPARATOR = String.CASE_INSENSITIVE_ORDER
            .thenComparing(Comparator.naturalOrder());
    /** The user-facing task description. */
    protected String description;
    /** Whether the task has been marked as completed. */
    protected boolean isDone;
    private final TaskType taskType;
    private final NavigableSet<String> tags;

    /**
     * Creates an unfinished task with the given description.
     *
     * @param description the task description.
     */
    public Task(String description) {
        this(TaskType.TODO, description, List.of());
    }

    /**
     * Creates a task with a caller-supplied type.
     *
     * @param taskType the task type.
     * @param description the task description.
     */
    public Task(TaskType taskType, String description) {
        this(taskType, description, List.of());
    }

    /**
     * Creates a task with a caller-supplied type and tags.
     *
     * @param taskType the task type.
     * @param description the task description.
     * @param tags the task tags.
     * @throws TurtleyException if the task type is null or the description is blank.
     */
    public Task(TaskType taskType, String description, Collection<String> tags) {
        if (taskType == null) {
            throw new TurtleyException("Task type cannot be null.");
        }
        if (description == null || description.isBlank()) {
            throw new TurtleyException("Task description cannot be blank.");
        }
        this.taskType = taskType;
        this.description = description;
        this.isDone = false;
        this.tags = new TreeSet<>(TAG_COMPARATOR);
        addTags(tags);
    }

    /**
     * Returns this task's type.
     *
     * @return the task type.
     */
    public TaskType getTaskType() {
        return taskType;
    }

    /**
     * Returns this task's type icon.
     *
     * @return the task type icon.
     */
    public String getTypeIcon() {
        return switch (taskType) {
            case TODO -> "T";
            case DEADLINE -> "D";
            case EVENT -> "E";
        };
    }

    /**
     * Returns the symbol used to display this task's completion status.
     *
     * @return {@code X} for a completed task, or a space otherwise.
     */
    public String getStatusIcon() {
        return isDone ? "X" : " ";
    }

    /**
     * Returns whether this task has been completed.
     *
     * @return {@code true} if the task is done.
     */
    public boolean isDone() {
        return isDone;
    }

    /**
     * Marks this task as done.
     */
    public void markAsDone() {
        isDone = true;
    }

    /**
     * Marks this task as not done.
     */
    public void markAsNotDone() {
        isDone = false;
    }

    /**
     * Returns this task's description.
     *
     * @return the task description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns this task's tags in display order.
     *
     * @return an immutable list of tags.
     */
    public List<String> getTags() {
        return List.copyOf(tags);
    }

    /**
     * Adds valid tags to this task, ignoring duplicate tags.
     *
     * @param newTags the tags to add.
     * @return the tags that were newly added.
     */
    public List<String> addTags(Collection<String> newTags) {
        TagValidator.validateTags(newTags);
        List<String> addedTags = new ArrayList<>();
        for (String tag : newTags) {
            if (!tags.contains(tag)) {
                addedTags.add(tag);
            }
        }
        if (tags.size() + addedTags.size() > TagValidator.MAX_TAG_COUNT) {
            throw new TurtleyException("A task can have at most 10 tags.");
        }
        tags.addAll(addedTags);
        return addedTags;
    }

    /**
     * Removes tags from this task, ignoring tags that are not present.
     *
     * @param tagsToRemove the tags to remove.
     * @return the tags that were removed.
     */
    public List<String> removeTags(Collection<String> tagsToRemove) {
        TagValidator.validateTags(tagsToRemove);
        List<String> removedTags = new ArrayList<>();
        for (String tag : tagsToRemove) {
            if (tags.remove(tag)) {
                removedTags.add(tag);
            }
        }
        return removedTags;
    }

    /**
     * Returns the description followed by the formatted tags.
     *
     * @return the description with tags, if any.
     */
    public String getDescriptionWithTags() {
        if (tags.isEmpty()) {
            return description;
        }
        return description + " " + tags.stream()
                .map(tag -> "[" + tag + "]")
                .reduce((first, second) -> first + " " + second)
                .orElse("");
    }

    /**
     * Returns the display form of this task.
     *
     * @return the type icon, status icon, and description.
     */
    @Override
    public String toString() {
        return "[" + getTypeIcon() + "][" + getStatusIcon() + "] " + getDescriptionWithTags();
    }
}
