package larper.task;

import java.time.LocalDate;

/**
 * Stores a parsed task date together with its optional time.
 */
public class TaskDateTime {
    private LocalDate date;
    private String time;

    /**
     * Creates a task date-time value.
     *
     * @param date Parsed date of the task.
     * @param time Normalized time of the task, or "no time".
     */
    public TaskDateTime(LocalDate date, String time) {
        assert date != null : "TaskDateTime should always contain a parsed date.";
        assert time != null : "TaskDateTime should always contain a time value or an empty pending marker.";
        this.date = date;
        this.time = time;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }
}
