package bob.command;

import java.util.List;

import bob.storage.Storage;
import bob.task.TaskList;
import bob.ui.Ui;

/**
 * Prints the unfinished tasks with the soonest dates on them, most urgent first.
 *
 * <p>Unlike the other listings this one reorders what it shows, which
 * {@link TaskList#findIndexesSoonestFirst()} does without disturbing the list.
 *
 * <p>Fewer tasks than asked for are shown without complaint when the list does
 * not hold that many, and the heading says how many are actually there.
 */
public class NextCommand extends Command {

    /** Said when no task in the list has a date at all. */
    private static final String MESSAGE_NO_DATED_TASKS = "No dates on tasks. No bee-do!";

    /**
     * Said when tasks with dates exist but every one of them is done, so the user is
     * not told there are no dates while finished dated tasks sit in the list.
     */
    private static final String MESSAGE_ALL_DATED_TASKS_DONE = "All dated tasks finish! No bee-do!";

    /** How many tasks the user asked to see, always one or more. */
    private final int wantedCount;

    /**
     * Creates a command that will list the most urgent tasks.
     *
     * @param wantedCount how many to show at most.
     */
    public NextCommand(int wantedCount) {
        // Parser refuses zero and negative counts, with a message of its own, so a
        // count that reaches this far has already been found to be worth showing.
        assert wantedCount >= 1 : "Asked for " + wantedCount + " tasks, which is not a number to show";
        this.wantedCount = wantedCount;
    }

    /**
     * Prints up to the requested number of unfinished dated tasks, soonest first, or
     * says why there are none: no task has a date, or every task with one is done.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        List<Integer> urgentTaskIndexes = tasks.findIndexesSoonestFirst();
        int shownCount = Math.min(wantedCount, urgentTaskIndexes.size());
        boolean hasDatedTask = !tasks.findIndexes(task -> task.getScheduledDate().isPresent()).isEmpty();
        // When nothing is urgent the heading is built naming none, and never
        // printed: an empty selection is shown as the message below it instead.
        ui.showTasks(tasks, urgentTaskIndexes.subList(0, shownCount),
                shownCount == 1
                        ? "Bee-do bee-do! Most urgent:"
                        : "Bee-do bee-do! " + shownCount + " most urgent, soonest first:",
                hasDatedTask ? MESSAGE_ALL_DATED_TASKS_DONE : MESSAGE_NO_DATED_TASKS);
    }
}
