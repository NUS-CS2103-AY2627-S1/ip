// javadocs created by AI

package baron.task;

import java.time.LocalDateTime;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import baron.data.DateHandler;
import baron.exception.BaronException;

/**
 * Represents an event task with a start and end time.
 */
public class Event extends Task {
    /** The start time or date of the event. */
    private LocalDateTime from;

    /** The end time or date of the event. */
    private LocalDateTime to;

    /**
     * Creates an event task.
     *
     * @param description the event description.
     * @param from the event start time.
     * @param to the event end time.
     * @throws BaronException if either event time has an invalid date format.
     */
    public Event(String description, String from, String to) throws BaronException {
        super(description);
        this.from = DateHandler.parse(from);
        this.to = DateHandler.parse(to);
        if (!this.from.isBefore(this.to)) {
            throw new BaronException("Event start must be earlier than its end.");
        }
    }

    /**
     * Returns the event time range to include in the task display.
     *
     * @return a formatted string containing the start and end times.
     */
    @Override
    public String extraInfo() {
        return " (from: " + DateHandler.format(from) + " to: " + DateHandler.format(to) + ")";
    }

    /**
     * Returns the type symbol for an event task.
     *
     * @return "E".
     */
    @Override
    public String getTypeSymbol() {
        return "E";
    }

    /**
     * Returns the string representation of the task for saving to a file.
     *
     * @return the formatted task status and description for file storage.
     */
    @Override
    public String serialize() {
        JsonObject taskData = new Gson().fromJson(super.serialize(), JsonObject.class);
        taskData.addProperty("from", DateHandler.format(from));
        taskData.addProperty("to", DateHandler.format(to));
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
        return from;
    }
}
