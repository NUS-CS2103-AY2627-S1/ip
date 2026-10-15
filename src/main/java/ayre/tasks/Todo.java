package ayre.tasks;

/**
 * A simple Task.
 */
public class Todo extends Task {
    /**
     * Initializes the Todo by passing the name on.
     *
     * @param name Name of the Todo.
     */
    public Todo(String name) {
        super(name);
    }

    /**
     * Converts the Task into a String that is easily parsable to store into the save file.
     *
     * @return String containing all necessary information about the Task to reconstruct it later.
     */
    @Override
    public String toLogString() {
        return "T " + super.toLogString();
    }

    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}
