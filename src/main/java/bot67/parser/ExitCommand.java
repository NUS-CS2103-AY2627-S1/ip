package bot67.parser;

import bot67.storage.Storage;
import bot67.task.TaskList;
import bot67.ui.Ui;

/**
 * Command that terminates the Bot67 session.
 */
public class ExitCommand extends Command {
    private final String personalityArt;

    /** Creates an exit command with the application's personality artwork. */
    public ExitCommand(String personalityArt) {
        this.personalityArt = personalityArt;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showGoodbye(personalityArt);
    }

    @Override
    public boolean isExit() {
        return true;
    }
}
