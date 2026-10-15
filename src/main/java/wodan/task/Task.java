package wodan.task;

import java.time.LocalDate;
import java.util.Locale;

/**
 * A task that the user can mark as done or not done.
 */
public abstract class Task {
    /** Save-file type code for a todo. */
    public static final String STORAGE_TYPE_TODO = "T";
    /** Save-file type code for a deadline. */
    public static final String STORAGE_TYPE_DEADLINE = "D";
    /** Save-file type code for an event. */
    public static final String STORAGE_TYPE_EVENT = "E";
    /** Save-file flag for a done task. */
    public static final String STORAGE_DONE = "1";
    /** Save-file flag for a task that is not done. */
    public static final String STORAGE_NOT_DONE = "0";
    /** Default category when a task has not been tagged. */
    public static final String DEFAULT_CATEGORY = "general";

    private static final String STATUS_DONE_ICON = "X";
    private static final String STATUS_NOT_DONE_ICON = " ";

    /** What the user asked to do. */
    private String description;
    /** Whether this task has been marked done. */
    private boolean isDone;
    /** What the user tagged the task as. */
    private String category = DEFAULT_CATEGORY;

    /**
     * Creates a task that is not done yet.
     *
     * @param description What the task is.
     */
    public Task(String description) {
        assert description != null : "Task description should not be null";
        this.description = description;
        this.isDone = false;
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
    public void markAsUndone() {
        isDone = false;
    }

    /**
     * Returns the status icon for this task.
     *
     * @return {@code X} if done, or a space if not done.
     */
    public String getStatusIcon() {
        return isDone ? STATUS_DONE_ICON : STATUS_NOT_DONE_ICON;
    }

    /**
     * Returns the description of this task.
     *
     * @return What the task is.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the shared start of a save-file line: type, done flag, and description.
     *
     * @param typeCode One of the {@code STORAGE_TYPE_*} constants.
     * @return Prefix such as {@code T | 0 | borrow book}.
     */
    protected String toStoragePrefix(String typeCode) {
        String doneFlag = isDone ? STORAGE_DONE : STORAGE_NOT_DONE;
        return typeCode + " | " + doneFlag + " | " + description;
    }

    /**
     * Returns whether this task has been marked done.
     *
     * @return {@code true} if done.
     */
    public boolean isDone() {
        return isDone;
    }

    /**
     * Returns the category this task is grouped under.
     *
     * @return The category name, {@code general} if the task is untagged.
     */
    public String getCategory() {
        return category;
    }

    /**
     * Sets the category, using {@code general} if {@code category} is blank.
     * The name is trimmed and stored in lowercase so {@code School} and {@code school} match.
     *
     * @param category Category name from the user or the save file.
     */
    public void setCategory(String category) {
        assert category != null : "Category should not be null";
        String trimmed = category.trim().toLowerCase(Locale.ROOT);
        this.category = trimmed.isEmpty() ? DEFAULT_CATEGORY : trimmed;
    }

    /**
     * Returns {@code true} if this task is in the default {@code general} category.
     *
     * @return Whether the category is {@code general}.
     */
    public boolean isGeneral() {
        return DEFAULT_CATEGORY.equals(category);
    }

    /**
     * Returns this task as the user sees it in list replies.
     *
     * @return Display text including type, status, and details.
     */
    public abstract String toString();

    /**
     * Returns this task encoded as one line for the save file.
     *
     * @return A pipe-separated line describing the task type, done status, and details.
     */
    public abstract String toStorageString();

    /**
     * Returns {@code true} if this task occurs on {@code date}.
     * Todos never occur on a calendar date.
     *
     * @param date Date to check.
     * @return Whether this task falls on {@code date}.
     */
    public boolean occursOn(LocalDate date) {
        return false;
    }

    /**
     * Returns {@code true} if this task's description contains {@code keyword}.
     * Matching ignores letter case and looks only at the description, not dates.
     *
     * @param keyword Text to search for.
     * @return Whether the description contains {@code keyword}.
     */
    public boolean hasDescriptionContaining(String keyword) {
        return description.toLowerCase(Locale.ROOT)
                .contains(keyword.toLowerCase(Locale.ROOT));
    }

    /**
     * Returns whether {@code other} is the same kind of quest with the same details.
     * Done status and category are ignored.
     *
     * @param other Task to compare, which may be {@code null}.
     * @return {@code true} if adding both would look like a duplicated quest.
     */
    public boolean hasSameDetails(Task other) {
        if (other == null || getClass() != other.getClass()) {
            return false;
        }
        return getDescription().equals(other.getDescription());
    }
}

