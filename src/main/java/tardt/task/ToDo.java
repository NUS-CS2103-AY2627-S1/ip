package tardt.task;

import tardt.priority.Priority;

/**
 * A type of Task that only has a description associated with it.
 */
public class ToDo extends Task {

    public ToDo(String description) {
        super(description);
    }

    public ToDo(String description, Priority priority) {
        super(description, priority);
    }

    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}
