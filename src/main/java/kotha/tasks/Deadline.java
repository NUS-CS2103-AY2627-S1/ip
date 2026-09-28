package kotha.tasks;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/** Represents a task with a deadline. */
public class Deadline extends kotha.tasks.Task {
    private static final DateTimeFormatter DISPLAY_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd uuuu hh:mma");
    protected LocalDateTime deadline;

    /** Creates an incomplete task with the supplied description and deadline. */
    public Deadline(String description, LocalDateTime deadline) {
        super(description);
        this.deadline = deadline;
    }

    /** Returns the deadline text for persistent storage. */
    public LocalDateTime getDeadline() {
        return deadline;
    }

    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + deadline.format(DISPLAY_FORMAT) + ")";
    }

    @Override
    public void printAddText() {
        System.out.println("Master I have added the following task:");
        System.out.println(this.toString());
    }
}
