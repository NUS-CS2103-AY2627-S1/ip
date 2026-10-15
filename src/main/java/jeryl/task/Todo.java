package jeryl.task;

/**
 * A task without any date/time attached to it.
 */
public class Todo extends Task {
    /**
     * Creates a not-yet-done todo with the given description and no
     * priority set.
     */
    public Todo(String description) {
        super(description);
    }

    /**
     * Creates a not-yet-done todo with the given description and priority.
     */
    public Todo(String description, Priority priority) {
        super(description, priority);
    }

    @Override
    public String toString() {
        return getPriorityTag() + "[T]" + super.toString();
    }

    @Override
    public String toFileString() {
        return withPriority("T | " + super.toFileString());
    }
}
