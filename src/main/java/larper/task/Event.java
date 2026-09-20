package larper.task;

import java.time.LocalDate;

/**
 * Represents a task that starts at one date and time and ends at another date and time.
 */
public class Event extends Task {
    private LocalDate startDate;
    private String startTime;
    private LocalDate endDate;
    private String endTime;

    /**
     * Creates an unmarked event task using ISO date strings.
     *
     * @param description Text that describes the event task.
     * @param startDate Start date in ISO format.
     * @param startTime Start time in normalized form, or "no time".
     * @param endDate End date in ISO format.
     * @param endTime End time in normalized form, or "no time".
     */
    public Event(String description, String startDate, String startTime, String endDate, String endTime) {
        this(description, LocalDate.parse(startDate), startTime, LocalDate.parse(endDate), endTime);
    }

    /**
     * Creates an unmarked event task using parsed dates.
     *
     * @param description Text that describes the event task.
     * @param startDate Start date.
     * @param startTime Start time in normalized form, or "no time".
     * @param endDate End date.
     * @param endTime End time in normalized form, or "no time".
     */
    public Event(String description, LocalDate startDate, String startTime, LocalDate endDate, String endTime) {
        super(description);
        assert startDate != null : "Event start date should be parsed before creating an event.";
        assert startTime != null && !startTime.isBlank() : "Event start time should be normalized before creation.";
        assert endDate != null : "Event end date should be parsed before creating an event.";
        assert endTime != null && !endTime.isBlank() : "Event end time should be normalized before creation.";
        this.startDate = startDate;
        this.startTime = startTime;
        this.endDate = endDate;
        this.endTime = endTime;
    }

    /**
     * Creates an unmarked event task by parsing start and end date-time strings.
     *
     * @param description Text that describes the event task.
     * @param start Start date-time text.
     * @param end End date-time text.
     */
    public Event(String description, String start, String end) {
        this(description, TaskDateTimeParser.parse(start, "no time"), TaskDateTimeParser.parse(end, "no time"));
    }

    /**
     * Creates an unmarked event task using parsed start and end date-time values.
     *
     * @param description Text that describes the event task.
     * @param start Parsed start date and time.
     * @param end Parsed end date and time.
     */
    public Event(String description, TaskDateTime start, TaskDateTime end) {
        this(description, start.getDate(), start.getTime(), end.getDate(), end.getTime());
    }

    /**
     * Returns the type icon for event tasks.
     */
    @Override
    public String getTypeIcon() {
        return "E";
    }

    /**
     * Returns a detached copy of this event task.
     */
    @Override
    public Task copy() {
        Event taskCopy = new Event(getDescription(), startDate, startTime, endDate, endTime);
        taskCopy.addTags(getTags());
        taskCopy.setDone(isDone());
        return taskCopy;
    }

    /**
     * Returns this event in the storage file format.
     */
    @Override
    public String toFileString() {
        assert startDate != null && endDate != null : "Event dates should be available before saving.";
        assert startTime != null && !startTime.isBlank() : "Event start time should be available before saving.";
        assert endTime != null && !endTime.isBlank() : "Event end time should be available before saving.";
        return getBaseFileString() + " | " + startDate + " | " + startTime + " | "
                + endDate + " | " + endTime + getTagsFileSuffix();
    }

    /**
     * Returns this event in the console display format.
     */
    @Override
    public String toString() {
        assert startDate != null && endDate != null : "Event dates should be available before display.";
        assert startTime != null && !startTime.isBlank() : "Event start time should be available before display.";
        assert endTime != null && !endTime.isBlank() : "Event end time should be available before display.";
        return formatWithTags(formatTaskWithoutTags() + " (from: " + formatDateTime(startDate, startTime)
                + " to: " + formatDateTime(endDate, endTime) + ")");
    }

    private String formatDateTime(LocalDate date, String time) {
        String formattedDate = TaskDateTimeParser.formatDate(date);
        if (isNoTime(time)) {
            return formattedDate;
        }
        return formattedDate + " " + time;
    }

    private boolean isNoTime(String time) {
        return time.trim().equalsIgnoreCase("no time");
    }
}
