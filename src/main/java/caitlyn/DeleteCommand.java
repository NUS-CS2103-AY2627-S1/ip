package caitlyn;

import java.util.List;

/**
 * A command that removes one task from the list.
 */
public final class DeleteCommand extends Command {
    /** The complete delete command. */
    private final String command;

    /**
     * Creates a delete command with its unparsed arguments.
     *
     * @param command the complete delete command entered by the user.
     */
    public DeleteCommand(String command) {
        this(command, new TaskStorage());
    }

    /**
     * Creates a command with a supplied destination for task changes.
     *
     * @param command the command input.
     * @param storage the destination for task changes.
     */
    DeleteCommand(String command, TaskStorage storage) {
        super(storage);
        this.command = command;
    }

    /**
     * Removes the selected task and saves the shortened task list.
     *
     * @param tasks the current task list.
     * @param ui the UI used to display the result.
     * @throws CaitlynException when the task number is invalid or saving fails.
     */
    @Override
    public void execute(List<Task> tasks, Ui ui) throws CaitlynException {
        int taskIndex = parseTaskIndex(command, "delete", tasks);
        Task removedTask = tasks.remove(taskIndex);
        try {
            saveTasks(tasks);
        } catch (CaitlynException exception) {
            tasks.add(taskIndex, removedTask);
            throw exception;
        }
        ui.showTaskDeleted(removedTask, tasks.size());
    }
}
