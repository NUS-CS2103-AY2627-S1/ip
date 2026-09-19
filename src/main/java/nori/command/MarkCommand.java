package nori.command;

import nori.NoriException;
import nori.storage.Storage;
import nori.task.Task;
import nori.task.TaskList;
import nori.ui.Ui;

/**
 * Marks a task as complete.
 */
class MarkCommand extends InputCommand {
    /**
     * Creates a command that completes a task.
     *
     * @param details the task number after the {@code mark} keyword.
     */
    MarkCommand(String details) {
        super(details);
    }

    /** {@inheritDoc} */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws NoriException {
        int taskIndex = tasks.getTaskIndex(details, "mark");
        Task task = tasks.get(taskIndex);
        if (task.isDone()) {
            ui.showResponse("Noot noot! That task is already frozen solid (done).");
            return;
        }

        task.markAsDone();
        try {
            storage.saveTasks(tasks.asUnmodifiableList());
        } catch (NoriException exception) {
            task.markAsNotDone();
            throw exception;
        }
        ui.showResponse("Noot noot! This task is now ice-cold complete:", "  " + task);
    }
}
