package rex.task;

import java.time.LocalDate;

/**
 * Something the user wants to keep track of, and whether it has been done yet.
 *
 * This is the plain kind of task, carrying only a description. It is also the
 * parent of every other kind, and defines what they all have in common: a
 * description, a done flag, an icon naming the kind, and the extra text a kind
 * may want to show after its description. A subclass overrides only the parts
 * that differ, which is why a task with a date needs to say nothing about
 * being marked done.
 */
public class Task {
    /**
     * What stands between two fields of a task's line in the save file.
     *
     * The format is this class's own, so the separator is named here and used
     * by every class that writes or reads a saved line, rather than the same
     * three characters being spelled out in each of them.
     */
    public static final String FIELD_SEPARATOR = " | ";

    private final String description;
    private boolean isDone;

    /**
     * Creates a task that has not been done yet.
     *
     * @param description what the task says, e.g. "return book".
     */
    public Task(String description) {
        assert description != null && !description.isBlank()
                : "An empty description is refused before a task is built";

        this.description = description;
        this.isDone = false;
    }

    /** Marks this task as done. */
    public void markAsDone() {
        this.isDone = true;
    }

    /** Marks this task as not done, undoing markAsDone(). */
    public void markAsNotDone() {
        this.isDone = false;
    }

    /**
     * Returns the mark shown in the box beside a task: "X" once it is done,
     * and a space while it is not, so that both line up in a list.
     */
    public String getStatusIcon() {
        return (isDone ? "X" : " ");
    }

    /**
     * Returns what the task says, e.g. "return book".
     *
     * The field itself is private, so this is the only way to read it. No
     * subclass needs it either: each one adds its own fields and leaves the
     * description to the parent, which is why it need not be widened to
     * protected.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the letter naming this kind of task, shown in the first box of a
     * listed task. A plain task has no letter of its own, so this is a space;
     * each subclass gives its own.
     */
    public String getTypeIcon() {
        return " ";
    }

    /**
     * Returns whatever this kind of task shows after its description, such as
     * a deadline's due date. A plain task has nothing to add, so this is
     * empty; subclasses that carry dates override it.
     */
    public String getDetails() {
        return "";
    }

    /**
     * Returns true if this task falls on the given day.
     *
     * A plain task has no date, so it never falls on any particular day and
     * this returns false. Subclasses that do have dates override it. Asking
     * the task itself, rather than testing its type from outside, means a new
     * kind of task decides this for itself instead of every place that
     * searches by date needing to learn about it.
     */
    public boolean isOn(LocalDate date) {
        return false;
    }

    /**
     * Returns this task as a single line in the save file format, e.g.
     * "T | 1 | read book", where the second field is 1 if the task is done
     * and 0 if it is not. Subclasses append their own extra fields to this.
     *
     * Note that a description containing the field separator would produce
     * a line that cannot be read back correctly. The parser refuses such a
     * description before a task is built, so none reaches this method.
     */
    public String toSaveFormat() {
        return getTypeIcon() + FIELD_SEPARATOR + (isDone ? "1" : "0") + FIELD_SEPARATOR + description;
    }
}
