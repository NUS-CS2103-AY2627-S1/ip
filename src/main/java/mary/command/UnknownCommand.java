package mary.command;

import mary.storage.Storage;
import mary.task.TaskList;
import mary.ui.Ui;

/**
 * Reports an input that does not match a supported command.
 */
public class UnknownCommand extends Command {
    private final String input;

    /**
     * Creates a response for an unsupported input.
     */
    public UnknownCommand(String input) {
        this.input = input;
    }

    /**
     * Shows an error explaining how to enter a supported command.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        if (input.isBlank()) {
            ui.showError("please enter a command or task.");
        } else {
            ui.showError("I can't get my paws around that command; use todo, deadline, event, on, list, find,"
                    + " sort, mark, unmark, delete, or bye.");
        }
    }
}
