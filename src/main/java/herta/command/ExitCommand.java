package herta.command;

import herta.storage.Storage;
import herta.task.TaskList;
import herta.ui.UiOutput;

/**
 * Represents the command that ends the Herta application.
 */
public class ExitCommand extends Command {

    /**
     * Displays the goodbye message.
     *
     * @param tasks unused because exiting does not access tasks.
     * @param ui the output interface used to display the goodbye message.
     * @param storage unused because exiting does not access storage.
     */
    @Override
    public void execute(TaskList tasks, UiOutput ui, Storage storage) {
        ui.showGoodbye();
    }
}
