package goat.command;

import goat.GoatException;
import goat.storage.Storage;
import goat.task.TaskList;
import goat.ui.Ui;

/**
 * Shows the tasks whose descriptions contain a given keyword.
 *
 * Like {@link ListCommand} this command changes nothing, so it never calls
 * {@code storage.save}. The two are kept apart rather than merged into one
 * "show some tasks" command because what they are asked for differs: list
 * takes no argument and always shows everything, find takes a keyword and can
 * legitimately come back with nothing.
 */
public class FindCommand extends Command {

    /** The text to look for in each task's description. */
    private final String keyword;

    /**
     * Creates a command that will search for the given text.
     *
     * @param keyword the text to look for
     */
    public FindCommand(String keyword) {
        this.keyword = keyword;
    }

    /**
     * {@inheritDoc}
     *
     * <p>Shows the tasks whose descriptions contain the keyword, numbered from
     * 1 within the results, or says there were none. Nothing is written to
     * disk, since nothing changed.
     *
     * @throws GoatException never in practice: the numbers used below are all
     *                       in range
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws GoatException {
        TaskList matches = tasks.find(keyword);
        if (matches.isEmpty()) {
            ui.show("Nothing matches that.");
            return;
        }
        ui.show(formatNumbered("Found these:", matches));
    }

}
