package fifi.command;

import fifi.task.Task;

/**
 * Adds a deadline task to the task list.
 */
public class AddDeadlineCommand extends AddTaskCommand {
    /**
     * Creates a command that adds the given deadline.
     *
     * @param task the deadline task to add
     */
    public AddDeadlineCommand(Task task) {
        super(task);
    }
}
