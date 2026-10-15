package zen.command;

import zen.ZenException;
import zen.storage.Storage;
import zen.task.TaskList;
import zen.ui.Ui;

/** Ends the application after displaying the farewell message. */
public class ExitCommand extends Command {
    private final String arguments;

    /**
     * Creates an exit command using the supplied arguments.
     *
     * @param arguments arguments to validate as empty
     */
    public ExitCommand(String arguments) {
        this.arguments = arguments;
    }

    /** Validates its arguments and displays the farewell message. */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws ZenException {
        Parser.requireNoArguments(arguments, "bye");
        ui.printExitMessage();
    }

    /**
     * Indicates that this command ends the application loop.
     *
     * @return true
     */
    @Override
    public boolean isExit() {
        return true;
    }
}
