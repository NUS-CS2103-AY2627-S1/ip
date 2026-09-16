package bobby;

/** Marks a selected task as not done and persists the updated task list. */
public class UnmarkCommand extends Command {
    /** Original user input, including the command and task number. */
    private final String commandInput;

    /**
     * Creates an unmark command containing the user's original input.
     *
     * @param commandInput original user input
     */
    public UnmarkCommand(String commandInput) {
        this.commandInput = commandInput;
    }

    /**
     * Marks the selected task as not done, rolling back if saving fails.
     *
     * @param tasks current task list to update
     * @param ui interface used to show the confirmation
     * @param storage persistence service used to save the updated list
     * @throws BobbyException if the task number is invalid or saving fails
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws BobbyException {
        Parser parser = new Parser();
        int taskIndex = parser.parseTaskIndex(commandInput, "unmark", tasks.size());
        Task task = tasks.get(taskIndex);
        assert task != null : "A valid task index must refer to a task";
        boolean wasDone = task.isDone();
        task.unmarkAsDone();
        try {
            storage.save(tasks);
        } catch (BobbyException exception) {
            restoreTaskStatus(task, wasDone);
            throw exception;
        }
        ui.showTaskMarkedNotDone(task);
    }
}
