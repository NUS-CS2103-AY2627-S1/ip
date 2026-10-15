package kevin.task;

/**
 * ToDo class is a basic Task.
 */
public class ToDo extends Task {
    public ToDo(String description, boolean isDone) {
        super(description, isDone);
    }

    public ToDo(String description) {
        super(description, false);
    }

    /**
     * Formats ToDo into String for saving into tasks.txt.
     */
    @Override
    public String formatSaveString() {
        return "T | " + super.formatSaveString();
    }

    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}
