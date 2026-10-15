package trybot.command;

import trybot.exception.TryBotException;
import trybot.storage.Storage;
import trybot.task.Task;
import trybot.task.TaskList;
import trybot.ui.Ui;

/**
 * Marks one task as done.
 */
public class MarkCommand extends Command {
    private final int taskNumber;

    /**
     * Creates a mark command.
     *
     * @param taskNumber one-based task number.
     */
    public MarkCommand(int taskNumber) {
        this.taskNumber = taskNumber;
    }

    /**
     * Marks the selected task as done, saves the updated list, and reports the result.
     *
     * @param tasks current task list.
     * @param ui user-interface handler.
     * @param storage task persistence handler.
     * @throws TryBotException if the task number is outside the list
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws TryBotException {
        if (taskNumber < 1 || taskNumber > tasks.size()) {
            throw new TryBotException("That task number does not exist. Use list to see your task numbers.");
        }

        Task task = tasks.get(taskNumber - 1);
        // The task-list invariant guarantees that a valid index yields a task.
        assert task != null;
        task.markAsDone();
        saveTasks(tasks, ui, storage);
        ui.showTaskMarkedDone(task);
    }

    /**
     * Indicates that marking a task does not end the session.
     *
     * @return always false
     */
    @Override
    public boolean isExit() {
        return false;
    }
}
