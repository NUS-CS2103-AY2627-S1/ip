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

        // Positions are streamed rather than the tasks, because where a task
        // sits in the full list is part of what is being shown, and filtering a
        // stream of tasks would throw that position away. They are kept as
        // list indexes and turned into the numbers the user sees only at the
        // point of display, so the conversion happens once.
        List<Integer> matchIndexes = IntStream.range(0, allTasks.size())
                .filter(index -> matches(allTasks.get(index)))
                .boxed()
                .toList();

        // Working out the matches before showing any of them is what lets the
        // heading be decided up front, rather than by a flag carried through
        // the walk to remember whether it had been printed yet.
        if (matchIndexes.isEmpty()) {
            ui.showMessage(getNoMatchMessage());
            return;
        }

        ui.showMessage(getHeader());
        matchIndexes.forEach(index ->
                ui.showNumberedTask(index + 1, allTasks.get(index)));
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
