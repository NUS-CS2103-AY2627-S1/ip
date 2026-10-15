package shrekanddonkey.command;

import shrekanddonkey.storage.Storage;
import shrekanddonkey.task.Task;
import shrekanddonkey.task.TaskList;
import shrekanddonkey.ui.Ui;

/**
 * Represents the command that removes a task by its one-based number.
 */
public class DeleteCommand extends Command {
    private final String arguments;

    /**
     * Creates a delete command with its raw task-number argument.
     *
     * @param arguments text following the {@code delete} command.
     */
    public DeleteCommand(String arguments) {
        this.arguments = arguments;
    }

    /**
     * Removes the selected task, or displays an error when the task number is invalid.
     *
     * @param tasks application's task list.
     * @param ui application's user interface.
     * @param storage application's file storage.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        assert tasks != null : "TaskList collaborator cannot be null";
        assert ui != null : "Ui collaborator cannot be null";
        assert storage != null : "Storage collaborator cannot be null";
        assert arguments != null : "Command arguments cannot be null";
        try {
            int taskIndex = Integer.parseInt(arguments) - 1;
            if (taskIndex < 0 || taskIndex >= tasks.size()) {
                ui.showError(" Pick a number from 1 to " + tasks.size() + ", not that hard!");
            } else {
                Task removedTask = tasks.remove(taskIndex);
                assert removedTask != null : "Removed task should not be null";
                ui.showMessage(" Poof! Banished from the swamp:");
                ui.showMessage("   " + removedTask);
                ui.showMessage(" Now ye have " + tasks.size() + " tasks in the swamp.");
            }
        } catch (NumberFormatException e) {
            ui.showError(" That's not a number! Use: delete N");
        }
        ui.showDivider();
    }

    @Override
    public boolean modifiesTasks() {
        return true;
    }
}
