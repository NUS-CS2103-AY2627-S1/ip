package zen.command;

import zen.ZenException;
import zen.storage.Storage;
import zen.task.Task;
import zen.task.TaskList;
import zen.ui.Ui;

/** Marks a task as not done. */
public class UnmarkCommand extends Command {
    private final String arguments;

    /**
     * Creates a command using the supplied task number argument.
     *
     * @param arguments task number to mark as not done
     */
    public UnmarkCommand(String arguments) {
        this.arguments = arguments;
    }

    /** Marks the requested task as not done, saves the list, and displays the result. */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws ZenException {
        Task task = tasks.unmarkTask(Parser.parseTaskNumber(arguments));
        storage.save(tasks);
        ui.printMarkTaskNotDone(task);
    }
}
