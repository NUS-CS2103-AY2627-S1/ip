package carl.commands;

import carl.exceptions.CarlException;
import carl.task.Task;
import carl.task.TaskList;
import carl.task.TaskManager;
import carl.task.Todo;
import carl.ui.Ui;

/**
 * Represents a command to create and add a Todo task.
 */
public class TodoCommand extends AddTaskCommand {

    /**
     * Constructs a {@code TodoCommand} with the specified task name.
     *
     * @param name Name or description of the todo task.
     */
    public TodoCommand(String name) {
        super(name);
    }


    /**
     * {@inheritDoc}
     * Adds the todo task to the task list, saves the list, and informs the user.
     *
     * @param ui      User interface for interacting with the user.
     * @param storage Task manager handling task data persistence.
     * @param tasks   List of current tasks.
     * @param raw     Raw input string entered by the user.
     * @return result describing the added todo and updated task count.
     * @throws CarlException If an error occurs during execution.
     */
    @Override
    public CommandResult execute(Ui ui, TaskManager storage, TaskList tasks, String raw) throws CarlException {
        Task task = new Todo(name);
        tasks.addTaskToList(task);

        return CommandResult.createSuccess(ui.showAddTask(task, tasks.getTasksLeft()));
    }
}
