package alfred.command;

import alfred.AlfredException;
import alfred.Storage;
import alfred.Ui;
import alfred.task.TaskList;

/**
 * Marks one task as not done after all.
 */
public class UnmarkCommand extends Command {
    private final int index;

    /**
     * Creates a command that will mark one task as not done.
     *
     * @param index which task, counting from 0.
     */
    public UnmarkCommand(int index) {
        this.index = index;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws AlfredException {
        ui.showUnmarked(tasks.unmarkDone(index));
        storage.save(tasks.toList());
    }
}
