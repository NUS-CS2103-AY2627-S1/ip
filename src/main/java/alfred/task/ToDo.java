package alfred.task;

/**
 * Represents a task without an attached date or time.
 */
public class ToDo extends Task {
    /** Creates an incomplete todo task. */
    public ToDo(String description) {
        super(description);
    }

    @Override
    protected TaskType getType() {
        return TaskType.TODO;
    }

    @Override
    protected String getDateTimeText() {
        return "";
    }

    @Override
    public String toSaveFormat() {
        return getType().getIcon() + " | " + getStatusBit() + " | " + description
                + " | " + getArchiveBit();
    }
}
