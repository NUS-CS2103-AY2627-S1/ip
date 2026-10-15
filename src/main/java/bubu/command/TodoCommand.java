package bubu.command;

import bubu.exception.BubuException;
import bubu.storage.Storage;
import bubu.task.TaskList;
import bubu.task.ToDo;
import bubu.ui.Ui;

/**
 * Adds a to-do task.
 */
public class TodoCommand extends AddTaskCommand {
    private final String description;

    /**
     * Creates a to-do command for the supplied description.
     */
    public TodoCommand(String description) {
        this.description = description;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws BubuException {
        addTask(new ToDo(description), tasks, ui, storage);
    }
}
