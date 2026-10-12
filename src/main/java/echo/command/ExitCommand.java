package echo.command;

import java.util.List;

import echo.task.TaskManager;
import echo.ui.Ui;

/**
 * Represents the command to exit the current E.C.H.O. session.
 */
public class ExitCommand extends Command {
    /**
     * Creates an exit command.
     */
    public ExitCommand() {
        super(Type.BYE, List.of());
    }

    /**
     * Displays a farewell message before the session ends.
     */
    @Override
    public String execute(TaskManager taskManager, Ui ui) {
        return ui.showFarewell();
    }

    @Override
    public boolean isExit() {
        return true;
    }
}
