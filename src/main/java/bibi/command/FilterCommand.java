package bibi.command;

import java.util.List;
import java.util.stream.IntStream;

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
        List<Task> allTasks = tasks.getTasks();

        // The numbers are streamed rather than the tasks, because the number a
        // task carries in the full list is part of what is being shown, and
        // filtering a stream of tasks would throw that position away.
        List<Integer> matchNumbers = IntStream.rangeClosed(1, allTasks.size())
                .filter(taskNumber -> matches(allTasks.get(taskNumber - 1)))
                .boxed()
                .toList();

        // Working out the matches before showing any of them is what lets the
        // heading be decided up front, rather than by a flag carried through
        // the walk to remember whether it had been printed yet.
        if (matchNumbers.isEmpty()) {
            ui.showMessage(getNoMatchMessage());
            return;
        }

        ui.showMessage(getHeader());
        matchNumbers.forEach(taskNumber ->
                ui.showNumberedTask(taskNumber, allTasks.get(taskNumber - 1)));
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
