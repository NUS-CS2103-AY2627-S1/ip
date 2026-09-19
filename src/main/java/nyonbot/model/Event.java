package nyonbot.model;

import java.time.LocalDateTime;

import nyonbot.Ui;

/**
 * Represents an event task with a start time and end time.
 */
public class Event extends Task {
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    /**
     * Creates a Event task with thie given name, start time and end time.
     *
     * @param taskName the name of the event.
     * @param start the start time of the event.
     * @param end the end time of the event.
     */
    public Event(String taskName, LocalDateTime start, LocalDateTime end) {
        super(taskName);
        this.startTime = start;
        this.endTime = end;
    }

    /**
     * Returns the start and end time as an array.
     *
     * @return an array containing the start time and end time.
     */
    public LocalDateTime[] getEventTimes() {
        LocalDateTime[] eventTime = {this.startTime, this.endTime};
        return eventTime;
    }

    @Override
    public String toString() {
        return String.format("[E][%s] %s (from: %s to: %s)",
                this.isDone ? "X" : " ",
                this.taskName,
                Ui.showDate(this.startTime),
                Ui.showDate(this.endTime)
        );
    }
}
