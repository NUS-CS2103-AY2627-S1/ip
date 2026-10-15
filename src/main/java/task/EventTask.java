package task;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Represents a task that spans a start and end time.
 */
public class EventTask extends Task {
    private static final DateTimeFormatter DEFAULT_TIME_FORMAT =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private DateTimeFormatter timeFormat = DEFAULT_TIME_FORMAT;
    private LocalDateTime startTime = LocalDateTime.now().plusHours(1);
    private LocalDateTime endTime = LocalDateTime.now().plusHours(2);

    /**
     * Creates an undone event task using a custom input format.
     *
     * @param description the task description
     * @param startTime the start time as a formatted string
     * @param endTime the end time as a formatted string
     * @param format the custom date format used by the input strings
     */
    public EventTask(String description, String startTime, String endTime, String format) {
        super(description);
        DateTimeFormatter tempFormat = DateTimeFormatter.ofPattern(format);
        this.timeFormat = DEFAULT_TIME_FORMAT;
        this.startTime = LocalDateTime.parse(startTime, tempFormat);
        this.endTime = LocalDateTime.parse(endTime, tempFormat);
    }

    /**
     * Creates an undone event task using the default format.
     *
     * @param description the task description
     * @param startTime the start time as a formatted string
     * @param endTime the end time as a formatted string
     */
    public EventTask(String description, String startTime, String endTime) {
        super(description);
        this.timeFormat = DEFAULT_TIME_FORMAT;
        this.startTime = LocalDateTime.parse(startTime, this.timeFormat);
        this.endTime = LocalDateTime.parse(endTime, this.timeFormat);
    }

    /**
     * Creates an undone event task with LocalDateTime values.
     *
     * @param description the task description
     * @param startTime the start time
     * @param endTime the end time
     */
    public EventTask(String description, LocalDateTime startTime, LocalDateTime endTime) {
        super(description);
        assert startTime != null && endTime != null : "Event tasks must have start and end times";
        this.timeFormat = DEFAULT_TIME_FORMAT;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    /**
     * Creates an event task with a specified completion state using a custom input format.
     *
     * @param description the task description
     * @param done whether the task is already complete
     * @param startTime the start time as a formatted string
     * @param endTime the end time as a formatted string
     * @param format the custom date format used by the input strings
     */
    public EventTask(String description, boolean done, String startTime, String endTime, String format) {
        super(description, done);
        DateTimeFormatter tempFormat = DateTimeFormatter.ofPattern(format);
        this.timeFormat = DEFAULT_TIME_FORMAT;
        this.startTime = LocalDateTime.parse(startTime, tempFormat);
        this.endTime = LocalDateTime.parse(endTime, tempFormat);
    }

    /**
     * Creates an event task with a specified completion state using the default format.
     *
     * @param description the task description
     * @param done whether the task is already complete
     * @param startTime the start time as a formatted string
     * @param endTime the end time as a formatted string
     */
    public EventTask(String description, boolean done, String startTime, String endTime) {
        super(description, done);
        this.timeFormat = DEFAULT_TIME_FORMAT;
        this.startTime = LocalDateTime.parse(startTime, this.timeFormat);
        this.endTime = LocalDateTime.parse(endTime, this.timeFormat);
    }

    /**
     * Creates an event task with a specified completion state and LocalDateTime values.
     *
     * @param description the task description
     * @param done whether the task is already complete
     * @param startTime the start time
     * @param endTime the end time
     */
    public EventTask(String description, boolean done, LocalDateTime startTime, LocalDateTime endTime) {
        super(description, done);
        assert startTime != null && endTime != null : "Event tasks must have start and end times";
        this.timeFormat = DEFAULT_TIME_FORMAT;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    /**
     * Returns the start time of the event.
     *
     * @return the start time
     */
    public LocalDateTime getStartTime() {
        return startTime;
    }

    /**
     * Returns the end time of the event.
     *
     * @return the end time
     */
    public LocalDateTime getEndTime() {
        return endTime;
    }

    /**
     * Sets the start time of the event.
     *
     * @param startTime the new start time
     */
    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    /**
     * Sets the end time of the event.
     *
     * @param endTime the new end time
     */
    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    /**
     * Checks whether another event task has the same description and time range.
     * Completion state is not considered when comparing tasks.
     *
     * @param other the object to compare with this task
     * @return true if both objects represent the same event task
     */
    @Override
    public boolean equals(Object other) {
        if (!super.equals(other)) {
            return false;
        }
        EventTask otherTask = (EventTask) other;
        return this.startTime.equals(otherTask.startTime)
                && this.endTime.equals(otherTask.endTime);
    }

    @Override
    public String toSignature() {
        if (this.isDone()) {
            return "E|1|" + getDescription() + "|" + this.startTime.format(timeFormat)
                    + "|" + this.endTime.format(timeFormat);
        }
        return "E|0|" + getDescription() + "|" + this.startTime.format(timeFormat)
                + "|" + this.endTime.format(timeFormat);
    }

    @Override
    public String toString() {
        if (this.isDone()) {
            return "[E][X] " + getDescription() + " (from: " + this.startTime.format(timeFormat)
                    + " to: " + this.endTime.format(timeFormat) + ")";
        }
        return "[E][ ] " + getDescription() + " (from: " + this.startTime.format(timeFormat)
                + " to: " + this.endTime.format(timeFormat) + ")";
    }
}
