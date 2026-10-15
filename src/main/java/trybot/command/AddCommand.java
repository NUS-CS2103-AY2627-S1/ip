package trybot.command;

import trybot.exception.TryBotException;
import trybot.storage.Storage;
import trybot.task.Task;
import trybot.task.TaskList;
import trybot.ui.Ui;

/**
 * Base class for commands that create and add one task.
 */
public abstract class AddCommand extends Command {
    /**
     * Creates the task represented by this command.
     *
     * @return task to add
     * @throws TryBotException if the task fields are invalid
     */
    protected abstract Task createTask() throws TryBotException;

    /**
     * Creates, stores, and reports the task represented by this command.
     *
     * @param tasks current task list.
     * @param ui user-interface handler.
     * @param storage task persistence handler.
     * @throws TryBotException if the task fields are invalid
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws TryBotException {
        Task task = createTask();
        try {
            tasks.add(task);
        } catch (IllegalArgumentException exception) {
            throw new TryBotException(exception.getMessage() == null
                    ? "That task could not be added." : exception.getMessage());
        }
        saveTasks(tasks, ui, storage);
        ui.showTaskAdded(task, tasks.size());
    }

    /**
     * Indicates that adding a task does not end the session.
     *
     * @return always false
     */
    @Override
    public boolean isExit() {
        return false;
    }
}
