package fifi.command;

import fifi.task.Task;

/**
 * Adds an event task to the task list.
 */
public class AddEventCommand extends AddTaskCommand {
    /**
     * Creates a command that adds the given event.
     *
     * @param task the event task to add
     */
    public AddEventCommand(Task task) {
        super(task);
    }
}
