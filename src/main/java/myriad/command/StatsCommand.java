package myriad.command;

import java.time.LocalDate;

import myriad.storage.Storage;
import myriad.task.TaskList;
import myriad.task.TaskStats;
import myriad.task.TaskType;
import myriad.ui.Ui;

/**
 * Reports a snapshot of the task list: how many tasks of each type there
 * are, which are due within the next {@value #DUE_SOON_DAYS} days, and the
 * oldest tasks not yet done. Everything is computed on demand from
 * {@link TaskList}, so it is never out of sync with the tasks actually
 * held; nothing is stored and, like {@link ShowCommand}, reading the list
 * never triggers a save.
 */
public class StatsCommand extends Command {

    /** How many days ahead of today counts as "due soon". */
    private static final int DUE_SOON_DAYS = 7;

    /** How many of the oldest not-done tasks to report. */
    private static final int OLDEST_UNDONE_LIMIT = 5;

    /** The date the "due soon" window is measured from. */
    private final LocalDate today;

    /**
     * Creates a command that reports statistics as of today's date on the
     * system clock.
     */
    public StatsCommand() {
        this(LocalDate.now());
    }

    /**
     * Creates a command that reports statistics as of the given date. Lets a
     * test fix the date the "due soon" window starts from, since the
     * system clock gives a different answer every day the test is run.
     *
     * @param today the date the "due soon" window is measured from.
     */
    public StatsCommand(LocalDate today) {
        assert today != null : "the no-argument constructor supplies today's date";
        this.today = today;
    }

    /**
     * Gathers the task-count breakdown, the tasks due soon, and the oldest
     * not-done tasks into a {@link TaskStats}, then shows it via {@link Ui}.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        TaskStats stats = new TaskStats(
                tasks.getTasksOfType(TaskType.TODO),
                tasks.getTasksOfType(TaskType.DEADLINE),
                tasks.getTasksOfType(TaskType.EVENT),
                DUE_SOON_DAYS,
                tasks.getTasksDueWithin(today, DUE_SOON_DAYS),
                tasks.getOldestUndone(OLDEST_UNDONE_LIMIT));

        ui.showStats(stats);
    }
}
