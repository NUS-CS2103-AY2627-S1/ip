package pdd.command;

import pdd.task.Deadline;

/** Adds an already-parsed {@link Deadline} to the task list. */
public class DeadlineCommand extends AddTaskCommand {
    /** Creates a command that will add the given already-parsed deadline to the task list. */
    public DeadlineCommand(Deadline deadline) {
        super(deadline);
    }
}
