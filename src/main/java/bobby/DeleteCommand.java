package bobby;

/** Deletes a selected task and persists the updated task list. */
public class DeleteCommand extends Command {
    /** Original user input, including the command and task number. */
    private final String commandInput;

    /**
     * Creates a delete command containing the user's original input.
     *
     * @param commandInput original user input
     */
    public DeleteCommand(String commandInput) {
        this.commandInput = commandInput;
    }

    /**
     * Deletes the selected task, restoring it if saving fails.
     *
     * @param tasks current task list to update
     * @param ui interface used to show the confirmation
     * @param storage persistence service used to save the updated list
     * @throws BobbyException if the task number is invalid or saving fails
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws BobbyException {
        Parser parser = new Parser();
        int taskIndex = parser.parseTaskIndex(commandInput, "delete", tasks.size());
        Task deletedTask = tasks.remove(taskIndex);
        assert deletedTask != null : "A valid task index must refer to a task";
        try {
            storage.save(tasks);
        } catch (BobbyException exception) {
            tasks.add(taskIndex, deletedTask);
            throw exception;
        }
        ui.showTaskDeleted(deletedTask, tasks.size());
    }
}
