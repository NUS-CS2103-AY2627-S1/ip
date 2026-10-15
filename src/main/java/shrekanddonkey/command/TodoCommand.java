package shrekanddonkey.command;

import shrekanddonkey.exception.ShrekAndDonkeyException;
import shrekanddonkey.storage.Storage;
import shrekanddonkey.task.TaskList;
import shrekanddonkey.task.Todo;
import shrekanddonkey.ui.Ui;

/**
 * Represents the command that adds a simple to-do task.
 */
public class TodoCommand extends Command {
    private final String arguments;

    /**
     * Creates a to-do command with its raw task description.
     *
     * @param arguments text following the {@code todo} command.
     */
    public TodoCommand(String arguments) {
        this.arguments = arguments;
    }

    /**
     * Adds a to-do task, or displays an error when the description is empty.
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
            if (arguments.isEmpty()) {
                throw new ShrekAndDonkeyException("todo");
            }
            assert !arguments.isEmpty() : "Arguments should not be empty when adding todo";
            tasks.add(new Todo(arguments));
            ui.showTaskAdded(tasks.get(tasks.size() - 1), tasks.size());
        } catch (ShrekAndDonkeyException e) {
            ui.showError("OOPS!!UWU description of a " + e.getMessage() + " cannot be empty UwU");
        }
        ui.showDivider();
    }

    @Override
    public boolean modifiesTasks() {
        return true;
    }
}
