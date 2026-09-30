package bean.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/** Represents a task that occurs between two dates. */
public class Event extends Task {
    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("MMM dd yyyy", Locale.ENGLISH);

    private final LocalDate from;
    private final LocalDate to;

    Event(String task, LocalDate from, LocalDate to, int position) {
        super(task, position);
        this.tag = "[E]";
        this.from = from;
        this.to = to;
    }
    Event(String task, LocalDate from, LocalDate to, Priority priority, int position) {
        super(task, priority, position);
        this.tag = "[E]";
        this.from = from;
        this.to = to;
    }

    @Override
    public String[] get() {
        return new String[] { isDone ? "1" : "0", task, from.toString(),
                to.toString(), String.valueOf(priority.getLevel())};
    }

    @Override
    public String toString() {
        return priority.toString() + " " + tag + (isDone ? "[X] " : "[ ] ") + task + " (from: "
                + from.format(DATE_FORMATTER) + " to: " + to.format(DATE_FORMATTER) + ")";
    }
}
