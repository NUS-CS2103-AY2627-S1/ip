package bingbong.task;

/**
 * Represents a simple task without any date or time constraints.
 */
public class Todo extends Task {

    /**
     * Constructs a basic task containing only description string values.
     *
     * @param description The summary statement details of the item.
     */
    public Todo(String description) {
        super(description);
    }

    /**
     * Returns a formatted text summary representation containing todo status block tags.
     *
     * @return The formatted terminal view layout string.
     */
    @Override
    public String toString() {
        return "[T] [" + getStatusIcon() + "] " + this.description;
    }

    /**
     * Formats the todo structural properties into a database file data log stream record line.
     *
     * @return A pipe-separated serialisation data string.
     */
    @Override
    public String formatToFile() {
        return "T | " + getStatusBinary() + " | " + this.description;
    }
}
