package nori.command;

import nori.NoriException;
import nori.storage.Storage;
import nori.task.Task;
import nori.task.TaskList;
import nori.ui.Ui;

/**
 * Removes a task from the list.
 */
class DeleteCommand extends InputCommand {
    /**
     * Creates a command that removes a task.
     *
     * @param details the task number after the {@code delete} keyword.
     */
    DeleteCommand(String details) {
        super(details);
    }

    /** {@inheritDoc} */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws NoriException {
        int taskIndex = tasks.getTaskIndex(details, "delete");
        Task deletedTask = tasks.remove(taskIndex);
        assert taskIndex <= tasks.size()
                : "Removing at taskIndex leaves that index insertable, which a failed save relies on.";
        try {
            storage.saveTasks(tasks.asUnmodifiableList());
        } catch (NoriException exception) {
            tasks.add(taskIndex, deletedTask);
            throw exception;
        }
        ui.showResponse("Splash! I kicked this task off the iceberg:", "  " + deletedTask,
                "The iceberg now holds " + tasks.size() + " task(s).");
    }
}
