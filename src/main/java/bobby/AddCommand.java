package bobby;

/** Adds a parsed task to the task list and persists the change. */
public class AddCommand extends Command {
    /** Task to add and persist when this command executes. */
    private final Task task;

    /**
     * Creates an add command for the given task.
     *
     * @param task task to add
     */
    public AddCommand(Task task) {
        assert task != null : "An add command must contain a task";
        this.task = task;
    }

    /**
     * Adds the task, saving it and rolling back the addition if saving fails.
     *
     * @param tasks current task list to update
     * @param ui interface used to show the confirmation
     * @param storage persistence service used to save the updated list
     * @throws BobbyException if saving the updated list fails
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws BobbyException {
        if (tasks.containsTaskWithSameDetails(task)) {
            throw new BobbyException("Error! This task already exists in the list.");
        }
        tasks.add(task);
        try {
            storage.save(tasks);
        } catch (BobbyException exception) {
            tasks.remove(tasks.size() - 1);
            throw exception;
        }
        ui.showTaskAdded(task, tasks.size());
    }
}
