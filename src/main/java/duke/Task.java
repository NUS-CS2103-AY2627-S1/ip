package duke;

import java.nio.file.Path;
import java.nio.file.Paths;

/** Represents a task and whether it has been completed. */
public class Task {
    /** Relative path used to save data. */
    private static final Path DATA_FILE = Paths.get("data", "hermes.txt");

    /** The text describing this task. */
    private final String description;

    /** The category of this task. */
    private final TaskType type;

    /** Whether this task has been marked as done. */
    private boolean isDone;

    /** Creates a task that is initially not done. */
    public Task(String description) {
        this(description, TaskType.TODO);
    }

    /** Creates a task with the supplied category. */
    protected Task(String description, TaskType type) {
        this.description = description;
        this.type = type;
        this.isDone = false;
    }

    /** Marks this task as done. */
    public void markAsDone() {
        isDone = true;
    }

    /** Marks this task as not done. */
    public void markAsNotDone() {
        isDone = false;
    }

    /** Returns {@code X} for a done task or a space otherwise. */
    public String getStatusIcon() {
        return isDone ? "X" : " ";
    }

    /** Returns the task description. */
    public String getDescription() {
        return description;
    }

    /** Returns the one-letter icon identifying this task type. */
    public String getTypeIcon() {
        return type.getIcon();
    }

    /** Returns the task description together with any date/time details. */
    public String getDisplayText() {
        return description;
    }

    /** Returns the relative path used for persistent task storage. */
    public static Path getDataFile() {
        return DATA_FILE;
    }

    /** Returns the completion flag in the file format. */
    public String getSaveStatus() {
        return isDone ? "1" : "0";
    }

    /** Returns this task in the format used by the save file. */
    public String toSaveString() {
        return getTypeIcon() + " | " + getSaveStatus() + " | " + description;
    }
}
