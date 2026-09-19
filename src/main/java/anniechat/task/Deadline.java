package anniechat.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/** Represents a task that must be completed by a specific date. */
public class Deadline extends Task {
    // Example: deadline return book /by 2019-10-15
    private final LocalDate deadline;
    private final String taskOnly;
    private final String taskDesc;

    /**
     * Creates a deadline task from a command containing an ISO date.
     *
     * @param desc command containing the task description and ISO date.
     */
    public Deadline(String desc) {
        super(desc);
        String descDeadline = desc.split(" ", 2)[1];
        String[] parts = descDeadline.split("/by ", 2);
        taskOnly = parts[0].trim();
        deadline = LocalDate.parse(parts[1].trim());
        taskDesc = taskOnly + " (by: "
                + deadline.format(OUTPUT_FORMAT) + ")";
    }

    @Override
    public String getTaskDesc() {
        return taskDesc;
    }
    @Override
    public String getTaskIcon() {
        return "D";
    }

    @Override
    public String toSaveFormat() {
        return this.getTaskIcon() + " | " + (isDone ? "1" : "0")
                + " | " + this.taskOnly + " | " + this.deadline;
    }

    private static final DateTimeFormatter OUTPUT_FORMAT =
            DateTimeFormatter.ofPattern("MMM d yyyy", Locale.ENGLISH);
}
