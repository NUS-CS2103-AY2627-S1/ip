package cassava.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Represents a Event-type task.
 */
public class EventTask extends Task {

    private LocalDate from;
    private LocalDate to;

    /**
     * Default constructor for EventTask.
     * @param description Text description of event.
     * @param completed Completion status.
     * @param taskPriority Priority of event.
     * @param from Date which event starts.
     * @param to Date which event ends.
     */
    public EventTask(
            String description,
            boolean completed,
            TaskPriority taskPriority,
            LocalDate from,
            LocalDate to
    ) {
        super(description, completed, taskPriority);
        this.from = from;
        this.to = to;
    }

    /**
     * Creates an EventTask with completed = false,
     * and taskPriority = UNKNOWN.
     * @param description Text description of event.
     * @param from Date which event starts.
     * @param to Date which event ends.
     */
    public EventTask(String description, LocalDate from, LocalDate to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    @Override
    public String toString() {
        return super.toString()
                + " (from: " + from.format(DateTimeFormatter.ofPattern("MMM d yyyy"))
                + " to: " + to.format(DateTimeFormatter.ofPattern("MMM d yyyy")) + ")";
    }

    @Override
    public String getTaskTypeToken() {
        return "E";
    }

    @Override
    public String toDataString() {
        return super.toDataString() + " | "
                + this.from.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + " | "
                + this.to.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }
}
