package herta.command;

import herta.task.Deadline;

/**
 * Represents the command that adds a deadline task.
 */
public class DeadlineCommand extends AddCommand {

    /**
     * Creates a command that adds the given deadline task.
     *
     * @param deadline the deadline task to add.
     */
    public DeadlineCommand(Deadline deadline) {
        super(deadline);
    }
}
