package zen.command;

import zen.ZenException;
import zen.storage.Storage;
import zen.task.TaskList;
import zen.ui.Ui;

/** Displays tasks whose descriptions contain a specified keyword. */
public class FindCommand extends Command {
    private final String arguments;

    /**
     * Creates a command that searches descriptions for a keyword.
     *
     * @param arguments the keyword to search for
     */
    public FindCommand(String arguments) {
        this.arguments = arguments;
    }

    /** Displays the tasks whose descriptions contain this command's keyword. */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws ZenException {
        Parser.requireNonBlankArgument(arguments, "find", "keyword");
        ui.printMatchingTasks(tasks.findTasksByDescription(arguments));
    }
}
