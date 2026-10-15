package clover.command;

import clover.exception.CloverException;
import clover.parser.Parser;
import clover.parser.TaskArguments;
import clover.storage.Storage;
import clover.task.TaskList;
import clover.task.ToDo;
import clover.tutoree.TutoreeList;
import clover.ui.Ui;

/**
 * Adds a todo task.
 */
public class ToDoCommand extends Command {
    private final String description;

    /**
     * Creates a command with the todo description entered by the user.
     */
    public ToDoCommand(String description) {
        this.description = description;
    }

    /**
     * Validates, adds, and saves the todo task.
     */
    @Override
    public void execute(TaskList tasks, TutoreeList tutorees, Ui ui, Storage storage) throws CloverException {
        Parser.rejectUnknownParameters(description, "/for");
        TaskArguments parsedArguments = Parser.parseTaskArguments(description);
        String taskDescription = parsedArguments.getTaskArguments();
        if (taskDescription.isEmpty()) {
            throw new CloverException("To plant a task, use: todo DESCRIPTION. Optional: add /for TUTOREE NAME.");
        }
        String tutoreeName = validateTutoreeName(parsedArguments.getTutoreeName(), tutorees);
        ToDo task = new ToDo(taskDescription, tutoreeName);
        addTask(tasks, task, storage);
        ui.showTaskAdded(task, tasks.size());
    }

    @Override
    public CommandResponseStyle getResponseStyle() {
        return CommandResponseStyle.TASK_ADDED;
    }

    /** Adds a non-duplicate task and restores the list if persistence fails. */
    private void addTask(TaskList tasks, ToDo task, Storage storage) throws CloverException {
        if (tasks.containsEquivalent(task)) {
            throw new CloverException("That study quest is already in the grove.");
        }
        tasks.add(task);
        try {
            saveTasks(tasks, storage);
        } catch (CloverException exception) {
            tasks.remove(tasks.size() - 1);
            throw exception;
        }
    }
}
