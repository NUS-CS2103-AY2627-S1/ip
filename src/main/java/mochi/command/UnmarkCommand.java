package mochi.command;

import mochi.MochiException;
import mochi.TaskList;
import mochi.ui.Ui;

/**
 * Marks a task as not done.
 */
public class UnmarkCommand extends Command {
    private final int index;

    /**
     * Creates an unmark command for the task at the given 0-based index.
     */
    public UnmarkCommand(int index) {
        this.index = index;
    }

    /** {@inheritDoc} */
    @Override
    public void execute(TaskList tasks, Ui ui) throws MochiException {
        if (index < 0 || index >= tasks.getTasks().size()) {
            throw new MochiException("There is no task number " + (index + 1) + " in the list.");
        }
        tasks.unmark(index);
        ui.showUnmarked(tasks.getTasks().get(index));
    }
}
