package hermes.task;

import java.time.LocalDateTime;

/** A task that runs between two moments, measured by its start time. */
public class Event extends Task {

    protected LocalDateTime start;
    protected LocalDateTime end;

    /** Creates an event that is not yet completed. */
    public Event(String taskDescription, LocalDateTime start, LocalDateTime end) {
        super(taskDescription);
        this.start = start;
        this.end = end;
    }

    /** Creates an event in a known state from its stored ISO-8601 dates. */
    public Event(boolean isCompleted, String taskDescription, String start, String end) {
        super(isCompleted, taskDescription);
        this.start = LocalDateTime.parse(start);
        this.end = LocalDateTime.parse(end);
    }

    @Override
    public Event copy() {
        return new Event(this.isCompleted, this.taskDescription, this.start.toString(), this.end.toString());
    }

    @Override
    public LocalDateTime getDueDateTime() {
        return this.start;
    }

    @Override
    public String getFileContent() {
        return String.format("%s | %d | %s | %s | %s",
                TaskType.EVENT.getSymbol(),
                this.isCompleted ? 1 : 0,
                this.taskDescription,
                this.start,
                this.end);
    }

    @Override
    public String toString() {
        return String.format("[E]%s (from: %s to: %s)",
                super.toString(), start.format(DISPLAY_FORMATTER), end.format(DISPLAY_FORMATTER));
    }
}
