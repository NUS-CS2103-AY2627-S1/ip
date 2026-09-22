package steph.task;

/**
 * A task with a name and a done/not-done status. Concrete task types
 * (ToDo, Deadline, Event) extend this class.
 */
public abstract class Task {

    /**
     * Separates fields within a save-file line (see {@link #toFileFormat()}).
     * Shared so every subclass's {@code toFileFormat()} and
     * {@link steph.Storage}'s reader agree on the same delimiter.
     */
    public static final String FIELD_SEPARATOR = "|";

    private final String name;
    private boolean isDone;

    /**
     * Constructs a task with the given name, initially not done.
     *
     * @param name Description of the task.
     */
    public Task(String name) {
        this.name = name;
        this.isDone = false;
    }

    public boolean isDone() {
        return this.isDone;
    }

    public String getName() {
        return this.name;
    }

    /** Marks this task as done. */
    public void complete() {
        this.isDone = true;
    }

    /** Marks this task as not done. */
    public void uncomplete() {
        this.isDone = false;
    }

    /**
     * Returns this task's data as a partial save-file line: the status flag and
     * name, e.g. " | 0 | read book". Subclasses prepend their type letter (and
     * append any extra fields) to produce the full line written to disk.
     */
    public String toFileFormat() {
        return " " + FIELD_SEPARATOR + " " + (isDone ? "1" : "0") + " " + FIELD_SEPARATOR + " " + this.name;
    }

    /**
     * Returns this task as {@code "[X] name"} when done, or {@code "[ ] name"}
     * when not. Subclasses prepend their type tag, e.g. {@code "[T]"}.
     */
    @Override
    public String toString() {
        if (this.isDone) {
            return "[X] " + this.name;
        }
        return "[ ] " + this.name;
    }
}
