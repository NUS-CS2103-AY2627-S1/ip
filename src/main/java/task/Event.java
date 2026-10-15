package task;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * A task that contains a description, a start date and end date.
 * This class extends {@link Task}.
 */
public class Event extends Task {
    protected LocalDateTime start, end;
    protected String invalidStart, invalidEnd;
    private final DateTimeFormatter customFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HHmm");

    /**
     * Constructs the task with the specific description, start and end dates.
     * The start and end dates are represented by either a LocalDateTime object or a String, but not both.
     *
     * @param desc the task's description.
     * @param start the start date as a LocalDateTime object, or null if it is stored as a String.
     * @param end the end date as a LocalDateTime object, or null if it is stored as a String.
     * @param invalidStart the start date as a String, or an empty String if it is stored as a LocalDateTime.
     * @param invalidEnd the end date as a String, or an empty String if it is stored as a LocalDateTime.
     */
    public Event (String desc, LocalDateTime start, LocalDateTime end, String invalidStart, String invalidEnd) {
        super(desc);
        this.start = start;
        this.end = end;
        this.invalidStart = invalidStart;
        this.invalidEnd = invalidEnd;
    }

    /**
     * Returns a String representation of the task to be written to a file for storage.
     * Status is indicated as "1" for marked and "0" for unmarked.
     *
     * @return the task as a String in the format: "|start date|end date|status" where the start and end dates
     * are in LocalDateTime formats or as Strings the task was created with.
     */
    public String getFileFormat() {
        if (this.start == null && this.end == null){
            return " | " + this.invalidStart + " | " + this.invalidEnd + " | " + (this.isDone() ? "1" : "0");
        } else if (this.start == null) {
            return " | " + this.invalidStart + " | " + this.end.format(customFormatter) + " | " + (this.isDone() ? "1" : "0");
        } else if (this.end == null) {
            return " | " + this.start.format(customFormatter) + " | " + this.invalidEnd + " | " + (this.isDone() ? "1" : "0");
        } else {
            return " | " + this.start.format(customFormatter) + " | " + this.end.format(customFormatter) + " | " + (this.isDone() ? "1" : "0");
        }
    }

    /**
     * Returns the LocalDateTime start date.
     *
     * @return the start date as a LocalDateTime.
     */
    public LocalDateTime getStart() {
        return start;
    }

    /**
     * Returns the LocalDateTime end date.
     *
     * @return the end date as a LocalDateTime.
     */
    public LocalDateTime getEnd() {
        return end;
    }

    /**
     * Returns a string representation of the task consisting of its type, its status icon,
     * its description, its start and end dates.
     * Its start and end dates will either be formatted as a LocalDateTime object or as the Strings the
     * task was created with.
     *
     * @return the task as a String in the format: "[E][status] description (From: start date to: end date".
     */
    @Override
    public String toString() {
        if (this.start == null && this.end == null){
            return "[E]" + super.toString() + "(From: " + this.invalidStart + " to: " + this.invalidEnd + ")";
        } else if (this.end == null) {
            return "[E]" + super.toString() + "(From: " + this.start.format(customFormatter) + " to: " + this.invalidEnd + ")";
        } else if (this.start == null) {
            return "[E]" + super.toString() + "(From: " + this.invalidStart + " to: " + this.end.format(customFormatter) + ")";
        } else {
            return "[E]" + super.toString() + "(From: " + this.start.format(customFormatter) + " to: " + this.end.format(customFormatter) + ")";
        }
    }
}