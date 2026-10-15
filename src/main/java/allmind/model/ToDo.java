package allmind.model;

/** A task without a deadline or event date range. */
public class ToDo extends Task {

    /**
     * Creates a new {@code ToDo} object
     * @param description Description of the task
     * */
    public ToDo(String description) {
        super(description);
    }


    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}
