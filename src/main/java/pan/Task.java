package pan;

/**
 * Represents a single task with a description and a done/not-done status.
 */
public class Task {
    protected String description;
    protected boolean isDone;

    /** Creates a task with the given description, initially not done. */
    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    public String getStatusIcon() {
        return (isDone ? "X" : " "); // mark done task with X
    }

    /** Returns this task's description text (without the status icon or type tag). */
    public String getDescription() {
        return description;
    }

    public void markAsDone() {
        isDone = true;
    }

    public void markAsNotDone() {
        isDone = false;
    }

    /**
     * Applies one {@code update} option to this task, leaving every other
     * detail - including the done/not-done status - untouched.
     *
     * <p>Each subclass overrides this to handle the options it owns
     * ({@code /by} for a deadline, {@code /from} and {@code /to} for an event)
     * and calls {@code super.applyUpdate} for anything else. That is what lets
     * the {@code update} command work without knowing which kind of task it is
     * holding.
     *
     * @param option the option name without its slash, e.g. {@code "desc"}.
     * @param value  the new value for that option.
     * @throws PanException if this kind of task has no such option.
     */
    public void applyUpdate(String option, String value) throws PanException {
        if (option.equals("desc")) {
            description = value;
            return;
        }
        throw new PanException(" Ehhh?? PanPan doesn't know how to change \"/" + option
                + "\" on this task~ Try /desc, /by (deadline) or /from and /to (event)!");
    }

    @Override
    public String toString() {
        return "[" + getStatusIcon() + "] " + description;
    }

    /**
     * Returns the on-disk representation of this task's status and
     * description, e.g. {@code "1 | read book"}. Subclasses prepend their
     * type tag and append any extra fields. This is deliberately separate
     * from {@link #toString()}, which produces the pretty display format.
     */
    public String toFileString() {
        return (isDone ? "1" : "0") + " | " + description;
    }
}
