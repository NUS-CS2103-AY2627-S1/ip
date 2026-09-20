package grower.commands;

import grower.tasks.Task;
import grower.tasks.TaskList;
import grower.tasks.ToDo;
import grower.ui.Ui;

/**
 * Represents a command that adds a to-do task.
 */
public class ToDoCommand extends Command {
    private final String description;

    /**
     * Creates a command that adds a to-do task.
     *
     * @param description Description of the task.
     */
    public ToDoCommand(String description) {
        this.description = description;
    }

    @Override
    public boolean execute(TaskList tasks, Ui ui) {
        Task newTask = new ToDo(description);
        tasks.addTask(newTask);
        ui.showTaskAdded(newTask);
        return true;
    }
}
