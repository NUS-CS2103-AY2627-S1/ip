package task;

/**
 * A task that only contains a description.
 * This class extends {@link Task}.
 */
public class ToDo extends Task{

    /**
     * Constructs the task with the specific description.
     *
     * @param desc the description of the task.
     */
    public ToDo (String desc) {
        super(desc);
    }

    /**
     * Returns a string representation of the task consisting of its type, then its status icon
     * followed by its description.
     *
     * @return the task as a string in the format "[T][status] description".
     */
    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}