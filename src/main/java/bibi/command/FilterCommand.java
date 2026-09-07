package bibi.command;

import bibi.Storage;
import bibi.Ui;
import bibi.task.Task;
import bibi.task.TaskList;

/**
 * Shows the tasks that pass some test, leaving them numbered as they are in the
 * full list.
 *
 * <p>Both {@link FindCommand} and {@link OnCommand} answer the same shape of
 * question: walk the list, show the tasks that qualify under a heading, and say
 * so when none does. Only the test and the wording differ, so those are all a
 * subclass supplies.
 *
 * <p>Matches keep the number they have in the full list, rather than being
 * renumbered from one, so a task found this way can be marked or removed
 * straight away without listing everything first.
 */
public abstract class FilterCommand extends Command {
    /**
     * Creates a filtering command. Subclasses hold whatever their test needs.
     */
    protected FilterCommand() {
    }

    /**
     * Shows every task that passes {@link #matches(Task)}, or reports that none
     * does.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        boolean hasMatch = false;

        int taskNumber = 1;
        for (Task task : tasks.getTasks()) {
            if (matches(task)) {
                // The heading is shown lazily, so that a search with no results
                // does not announce a list it is about to leave empty.
                if (!hasMatch) {
                    ui.showMessage(getHeader());
                    hasMatch = true;
                }
                ui.showNumberedTask(taskNumber, task);
            }
            taskNumber++;
        }

        if (!hasMatch) {
            ui.showMessage(getNoMatchMessage());
        }
    }

    /**
     * Reports whether one task belongs in this command's results.
     *
     * @param task the task being considered
     * @return {@code true} when the task should be shown
     */
    protected abstract boolean matches(Task task);

    /**
     * Returns the line shown above the results, when there is at least one.
     *
     * @return the heading
     */
    protected abstract String getHeader();

    /**
     * Returns the line shown when nothing matches.
     *
     * @return the explanation
     */
    protected abstract String getNoMatchMessage();
}
