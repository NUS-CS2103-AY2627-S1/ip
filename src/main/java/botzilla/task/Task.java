package botzilla.task;

import java.time.LocalDate;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Represents a single task in the task list. Holds the task's name,
 * completion status, and type. Subclasses (ToDoTask, DeadlineTask,
 * EventTask) add type-specific fields such as dates.
 */
public abstract class Task {
    private String name;
    private boolean isDone;
    private TaskType type;
    private final Set<String> tags = new LinkedHashSet<>();

    /**
     * Constructs a new, not-done Task with the given name and type.
     *
     * @param name display name/description of the task
     * @param type the task's type (todo, deadline, or event)
     */
    public Task(String name, TaskType type) {
        assert name != null && !name.trim().isEmpty()
                : "Task name should never be null or blank; callers (e.g. Parser) must reject empty "
                + "descriptions before constructing a Task";
        this.name = name;
        isDone = false;
        this.type = type;
    }

    /**
     * Marks this task as done.
     *
     * @return a confirmation message describing the change
     */
    public String mark() {
        isDone = true;
        return name + " marked as done! You go girl!";
    }

    /**
     * Marks this task as not done.
     *
     * @return a confirmation message describing the change
     */
    public String unmark() {
        isDone = false;
        return name + " is now marked as not done! Keep pushing on!";
    }

    /**
     * Returns a human-readable representation of this task for display,
     * e.g. "[T][X] read book". Subclasses append their own extra details.
     *
     * @return the formatted task string
     */
    @Override
    public String toString() {
        return "[" + type.getIcon() + "][" + getTaskStatus() + "] " + name;
    }

    /**
     * Returns the single-character icon representing completion status.
     *
     * @return "X" if done, otherwise a blank space
     */
    protected String getTaskStatus() {
        return isDone ? "X" : " ";
    }

    /**
     * Serializes this task into the pipe-delimited format used for saving
     * to disk. Subclasses append their own extra fields to this base string.
     *
     * @return the task encoded as "type icon | done flag | name"
     */
    public String toFileString() {
        return type.getIcon() + " | " + (isDone ? "1" : "0") + " | " + name;
    }

    /**
     * Returns this task's display name/description.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the calendar date associated with this task, if any. A plain
     * ToDoTask has no date and returns empty; DeadlineTask and EventTask
     * override this to return their deadline or start date respectively,
     * when it could be parsed.
     *
     * @return the task's date, or empty if it has none.
     */
    public Optional<LocalDate> getDate() {
        return Optional.empty();
    }

    /**
     * Adds a tag to this task, e.g. "fun". A leading '#' is stripped if
     * present, so callers may pass either "fun" or "#fun". Adding a tag
     * that's already present has no effect other than the returned message.
     *
     * @param tagName the tag to add, with or without a leading '#'
     * @return a confirmation message describing the outcome
     */
    public String addTag(String tagName) {
        String tag = normalizeTagName(tagName);
        boolean added = tags.add(tag);
        return added
                ? "Tagged \"" + name + "\" with #" + tag + "!"
                : "\"" + name + "\" is already tagged #" + tag + ".";
    }

    /**
     * Removes a tag from this task, if present. A leading '#' is stripped
     * if present, so callers may pass either "fun" or "#fun".
     *
     * @param tagName the tag to remove, with or without a leading '#'
     * @return a confirmation message describing the outcome
     */
    public String removeTag(String tagName) {
        String tag = normalizeTagName(tagName);
        boolean removed = tags.remove(tag);
        return removed
                ? "Removed #" + tag + " from \"" + name + "\"!"
                : "\"" + name + "\" doesn't have the tag #" + tag + ".";
    }

    /**
     * Returns this task's tags, in the order they were added.
     *
     * @return an unmodifiable view of this task's tags
     */
    public Set<String> getTags() {
        return Collections.unmodifiableSet(tags);
    }

    /**
     * Strips a leading '#' (if present) and surrounding whitespace from a
     * tag name, so tags are stored consistently regardless of how the
     * caller wrote them.
     *
     * @param tagName the raw tag name, with or without a leading '#'
     * @return the normalized tag name
     */
    private static String normalizeTagName(String tagName) {
        String trimmed = tagName.trim();
        String tag = trimmed.startsWith("#") ? trimmed.substring(1) : trimmed;
        assert !tag.isEmpty() : "tag name should never be blank; callers (e.g. Parser) must reject empty "
                + "tags before calling addTag/removeTag";
        return tag;
    }

    /**
     * Returns this task's tags formatted for display, e.g. " #fun #urgent",
     * or an empty string if it has none. Subclasses append this at the very
     * end of their own toString() override, so tags always appear last.
     *
     * @return the display suffix for this task's tags
     */
    protected String tagsDisplaySuffix() {
        if (tags.isEmpty()) {
            return "";
        }
        return " " + tags.stream().map(t -> "#" + t).collect(Collectors.joining(" "));
    }

    /**
     * Returns this task's tags formatted for saving to disk, e.g.
     * " | fun,urgent", or an empty string if it has none. Subclasses
     * append this at the very end of their own toFileString() override, so
     * the tags field is always the last on the line regardless of task
     * type, keeping the field count/position for each type unambiguous
     * when reloading.
     *
     * @return the file-format suffix for this task's tags
     */
    protected String tagsFileSuffix() {
        if (tags.isEmpty()) {
            return "";
        }
        return " | " + String.join(",", tags);
    }

    /**
     * Returns a signature identifying this task's substantive content —
     * its type and name, plus (for subclasses with dates) its date
     * field(s) — but not its done/tag state. Used by {@link TaskList} to
     * detect an attempt to add a task that's effectively a duplicate of
     * one already in the list.
     *
     * @return this task's duplicate-detection signature
     */
    String duplicateSignature() {
        return type.getIcon() + "|" + name.trim().toLowerCase();
    }
}

