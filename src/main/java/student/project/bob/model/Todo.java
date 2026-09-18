package student.project.bob.model;

/**
 * A task without a date or time attached to it.
 */
public class Todo extends Task {

    /**
     * Creates an incomplete todo task.
     *
     * @param description description of the todo
     */
    public Todo(String description) {
        super(description);
    }

    /**
     * Returns this todo in Bob's display and persistence format.
     *
     * @return the todo's status and description
     */
    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}
