package grower.commands;

import grower.tasks.TaskList;
import grower.ui.Ui;

/**
 * Represents a command that displays a supplied message.
 */
public class EchoCommand extends Command {
    private final String textToEcho;

    /**
     * Creates a command that displays the supplied message.
     *
     * @param textToEcho Message to display.
     */
    public EchoCommand(String textToEcho) {
        this.textToEcho = textToEcho;
    }

    @Override
    public boolean execute(TaskList tasks, Ui ui) {
        ui.showMessage(textToEcho);
        return true;
    }
}
