package zen.command;

import zen.ZenException;
import zen.storage.Storage;
import zen.task.Task;
import zen.task.TaskList;
import zen.ui.Ui;

/** Marks a task as done. */
public class MarkCommand extends Command {
    private final String arguments;

    /**
     * Creates a command using the supplied task-number argument.
     *
     * @param arguments task number to mark as done
     */
    public MarkCommand(String arguments) {
        this.arguments = arguments;
    }

    /** Marks the requested task as done, saves the list, and displays the result. */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws ZenException {
        Task task = tasks.markTask(Parser.parseTaskNumber(arguments));
        storage.save(tasks);
        ui.printMarkTaskDone(task);
    }
}
