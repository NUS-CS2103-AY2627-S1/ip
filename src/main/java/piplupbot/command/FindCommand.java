package piplupbot.command;

import piplupbot.Storage;
import piplupbot.Ui;
import piplupbot.task.Task;
import piplupbot.task.TaskList;

// ACKNOWLEDGEMENTS: This Java file was written with the help of Claude.

/**
 * Displays the tasks whose description contains a given piece of text.
 *
 * <p>Like {@link ListCommand}, all this command decides is what to call the
 * list it shows: which tasks match is {@link TaskList#find}'s question, what
 * counts as a match is {@link Task#descriptionContains}'s, and the layout is
 * {@link Ui}'s. It changes nothing, so it has nothing to save.</p>
 *
 * <p>It is a separate class from {@code ListCommand} rather than a flag on it,
 * because the two differ in what they are given as well as in what they show: a
 * find carries the keyword it was built with, and a list has nothing to
 * carry.</p>
 *
 * <p>The matches are numbered from 1 among themselves, so a task that
 * {@code list} calls 5 may appear here as 2. The numbers are a way of reading
 * the reply rather than a way of naming a task -- {@code mark} and
 * {@code delete} still count from the full list -- which is worth knowing
 * before typing {@code delete 2} at a search result.</p>
 */
public class FindCommand extends Command {
    /** The text to look for in each task's description. */
    private final String keyword;

    /**
     * Creates a command that will show the tasks matching the given text.
     *
     * @param keyword The text to look for in each task's description.
     */
    public FindCommand(String keyword) {
        this.keyword = keyword;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showList("Here are the matching tasks in your list:",
                tasks.find(keyword).toNumberedLines());
    }
}
