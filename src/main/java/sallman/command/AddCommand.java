package sallman.command;

import java.util.OptionalInt;

import sallman.SallmanException;
import sallman.Storage;
import sallman.TaskList;
import sallman.Ui;
import sallman.task.Task;

/**
 * Adds one task to the list.
 * <p>
 * Todos, deadlines and events differ only in how they are built, which the
 * parser has already done by this point, so one command covers all three.
 */
public class AddCommand extends Command {

    /** The task to add, already built and validated. */
    private final Task task;

    /**
     * Creates a command that adds the given task.
     *
     * @param task the task to add
     */
    public AddCommand(Task task) {
        this.task = task;
    }

    /**
     * Adds the task to the list, confirms it, and saves the list.
     *
     * @param tasks   the list to add to
     * @param ui      used to confirm the addition
     * @param storage used to save the enlarged list
     * @throws SallmanException if the list already holds the same task, or the
     *                          list cannot be saved
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws SallmanException {
        OptionalInt existing = tasks.indexOfSameTask(task);
        if (existing.isPresent()) {
            // Checked before the snapshot, so a refused add leaves no undo step.
            int index = existing.getAsInt();
            throw new SallmanException(
                    "Great minds think alike! You already have that task, so I didn't add it again:",
                    "  " + (index + 1) + "." + tasks.get(index));
        }
        tasks.saveSnapshot();
        tasks.add(task);
        ui.showAdded(task, tasks.size());
        storage.save(tasks.asList());
    }
}
