package auto.task;

/** Represents a task without an associated date or time range. */
public class ToDo extends Task {

    /** Creates an incomplete todo with the specified description. */
    public ToDo(String name) {
        super(name);
    }

    @Override
    public String toString() {
        return String.format("[T]%s", super.toString());
    }
}
