package korvus.tasks;

import java.time.LocalDateTime;

import korvus.utils.DateTimeParser;

/**
 * Subclass of task, contains a start and end date.
 */
public class Event extends Task {
    private LocalDateTime start;
    private LocalDateTime end;

    /**
     * Returns an instance of the Event, a subclass of Task.
     *
     * @param isDone Boolean on whether task is completed.
     * @param name Name of Event.
     * @param start Datetime of when the event starts, as a String.
     * @param end Datetime of when the event ends, as a String.
     * @param dateTimeParser Parser for converting String to DateTime.
     * @throws InvalidTaskException If there is any errors in the above parameters for creating a task.
     */
    public Event(boolean isDone, String name, String start, String end,
                 DateTimeParser dateTimeParser) throws InvalidTaskException {
        super(isDone, name, dateTimeParser);
        this.start = dateTimeParser.parseDateString(start);
        this.end = dateTimeParser.parseDateString(end);
    }

    /**
     * Returns an instance of the Event, a subclass of Task.
     *
     * @param name Name of Event.
     * @param start Datetime of when the event starts, as a String.
     * @param end Datetime of when the event ends, as a String.
     * @param dateTimeParser Parser for converting String to DateTime.
     * @throws InvalidTaskException If there is any errors in the above parameters for creating a task.
     */
    public Event(String name, String start, String end, DateTimeParser dateTimeParser) throws InvalidTaskException {
        super(name, dateTimeParser);
        this.start = dateTimeParser.parseDateString(start);
        this.end = dateTimeParser.parseDateString(end);
    }

    /**
     * Returns an instance of the Event, a subclass of Task.
     *
     * @param name Name of Event.
     * @param dateTimeParser Parser for converting String to DateTime.
     * @throws InvalidTaskException If there is any errors in the above parameters for creating a task.
     */
    public Event(String name, DateTimeParser dateTimeParser) throws InvalidTaskException {
        this(name, null, null, dateTimeParser);
    }

    /**
     * Returns the time period of the event.
     *
     * @return Start and end dates of the event as a String.
     */
    public String getDuration() {
        return String.format("%s - %s",
                dateTimeParser.convertDateToString(start),
                dateTimeParser.convertDateToString(end));
    }

    /**
     * Edits the start datetime in this event.
     *
     * @param start Datetime of when the event starts.
     */
    public void setStart(LocalDateTime start) {
        this.start = start;
    }

    /**
     * Edits the end datetime in this event.
     *
     * @param end Datetime of when the event ends.
     */
    public void setEnd(LocalDateTime end) {
        this.end = end;
    }

    /**
     * @inheritDoc
     */
    @Override
    protected String writeToStore() {
        return String.format("%s%s%s%s%s%s%s",
                TaskType.EVENT.flag,
                DATA_SEP,
                super.writeToStore(),
                DATA_SEP,
                dateTimeParser.convertDateToString(this.start),
                DATA_SEP,
                dateTimeParser.convertDateToString(this.end));
    }

    /**
     * @inheritDoc
     */
    @Override
    public String toString() {
        return String.format("[E]%s (Duration: %s)", super.toString(), this.getDuration());
    }
}
