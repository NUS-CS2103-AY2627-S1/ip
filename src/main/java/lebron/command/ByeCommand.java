package lebron.command;

import lebron.task.TaskList;
import lebron.ui.Ui;

/**
 * Represents the "bye" command, which ends the program.
 */
public class ByeCommand implements Command {
    @Override
    public String execute(TaskList taskList, Ui ui) {
        return ui.showMessage("Peace out see ya later!");
    }

    @Override
    public boolean isExit() {
        return true;
    }
}
