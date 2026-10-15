package luna.command;

import luna.exception.LunaException;
import luna.parser.Parser;
import luna.storage.Storage;
import luna.task.Task;
import luna.task.TaskList;

/**
 * Marks a task as done.
 */
public class MarkCommand extends Command {
    private final String commandArgs;
    private final TaskList tasks;
    private final Storage storage;

    /**
     * Creates a command that marks the selected task as done.
     *
     * @param commandArgs User input after the command word.
     * @param tasks Task list to update.
     * @param storage Storage to update.
     */
    public MarkCommand(String commandArgs, TaskList tasks, Storage storage) {
        this.commandArgs = commandArgs;
        this.tasks = tasks;
        this.storage = storage;
    }

    @Override
    public String execute() throws LunaException {
        int taskIndex = Parser.parseIndex(commandArgs, "mark", tasks.size());
        Task task = tasks.get(taskIndex);

        if (task.isDone()) {
            throw new LunaException("This task is already marked as done.");
        }

        task.markDone();
        storage.save(tasks.getUnmodifiableList());
        return "Nice! I've marked this task as done:\n" + task;
    }
}
