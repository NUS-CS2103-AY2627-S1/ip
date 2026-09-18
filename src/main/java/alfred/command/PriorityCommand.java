package alfred.command;

import alfred.AlfredException;
import alfred.Storage;
import alfred.Ui;
import alfred.task.Priority;
import alfred.task.TaskList;

/**
 * Sets how much one task matters, or takes its priority off again.
 */
public class PriorityCommand extends Command {
    private final int index;

    /** The level to give it, {@link Priority#NONE} meaning take its priority off. */
    private final Priority priority;

    /**
     * Creates a command that will set one task's priority.
     *
     * @param index which task, counting from 0.
     * @param priority the level to give it.
     */
    public PriorityCommand(int index, Priority priority) {
        this.index = index;
        this.priority = priority;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws AlfredException {
        ui.showPrioritySet(tasks.setPriority(index, priority));
        storage.save(tasks.toList());
    }
}
