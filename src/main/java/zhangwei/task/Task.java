package zhangwei.task;

import java.time.LocalDate;
import java.util.Optional;

/**
 * A task tracked by the chatbot: a description plus whether it is done.
 * Subclasses add whatever extra detail their task type needs.
 */
public class Task {

    /** What the user wants to be reminded of, e.g. "read book". */
    private final String description;

    /** Whether the user has marked this task as done. */
    private boolean isDone;

    /**
     * Creates a task with the given description.
     * A newly created task is always not done.
     *
     * @param description what the user wants to be reminded of, e.g. "read book".
     */
    public Task(String description) {
        assert description != null && !description.isBlank()
                : "A task needs a description; the parser and the save file reader both reject a blank one.";
        this.description = description;
        this.isDone = false;
    }

    /**
     * Returns the icon shown inside the status brackets.
     *
     * @return "X" if this task is done, or a single space otherwise.
     */
    public String getStatusIcon() {
        return isDone ? "X" : " ";
    }

    /**
     * Returns the description this task was created with.
     *
     * @return the task's description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns whether this task has been marked as done.
     *
     * @return true if the task is done, false otherwise.
     */
    public boolean isDone() {
        return isDone;
    }

    /**
     * Returns the date this task is anchored to, if it has one.
     *
     * <p>A task type that carries no date -- a todo -- has nothing to be
     * ordered by, so it answers with an empty Optional rather than a stand-in
     * date that would sort it somewhere misleading. Subclasses that do have a
     * date override this.
     *
     * @return the date this task is anchored to, or empty if it has none.
     */
    public Optional<LocalDate> getDate() {
        return Optional.empty();
    }

    /** Marks this task as done. */
    public void markAsDone() {
        isDone = true;
    }

    /** Marks this task as not done. */
    public void markAsNotDone() {
        isDone = false;
    }

    /**
     * Returns the status and description shared by every task type,
     * e.g. "[X] read book". Subclasses prepend their own type icon.
     *
     * @return this task's status and description as one line.
     */
    @Override
    public String toString() {
        return "[" + getStatusIcon() + "] " + description;
    }
}
