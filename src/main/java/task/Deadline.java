package task;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * A task that contains a description and a due date.
 * This class extends {@link Task}.
 */
public class Deadline extends Task {
    protected LocalDateTime by;
    protected String invalidBy;
    private final DateTimeFormatter customFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HHmm");

    /**
     * Constructs the task with the specific description and due date.
     * The due date is represented by either a LocalDateTime object or a String, but not both.
     *
     * @param description the task's description.
     * @param by the due date as a LocalDateTime object, or null if it is stored as a String.
     * @param invalidBy the due date as a String, or an empty String if it is stored as a LocalDateTime.
     */
    public Deadline(String description, LocalDateTime by, String invalidBy) {
        super(description);
        this.by = by;
        this.invalidBy = invalidBy;
    }

    /**
     * Returns the task's deadline, if present.
     *
     * @return the task's deadline.
     */
    public LocalDateTime getBy() {
        return by;
    }

    /**
     * Returns a String representation of the task to be written to a file for storage.
     * Status is indicated as "1" for marked and "0" for unmarked.
     *
     * @return the task as a String in the format: "|due date (as a LocalDateTime or String)|status."
     */
    public String getFileFormat(){
        if (this.by == null){
            return " | " + this.invalidBy + " | " + (this.isDone() ? "1" : "0");
        } else {
            return " | " + this.by.format(customFormatter) + " | " + (this.isDone() ? "1" : "0");
        }
    }

    /**
     * Returns a string representation of the task consisting of its type, its status icon,
     * its description and its due date.
     * Its due date will either be formatted as a LocalDateTime object or as the String the
     * task was created with.
     *
     * @return the task as a String in the format: "[D][status] description (by: due date)"
     */
    @Override
    public String toString() {
        if (this.by == null){
            return "[D]" + super.toString() + " (by: " + this.invalidBy + ")";
        } else {
            return "[D]" + super.toString() + " (by: " + this.by.format(customFormatter) + ")";
        }
    }
}