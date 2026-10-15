package tangent.task;

/** Represents a task with no specified start or end time. */
public class ToDo extends Task {

    /** Creates a new ToDo object with a description. */
    public ToDo(String description) {
        super(description);
    }

    /** Converts the ToDo object to a String to be displayed to the user. */
    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}
