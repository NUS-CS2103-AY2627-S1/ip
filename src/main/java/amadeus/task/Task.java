package amadeus.task;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A single entry in the task list.
 * <p>
 * This is the base class of {@link Todo}, {@link Deadline} and {@link Event}.
 * It holds what every task has - a description and whether it is done - and
 * leaves anything to do with dates to the subclasses. The fields are protected
 * rather than private so the subclasses can read them when building their own
 * {@code toString}.
 */
public class Task {
    protected String description;
    protected Status status;

    /**
     * Tags attached to this task, e.g. "#fun". Tagging is optional, so a freshly
     * created task starts with none.
     */
    protected List<String> tags = new ArrayList<>();

    /**
     * Creates a task that starts off not done, which is the only sensible state
     * for a task the user has just typed in.
     *
     * @param description what the task is, e.g. "read book".
     */
    public Task(String description) {
        this.description = description;
        this.status = Status.NOT_DONE;
    }

    /** Returns the task's description, e.g. "read book". */
    public String getDescription() {
        return description;
    }

    /** Returns the task's tags, e.g. {@code ["#fun", "#urgent"]}; empty if none were given. */
    public List<String> getTags() {
        return Collections.unmodifiableList(tags);
    }

    /**
     * Replaces this task's tags.
     * <p>
     * Tags are set after construction, the same way {@link #markAsDone()} changes
     * status after construction, rather than through the constructor - the parser
     * builds the task first and only then knows which "#word" tokens it found.
     *
     * @param tags the tags to attach, e.g. {@code ["#fun", "#urgent"]}.
     */
    public void setTags(List<String> tags) {
        this.tags = new ArrayList<>(tags);
    }

    /**
     * Returns true if this task has been completed.
     * Used when saving, where the state is written as 1 or 0 rather than as an icon.
     */
    public boolean isDone() {
        return status == Status.DONE;
    }

    /**
     * Returns true if this task is relevant to the given day, which is what the
     * "on" command asks each task.
     * <p>
     * A plain todo carries no date, so the answer here is always false; the
     * subclasses that do have dates override this. Asking every task the same
     * question keeps the date logic inside the class that owns the date, instead
     * of a chain of type tests at the call site.
     */
    public boolean occursOn(LocalDate date) {
        return false;
    }

    /**
     * Returns true if the description contains the given keyword, ignoring case.
     * <p>
     * Case is ignored so that "find Book" and "find book" both turn up
     * "read book"; a user searching their own list should not have to remember
     * how they capitalised it. The match is on any part of the description, so
     * "book" also finds "bookshop".
     *
     * @param keyword the text to look for, e.g. "book".
     * @return true if the keyword appears anywhere in the description.
     */
    public boolean descriptionContains(String keyword) {
        return description.toLowerCase().contains(keyword.toLowerCase());
    }

    /** Returns the character shown between the brackets, "X" for a done task and a space otherwise. */
    public String getStatusIcon() {
        return status.getIcon(); // "X" marks a done task
    }

    /** Marks this task as completed, as the "mark" command does. */
    public void markAsDone() {
        this.status = Status.DONE;
    }

    /** Marks this task as not yet completed, undoing a {@link #markAsDone()}. */
    public void markAsNotDone() {
        this.status = Status.NOT_DONE;
    }

    /**
     * Returns the task as the user sees it, e.g. "[ ] read book" or, with tags,
     * "[ ] read book [#fun, #urgent]".
     * Each subclass prefixes its own tag to this, e.g. "[T]" for a todo.
     */
    @Override
    public String toString() {
        String tagSuffix = tags.isEmpty() ? "" : " [" + String.join(", ", tags) + "]";
        return "[" + this.getStatusIcon() + "] " + description + tagSuffix;
    }
}
