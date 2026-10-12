package echo.command;

import echo.task.TaskManager;
import echo.ui.Ui;

/**
 * Represents the help command to display list of commands.
 */
public class HelpCommand extends Command {
    /**
     * Creates a help command.
     * */
    public HelpCommand() {
        super(Type.HELP, java.util.List.of());
    }

    /**
     * Displays the supported commands.
     */
    @Override
    public String execute(TaskManager taskManager, Ui ui) {
        return ui.showHelp();
    }
}
