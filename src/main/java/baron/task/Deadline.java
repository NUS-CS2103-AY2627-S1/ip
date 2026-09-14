// javadocs created by AI

package baron.task;

import java.time.LocalDateTime;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import baron.data.DateHandler;
import baron.exception.BaronException;

/**
 * Represents a task that must be completed by a specified date or time.
 */
public class Deadline extends Task {
    /** The due date or time associated with the task. */
    private LocalDateTime dueDate;

    /**
     * Creates a deadline task.
     *
     * @param description the task description.
     * @param dueDate the deadline information.
     * @throws BaronException if the deadline has an invalid date format.
     */
    public Deadline(String description, String dueDate) throws BaronException {
        super(description);
        this.dueDate = DateHandler.parse(dueDate);
    }

    /**
     * Returns the deadline details to append to the task display.
     *
     * @return a formatted string containing the due date.
     */
    @Override
    public String extraInfo() {
        return " (by: " + DateHandler.format(dueDate) + ")";
    }

    /**
     * Returns the type symbol for a deadline task.
     *
     * @return "[D]".
     */
    @Override
    public String getTypeSymbol() {
        return "D";
    }

    /**
     * Returns the string representation of the task for saving to a file.
     *
     * @return the formatted task status and description for file storage.
     */
    @Override
    public String serialize() {
        JsonObject taskData = new Gson().fromJson(super.serialize(), JsonObject.class);
        taskData.addProperty("dueDate", DateHandler.format(dueDate));
        return new Gson().toJson(taskData);
    }

    /**
     * Returns the deadline or end time of the task, if applicable.
     * For tasks without a specific time constraint, this method returns null.
     *
     * @return the deadline or end time, or null if not applicable.
     */
    @Override
    public LocalDateTime getDeadline() {
        return dueDate;
    }
}
