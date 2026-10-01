package bany.tasks;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import bany.tags.Tag;

/** Represents a task with a unique ID, description, type, and completion state. */
public abstract class Task {
    /** The next ID available for a newly created task. */
    private static long nextId = 1;

    /** The ID of this task, unique across all task types during this run. */
    private final int id;
    /** Text describing the work represented by this task. */
    private final String description;
    /** Whether this task has been marked as completed. */
    private boolean isDone;
    /** Tags belonging to this task, kept in insertion order. */
    private final List<Tag> tags;

    /**
     * Creates a task with its initial tags.
     *
     * @param id ID previously assigned to the task.
     * @param description task description.
     * @param tags initial tags belonging to the task.
     */
    protected Task(int id, String description, List<Tag> tags) {
        if (id < 1) {
            throw new IllegalArgumentException("Task ID must be positive.");
        }
        Objects.requireNonNull(description, "Task description cannot be null.");
        if (description.isBlank()) {
            throw new IllegalArgumentException("Task description cannot be blank.");
        }

        this.id = id;
        this.description = description;
        this.isDone = false;
        this.tags = new ArrayList<>();
        addInitialTags(tags);
        nextId = Math.max(nextId, (long) id + 1);
    }

    /**
     * Creates a task without any tags.
     *
     * @param id ID previously assigned to the task.
     * @param description task description.
     */
    protected Task(int id, String description) {
        this(id, description, List.of());
    }

    /**
     * Allocates the next globally unique task ID for a newly created task.
     *
     * @return a positive task ID.
     * @throws IllegalStateException if all positive integer IDs are exhausted.
     */
    public static synchronized int allocateId() {
        if (nextId > Integer.MAX_VALUE) {
            throw new IllegalStateException("No task IDs are available.");
        }
        return (int) nextId++;
    }

    /**
     * Resets the ID allocator after rebuilding a task list from storage.
     *
     * @param nextIdForNewTask ID that should be assigned to the next new task.
     */
    public static synchronized void resetIdAllocator(long nextIdForNewTask) {
        if (nextIdForNewTask < 1) {
            throw new IllegalArgumentException("Next task ID must be positive.");
        }
        nextId = nextIdForNewTask;
    }

    /**
     * Returns the globally assigned ID of this task.
     *
     * @return this task's ID.
     */
    public int getId() {
        return id;
    }

    /**
     * Returns the completion marker used in task-list output.
     *
     * @return the completed or incomplete status marker.
     */
    public String getStatusIcon() {
        return (isDone ? TaskStatus.DONE.getStatus()
                : TaskStatus.NOT_DONE.getStatus()); // mark done task with X
    }

    /** Marks this task as completed. */
    public void mark() {
        isDone = true;
    }

