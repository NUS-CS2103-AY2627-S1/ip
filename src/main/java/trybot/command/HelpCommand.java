package trybot.command;

import trybot.storage.Storage;
import trybot.task.TaskList;
import trybot.ui.Ui;

/** Displays the commands supported by TryBot and their usage. */
public class HelpCommand extends Command {
    /** Displays the command reference. */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showHelp();
    }

    /** Indicates that displaying help does not end the session. */
    @Override
    public boolean isExit() {
        return false;
    }
}
