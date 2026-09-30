package bean.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/** Represents a task that must be completed by a date. */
public class Deadline extends Task {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MMM dd yyyy");

    private final LocalDate date;

    Deadline(String task, LocalDate date, int position) {
        super(task, position);
        this.tag = "[D]";
        this.date = date;
    }

    Deadline(String task, LocalDate date, Priority priority, int position) {
        super(task, priority, position);
        this.tag = "[D]";
        this.date = date;
    }

    public String getDate() {
        return date.format(DATE_FORMATTER);
    }

    @Override
    public String[] get() {
        return new String[] { isDone ? "1" : "0", task, date.toString(), String.valueOf(priority.getLevel()) };
    }

    @Override
    public String toString() {
        return priority.toString() + " " + tag + (isDone ? "[X] " : "[ ] ") + task + " (by: "
                + date.format(DATE_FORMATTER) + ")";
    }
}
