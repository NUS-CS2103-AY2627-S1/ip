package monday.task;

import java.time.LocalDate;
import java.time.LocalTime;

import monday.util.DateTimeFormat;

/**
 * Represents a task scheduled between a start and end date, with optional times.
 */
public class Event extends Task {
    private final LocalDate startDate;
    private final LocalTime startTime;
    private final LocalDate endDate;
    private final LocalTime endTime;

    /**
     * Creates an incomplete event task with the given schedule.
     */
    public Event(String description, LocalDate startDate, LocalTime startTime,
            LocalDate endDate, LocalTime endTime) {
        this(description, false, startDate, startTime, endDate, endTime, "");
    }

    /**
     * Creates an incomplete event task with the given schedule and notes.
     */
    public Event(String description, LocalDate startDate, LocalTime startTime,
            LocalDate endDate, LocalTime endTime, String notes) {
        this(description, false, startDate, startTime, endDate, endTime, notes);
    }

    /**
     * Creates an event task with the given schedule and completion status.
     */
    public Event(String description, boolean isDone, LocalDate startDate, LocalTime startTime,
            LocalDate endDate, LocalTime endTime) {
        this(description, isDone, startDate, startTime, endDate, endTime, "");
    }

    /**
     * Creates an event task with the given schedule, completion status, and notes.
     */
    public Event(String description, boolean isDone, LocalDate startDate, LocalTime startTime,
            LocalDate endDate, LocalTime endTime, String notes) {
        super(description, isDone, notes);
        this.startDate = startDate;
        this.startTime = startTime;
        this.endDate = endDate;
        this.endTime = endTime;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    @Override
    public String toString() {
        return "[E]" + formatTask()
                + " (from: " + formatDateTime(startDate, startTime)
                + " to: " + formatDateTime(endDate, endTime) + ")" + formatNotes();
    }

    /**
     * Formats a date and its optional time for display.
     */
    private String formatDateTime(LocalDate date, LocalTime time) {
        String formatted = date.format(DateTimeFormat.DISPLAY_DATE.getFormatter());

        if (time != null) {
            formatted += " " + time.format(DateTimeFormat.DISPLAY_TIME.getFormatter());
        }

        return formatted;
    }
}
