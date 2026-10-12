package echo.command;

import echo.task.TaskManager;
import echo.ui.Ui;

/**
 * Represents the command to list all tasks.
 */
public class ListCommand extends Command {
    /**
     * Creates a list command.
     */
    public ListCommand() {
        super(Type.LIST, java.util.List.of());
    }

    /**
     * Displays all tasks managed by E.C.H.O.
     */
    @Override
    public String execute(TaskManager taskManager, Ui ui) {
        return ui.showTaskList(taskManager.getTasks());
    }
}
