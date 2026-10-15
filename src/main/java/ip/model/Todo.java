
package ip.model;

/** Represents a task without a date or time constraint. */
public class Todo extends Task {

    /** Creates a todo with the given description. */
    public Todo(String description) {
        super(description);
    }

    @Override
    /** Returns the serialized todo representation. */
    public String toFileFormat() {
        return "T | " + super.toFileFormat();
    }

    @Override
    /** Returns the user-facing todo representation. */
    public String toString() {
        return "[T]" + super.toString();
    }
}