    /** Marks this task as incomplete. */
    public void unmark() {
        isDone = false;
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
     * Returns the full name of this task's type.
     *
     * @return full task type.
     */
    public abstract String getType();

    /**
     * Returns the abbreviated type marker used in task-list output.
     *
     * @return abbreviated task type.
     */
    public abstract String getTypeShort();

    /**
     * Returns this task's date information, including its leading space when present.
     *
     * @return task date information, or an empty string for a ToDo.
     */
    public abstract String getDateString();

    /**
     * Returns the names of tags that define this task's scheduling data.
     *
     * @return critical tag names for this task type.
     */
    public abstract List<String> getCriticalTags();

    /**
     * Returns all tags belonging to this task.
     *
     * @return immutable tags in insertion order.
     */
    public final List<Tag> getTags() {
        return List.copyOf(tags);
    }

    /**
     * Finds a tag by name, ignoring case.
     *
     * @param name tag name to find.
     * @return matching tag, or empty when absent.
     */
    public final Optional<Tag> getTag(String name) {
        String normalisedName = normaliseTagName(name);
        return tags.stream()
                .filter(tag -> tag.name().equals(normalisedName))
                .findFirst();
    }

    /**
     * Replaces or adds tags as one atomic update.
     *
     * <p>Duplicate names within the update are rejected. Existing tags may be
     * replaced, which is useful for commands such as {@code reschedule}.
     * Subclasses validate the proposed complete tag list before it is committed.</p>
     *
     * @param updates tags to add or replace.
     * @throws NullPointerException if the update list or a tag is null.
     * @throws IllegalArgumentException if update names are duplicated or the
     *         proposed tags violate this task type's rules.
     */
    public final void updateTags(List<Tag> updates) {
        Objects.requireNonNull(updates, "Tag updates cannot be null.");

        List<Tag> candidate = new ArrayList<>(tags);
        Set<String> updateNames = new HashSet<>();
        for (Tag update : updates) {
            Tag nonNullUpdate = Objects.requireNonNull(update, "Tag update cannot be null.");
            if (!updateNames.add(nonNullUpdate.name())) {
                throw new IllegalArgumentException(
                        "Duplicate tag name: " + nonNullUpdate.name());
            }

            int existingIndex = findTagIndex(candidate, nonNullUpdate.name());
            if (existingIndex == -1) {
                candidate.add(nonNullUpdate);
            } else {
                candidate.set(existingIndex, nonNullUpdate);
            }
        }

        validateTags(List.copyOf(candidate));
        tags.clear();
        tags.addAll(candidate);
    }

    /**
     * Replaces all tags as one atomic operation.
     *
     * <p>This is useful when a command needs to restore a task after a
     * persistence failure. Unlike {@link #updateTags(List)}, tags that are
     * not present in the replacement list are removed.</p>
     *
     * @param replacementTags complete replacement tag list.
     * @throws NullPointerException if the replacement list or a tag is null.
     * @throws IllegalArgumentException if tag names are duplicated or the
     *         proposed tags violate this task type's rules.
     */
    public final void replaceTags(List<Tag> replacementTags) {
        Objects.requireNonNull(replacementTags, "Replacement tags cannot be null.");

        List<Tag> replacement = new ArrayList<>();
        Set<String> replacementNames = new HashSet<>();
        for (Tag tag : replacementTags) {
            Tag nonNullTag = Objects.requireNonNull(tag, "Tag cannot be null.");
            if (!replacementNames.add(nonNullTag.name())) {
                throw new IllegalArgumentException(
                        "Duplicate tag name: " + nonNullTag.name());
            }
            replacement.add(nonNullTag);
        }

        validateTags(List.copyOf(replacement));
        tags.clear();
        tags.addAll(replacement);
    }

    /**
     * Validates a proposed tag list for this task type.
     *
     * @param candidate complete tag list after applying an update.
     */
    protected void validateTags(List<Tag> candidate) {
        // General tags need no additional validation in the base task.
    }

    /** Adds constructor tags while enforcing unique tag names. */
    private void addInitialTags(List<Tag> initialTags) {
        Objects.requireNonNull(initialTags, "Tags cannot be null.");
        for (Tag tag : initialTags) {
            Tag nonNullTag = Objects.requireNonNull(tag, "Tag cannot be null.");
            if (findTagIndex(tags, nonNullTag.name()) != -1) {
                throw new IllegalArgumentException(
                        "Duplicate tag name: " + nonNullTag.name());
            }
            tags.add(nonNullTag);
        }
    }

    /** Finds a tag index by its normalized name. */
    private static int findTagIndex(List<Tag> tags, String name) {
        for (int i = 0; i < tags.size(); i++) {
            if (tags.get(i).name().equals(name)) {
                return i;
            }
        }
        return -1;
    }

    /** Normalizes a name for case-insensitive tag lookup. */
    private static String normaliseTagName(String name) {
        Objects.requireNonNull(name, "Tag name cannot be null.");
        String normalisedName = name.trim().toLowerCase(Locale.ROOT);
        if (normalisedName.isBlank()) {
            throw new IllegalArgumentException("Tag name cannot be blank.");
        }
        return normalisedName;
    }

    /**
     * Returns whether this task is completed.
     *
     * @return {@code true} if the task is marked done.
     */
    public boolean isDone() {
        return isDone;
    }

    /**
     * Formats this task for display in the task list.
     *
     * @return formatted task text.
     */
    @Override
    public String toString() {
        return String.format("[%s][%s] %s%s", getTypeShort(), getStatusIcon(), description, getDateString());
    }
}
