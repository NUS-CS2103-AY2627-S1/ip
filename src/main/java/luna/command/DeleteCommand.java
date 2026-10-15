package luna.command;

import luna.exception.LunaException;
import luna.parser.Parser;
import luna.storage.Storage;
import luna.task.Task;
import luna.task.TaskList;

/**
 * Deletes a task from the task list.
 */
public class DeleteCommand extends Command {
    private final String commandArgs;
    private final TaskList tasks;
    private final Storage storage;

    /**
     * Creates a command that deletes the selected task.
     *
     * @param commandArgs User input after the command word.
     * @param tasks Task list to update.
     * @param storage Storage to update.
     */
    public DeleteCommand(String commandArgs, TaskList tasks, Storage storage) {
        this.commandArgs = commandArgs;
        this.tasks = tasks;
        this.storage = storage;
    }

    @Override
    public String execute() throws LunaException {
        int taskIndex = Parser.parseIndex(commandArgs, "delete", tasks.size());
        Task removedTask = tasks.remove(taskIndex);
        storage.save(tasks.getUnmodifiableList());

        return "Noted. I've removed this task:\n" + removedTask
                + "\nNow you have " + tasks.size() + " tasks in the list.";
    }
}
