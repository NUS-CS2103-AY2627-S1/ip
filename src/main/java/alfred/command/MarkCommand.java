package alfred.command;

import alfred.AlfredException;
import alfred.Storage;
import alfred.Ui;
import alfred.task.TaskList;

/**
 * Marks one task as done.
 */
public class MarkCommand extends Command {
    private final int index;

    /**
     * Creates a command that will mark one task as done.
     *
     * @param index which task, counting from 0.
     */
    public MarkCommand(int index) {
        this.index = index;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws AlfredException {
        ui.showMarked(tasks.markDone(index));
        storage.save(tasks.toList());
    }
}
