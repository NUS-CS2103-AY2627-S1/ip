package ekud.task;

import java.time.LocalDate;
import java.util.Locale;

/**
 * A single item in the chatbot's task list.
 * Concrete subclasses ({@link Todo}, {@link Deadline}, {@link Event}) add any
 * date/time details. Deadlines and events store those as {@link TaskDateTime}.
 */
public abstract class Task {
    private final String description;
    private boolean isDone;

    /**
     * Creates an incomplete task with the given description.
     *
     * @param description text describing the task
     */
    protected Task(String description) {
        // Parser (and Storage via Parser) reject blank descriptions before construction.
        assert description != null && !description.isBlank()
                : "Task description should already be validated by the parser";
        this.description = description;
        this.isDone = false;
    }

    /**
     * Returns the task description, without type or date details.
     *
     * @return the description text
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns whether this task is marked as done.
     *
     * @return {@code true} if the task is done
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
     * Returns the letter that identifies this task type, for example {@code T}.
     *
     * @return the type icon
     */
    public abstract String getTypeIcon();

    /**
     * Returns {@code X} if done, or a space if not done, for the status box.
     *
     * @return the status icon character as a string
     */
    public String getStatusIcon() {
        return isDone ? "X" : " ";
    }

    /**
     * Returns extra details such as a deadline or event times. Empty for todos.
     *
     * @return a suffix to append after the description, or an empty string
     */
    protected String getExtraDetails() {
        return "";
    }

    /**
     * Returns whether this task occurs on {@code date}.
     * Todos always return {@code false}; deadlines and events override this.
     *
     * @param date the calendar date to test
     * @return {@code true} if this task occurs on that date
     */
    public boolean occursOn(LocalDate date) {
        return false;
    }

    /**
     * Returns whether the description contains {@code keyword}, ignoring case.
     * Only the description is searched, not dates or status text.
     *
     * @param keyword text to look for
     * @return {@code true} if the description contains the keyword
     */
    public boolean descriptionContains(String keyword) {
        return description.toLowerCase(Locale.ENGLISH)
                .contains(keyword.toLowerCase(Locale.ENGLISH));
    }

    /**
     * Returns the add-command text that would recreate this task, without the
     * done flag. {@link ekud.storage.Storage} prefixes {@code 0} or {@code 1} when saving.
     *
     * @return a line such as {@code todo borrow book}
     */
    public abstract String toCommandString();

    /**
     * Returns the task with type and status boxes, for example
     * {@code [T][ ] borrow book}.
     *
     * @return the formatted task
     */
    @Override
    public String toString() {
        return "[" + getTypeIcon() + "][" + getStatusIcon() + "] " + description + getExtraDetails();
    }
}
