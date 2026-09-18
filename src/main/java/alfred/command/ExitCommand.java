package alfred.command;

import alfred.task.TaskList;
import alfred.ui.Ui;

/**
 * Ends the chatbot session.
 */
public class ExitCommand extends Command {
    @Override
    public void execute(TaskList tasks, Ui ui) {
        ui.showGoodbye();
    }

    @Override
    public boolean isExit() {
        return true;
    }
}
