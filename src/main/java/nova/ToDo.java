package nova;

/**
 * A task with no date attached, such as "borrow book".
 * The simplest of the three types: it adds only a type tag to what Task
 * already provides.
 */
public class ToDo extends Task {

    /**
     * Creates a to-do with the given description.
     *
     * @param taskName what the user typed as the description.
     * @param isMarked whether the task starts out done.
     */
    public ToDo(String taskName, boolean isMarked) {
        super(taskName, isMarked);
    }

    /**
     * Returns this task as one data-file line.
     *
     * @return the saved form, e.g. "T | 0 | borrow book".
     */
    @Override
    public String toDataString() {
        return "T | " + dataFields();
    }

    /**
     * Returns this task in the form shown to the user.
     *
     * @return the display form, e.g. "[T][ ] borrow book".
     */
    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}
