package computah.command;

import computah.model.Model;
import computah.storage.Storage;
import computah.ui.Ui;

/**
 * Exits the chatbot.
 */
public class ExitCommand extends Command {
    /**
     * Creates an exit command.
     */
    public ExitCommand() {
    }

    /**
     * Shows the farewell message.
     *
     * @param model current application data; unused for this command.
     * @param ui user interface used to display the farewell.
     * @param storage storage component; unused for this command.
     */
    @Override
    public void execute(Model model, Ui ui, Storage storage) {
        ui.showFarewell();
    }

    /**
     * Returns true because this command ends the command loop.
     *
     * @return true.
     */
    @Override
    public boolean isExit() {
        return true;
    }
}
