package wally;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.util.Locale;

/**
 * Stores a task with a description and deadline.
 */
public class Deadline extends Task {
    private final LocalDateTime endDate;

    /**
     * Creates a deadline task with the specified name and due date.
     *
     * @param name Name of the task.
     * @param endDate Deadline in the expected date-time format.
     */
    public Deadline(String name, String endDate) {
        super(name);
        this.endDate = LocalDateTime.parse(endDate,
                DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm").withResolverStyle(ResolverStyle.STRICT));
    }

    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: "
                + this.endDate.format(DateTimeFormatter.ofPattern("d MMM yyyy, HH:mm", Locale.ENGLISH)) + ")";
    }

    @Override
    public String getCommand() {
        return "deadline " + super.getCommand() + " /by "
                + this.endDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }

    @Override
    public LocalDateTime getScheduleDateTime() {
        return endDate;
    }
}
