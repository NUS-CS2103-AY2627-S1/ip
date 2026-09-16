package miku.command;

import miku.MikuException;
import miku.task.Task;

/** Adds a parser-created task to Miku's task list. */
public class AddCommand extends Command {
    private final Task task;

    /** Creates a command that adds the given task. */
    public AddCommand(Task task) {
        this.task = task;
    }

    /** Adds the task, persists the updated list, and reports the result. */
    @Override
    public String execute(CommandContext commandContext) throws MikuException {
        commandContext.tasks().add(task);
        saveTasks(commandContext);
        return commandContext.responseFormatter().formatTaskAdded(task, commandContext.tasks().size());
    }
}
