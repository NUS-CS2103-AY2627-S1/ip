package rex.task;

/**
 * A task with nothing but a description: something to be done, with no date
 * attached to it.
 *
 * It adds no state of its own to Task, and exists so that a todo can be told
 * apart from a deadline or an event when it is listed and when it is saved.
 */
public class ToDo extends Task {
    /**
     * Creates a todo.
     *
     * @param description what has to be done.
     */
    public ToDo(String description) {
        super(description);
    }

    /** Returns "T", the letter marking a todo in a listed or saved task. */
    @Override
    public String getTypeIcon() {
        return "T";
    }
}
