package carl.commands;

/**
 * Represents a command to add a task to the task list.
 */
public abstract class AddTaskCommand extends ModifyTaskCommand {
    /** Name or description of the task to be added. */
    protected final String name;

    /**
     * Constructs an {@code AddTaskCommand} with the specified task name.
     *
     * @param name Name or description of the task.
     */
    public AddTaskCommand(String name) {
        this.name = name;
    }
}
