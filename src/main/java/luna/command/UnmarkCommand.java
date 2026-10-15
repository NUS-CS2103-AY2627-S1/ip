package luna.command;

import luna.exception.LunaException;
import luna.parser.Parser;
import luna.storage.Storage;
import luna.task.Task;
import luna.task.TaskList;

/**
 * Marks a completed task as not done.
 */
public class UnmarkCommand extends Command {
    private final String commandArgs;
    private final TaskList tasks;
    private final Storage storage;

    /**
     * Creates a command that marks the selected task as not done.
     *
     * @param commandArgs User input after the command word.
     * @param tasks Task list to update.
     * @param storage Storage to update.
     */
    public UnmarkCommand(String commandArgs, TaskList tasks, Storage storage) {
        this.commandArgs = commandArgs;
        this.tasks = tasks;
        this.storage = storage;
    }

    @Override
    public String execute() throws LunaException {
        int taskIndex = Parser.parseIndex(commandArgs, "unmark", tasks.size());
        Task task = tasks.get(taskIndex);

        if (!task.isDone()) {
            throw new LunaException("This task is not yet marked as done.");
        }

        task.markUndone();
        storage.save(tasks.getUnmodifiableList());
        return "OK, I've marked this task as not done yet:\n" + task;
    }
}
