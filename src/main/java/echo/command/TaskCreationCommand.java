package echo.command;

import java.util.List;

import echo.task.Task;
import echo.task.TaskManager;
import echo.ui.Ui;

/**
 * Base class for commands that create tasks from a description.
 */
public abstract class TaskCreationCommand extends Command {
    /**
     * Creates a task-creation command.
     *
     * @param type command type.
     * @param arguments validated task-creation command arguments.
     */
    protected TaskCreationCommand(Type type, List<String> arguments) {
        super(type, arguments);
    }

    /**
     * Returns the task description.
     */
    protected String getDescription() {
        return getArgument(0);
    }

    /**
     * Displays a newly created task using the supplied application services.
     *
     * @param task newly created task.
     * @param taskManager task manager used to obtain the task count.
     * @param ui interface used to display the task.
     * @return the task added message.
     */
    protected String showAdded(Task task, TaskManager taskManager, Ui ui) {
        return ui.showAdded(task, taskManager.size());
    }
}
