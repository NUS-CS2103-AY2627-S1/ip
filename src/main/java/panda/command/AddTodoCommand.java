package panda.command;

import panda.task.Task;
import panda.task.TaskList;

/**
 * Adds a to-do task to Panda's task list.
 */
public class AddTodoCommand extends AddTaskCommand {
    private final String description;

    /**
     * Creates a command for a to-do description.
     *
     * @param description Description of the task.
     */
    public AddTodoCommand(String description) {
        this.description = description;
    }

    /**
     * Adds the to-do task to the supplied task list.
     *
     * @param taskList Active task list.
     * @return The newly added {@link panda.task.Todo} task.
     */
    @Override
    protected Task addTask(TaskList taskList) {
        return taskList.addTodo(description);
    }
}
