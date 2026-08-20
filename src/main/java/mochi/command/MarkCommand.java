package mochi.command;

import mochi.MochiException;
import mochi.TaskList;
import mochi.ui.Ui;

/**
 * Marks a task as done.
 */
public class MarkCommand extends Command {
    private final int index;

    /**
     * Creates a mark command for the task at the given 0-based index.
     */
    public MarkCommand(int index) {
        this.index = index;
    }

    /** {@inheritDoc} */
    @Override
    public void execute(TaskList tasks, Ui ui) throws MochiException {
        if (index < 0 || index >= tasks.getTasks().size()) {
            throw new MochiException("There is no task number " + (index + 1) + " in the list.");
        }
        tasks.mark(index);
        ui.showMarked(tasks.getTasks().get(index));
    }
}
