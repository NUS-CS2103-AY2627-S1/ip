package milo.task;

/** A task without a deadline or event time range. */
public class ToDo extends Task {
    /** Creates an incomplete todo task. */
    public ToDo(String description) {
        super(description);
    }

    /** Returns this todo in the format used by storage. */
    @Override
    public String storageString() {
        return String.format("T | %s", super.storageString());
    }

    /** Returns this todo in the format shown to the user. */
    @Override
    public String toString() {
        return String.format("[T] %s", super.toString());
    }
}
