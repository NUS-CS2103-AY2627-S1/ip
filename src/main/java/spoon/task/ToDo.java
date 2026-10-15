package spoon.task;

/**
 * Represents a To-Do (subclass of Task).
 */
public class ToDo extends Task {

    // Constructor
    public ToDo(String name) {
        super(name);
    }

    // Methods
    @Override
    public String format() {
        return "T | " + super.format();
    }

    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}
