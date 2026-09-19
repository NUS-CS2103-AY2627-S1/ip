package nori.command;

import nori.NoriException;
import nori.storage.Storage;
import nori.task.Task;
import nori.task.TaskList;
import nori.ui.Ui;

/**
 * Marks a task as incomplete.
 */
class UnmarkCommand extends InputCommand {
    /**
     * Creates a command that reopens a task.
     *
     * @param details the task number after the {@code unmark} keyword.
     */
    UnmarkCommand(String details) {
        super(details);
    }

    /** {@inheritDoc} */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws NoriException {
        int taskIndex = tasks.getTaskIndex(details, "unmark");
        Task task = tasks.get(taskIndex);
        if (!task.isDone()) {
            ui.showResponse("Noot noot! That task is already thawed (not done).");
            return;
        }

        task.markAsNotDone();
        try {
            storage.saveTasks(tasks.asUnmodifiableList());
        } catch (NoriException exception) {
            task.markAsDone();
            throw exception;
        }
        ui.showResponse("Brrr... thawing this task back out:", "  " + task);
    }
}
