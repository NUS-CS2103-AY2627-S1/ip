package chione.command;

import chione.Storage;
import chione.Ui;
import chione.task.TaskList;

/**
 * Shows the tasks whose description contains a given word.
 *
 * <p>Like {@link ListCommand} this only reads the list, so nothing is saved.
 */
public class FindCommand extends Command {
    /** The word being searched for, quoted back if nothing matches it. */
    private final String keyword;

    /**
     * Remembers what to search for.
     *
     * @param keyword the word to look for in task descriptions
     */
    public FindCommand(String keyword) {
        this.keyword = keyword;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showMatchingTasks(tasks.find(keyword), keyword);
    }
}
