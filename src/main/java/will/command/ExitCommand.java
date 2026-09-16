package will.command;

import will.Storage;
import will.TaskList;
import will.Ui;

/** The "bye" command: says goodbye and ends the program's main loop. */
public class ExitCommand extends Command {
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showGoodbye();
    }

    @Override
    public boolean isExit() {
        return true;
    }
}
