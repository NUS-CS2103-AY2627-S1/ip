package echo;

/**
 * Simple task object that only has a description.
 */
public class Todo extends Task {
    private final String taskMarker = "[T]";

    /**
     * Creates a task item with no time or date associated with it.
     *
     * @param desc the description associated with the task
     */
    public Todo(String desc) {
        super(desc);
    }
    @Override
    public String toString() {
        return String.format("%s%s %s",
                this.taskMarker,
                super.getStatusMarker(),
                super.getDesc());
    }
}
