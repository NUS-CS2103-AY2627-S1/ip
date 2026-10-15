package zen.command;

import zen.ZenException;
import zen.storage.Storage;
import zen.task.TaskList;
import zen.ui.Ui;

/** Displays all tasks. */
public class ListCommand extends Command {
    private final String arguments;

    /**
     * Creates a command using the supplied argument string.
     *
     * @param arguments arguments to validate as empty
     */
    public ListCommand(String arguments) {
        this.arguments = arguments;
    }

    /** Validates its arguments and displays the current task list. */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws ZenException {
        Parser.requireNoArguments(arguments, "list");
        ui.printTaskList(tasks);
    }
}
