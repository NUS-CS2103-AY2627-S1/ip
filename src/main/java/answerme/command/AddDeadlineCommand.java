package answerme.command;

import java.time.LocalDateTime;

import answerme.task.Deadline;

/**
 * Represents a command that adds a deadline task to the task list.
 */
public class AddDeadlineCommand extends AddCommand {
    /**
     * Creates a command that adds a deadline task with the specified description
     * and deadline.
     *
     * @param description The description of the deadline task.
     * @param deadline The deadline date and time.
     */
    public AddDeadlineCommand(String description, LocalDateTime deadline) {
        super(new Deadline(description, deadline));
    }
}
