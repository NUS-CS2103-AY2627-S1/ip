package goat.command;

import goat.GoatException;
import goat.storage.Storage;
import goat.task.TaskList;
import goat.ui.Ui;

/**
 * Shows every task in the list.
 *
 * This is the one command that changes nothing, so it never calls
 * {@code storage.save}.
 */
public class ListCommand extends Command {

    /**
     * {@inheritDoc}
     *
     * <p>Shows every task, numbered from 1, or says the list is empty. Nothing
     * is written to disk, since nothing changed.
     *
     * @throws GoatException never in practice: the numbers used below are all
     *                       in range
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws GoatException {
        if (tasks.isEmpty()) {
            ui.show("Nothing on the list. Enjoy it.");
            return;
        }
        ui.show(formatNumbered("What you are carrying:", tasks));
    }

}
