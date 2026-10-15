package piplupbot.command;

import piplupbot.Storage;
import piplupbot.Ui;
import piplupbot.task.SortDirection;
import piplupbot.task.SortKey;
import piplupbot.task.TaskList;

// ACKNOWLEDGEMENTS: This Java file was written with the help of Claude.

/**
 * Puts the stored tasks into a given order and shows the result.
 *
 * <p>Unlike {@link ListCommand} and {@link FindCommand}, which only read the
 * list, this command rearranges it: the tasks themselves are reordered and
 * written to disk. That is what makes the numbers beside the rows usable --
 * {@code mark 2} straight after a sort marks the second row the user was just
 * shown -- and it is also what costs them the order the tasks were added in,
 * which is kept nowhere and so cannot be restored.</p>
 *
 * <p>Which order the tasks go into is {@link SortKey}'s question and
 * {@link SortDirection}'s; carrying them out is {@link TaskList#sort}'s. All
 * this command decides is what to call the list it shows, and that the change
 * should be saved.</p>
 */
public class SortCommand extends Command {
    /** What to order the tasks by. */
    private final SortKey key;

    /** Whether that order runs forwards or backwards. */
    private final SortDirection direction;

    /**
     * Creates a command that will put the tasks into the given order.
     *
     * @param key       What to order the tasks by.
     * @param direction Whether that order runs forwards or backwards.
     */
    public SortCommand(SortKey key, SortDirection direction) {
        this.key = key;
        this.direction = direction;
    }

    /**
     * {@inheritDoc}
     *
     * <p>The sorted list is shown rather than merely confirmed, because the
     * numbers it shows are the ones {@code mark} and {@code delete} now take.
     * An empty list prints the heading and no rows, exactly as {@code list}
     * does.</p>
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        tasks.sort(key, direction);
        ui.showList(buildHeading(), tasks.toNumberedLines());
        save(tasks, ui, storage);
    }

    /**
     * Returns the line that introduces the sorted tasks, e.g.
     * {@code "Here are your tasks, sorted by date:"}.
     *
     * <p>A descending sort adds one clause, ", in reverse", rather than each key
     * having a wording of its own such as "latest first" or "Z to A": one clause
     * serves all four keys, so their wordings cannot drift apart. Without it the
     * reply to {@code sort date} and to {@code sort date desc} would read
     * identically.</p>
     *
     * @return The heading naming the key, and the direction when it is reversed.
     */
    private String buildHeading() {
        String reversedNote = direction == SortDirection.DESC ? ", in reverse" : "";
        return "Here are your tasks, sorted by " + key.getKeyword() + reversedNote + ":";
    }
}
