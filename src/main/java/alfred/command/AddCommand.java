package alfred.command;

import alfred.task.Task;
import alfred.task.TaskList;
import alfred.ui.Ui;

/**
 * Adds a task to the list and confirms the addition.
 */
public class AddCommand extends Command {
    /** Task to store. */
    private final Task task;

    /**
     * Creates a command that will add {@code task}.
     *
     * @param task Task produced by {@link alfred.parser.Parser}.
     */
    public AddCommand(Task task) {
        // Parser constructs this only after a ToDo, Deadline, or Event is created.
        assert task != null : "AddCommand requires a parsed task";
        this.task = task;
    }

    @Override
    public void execute(TaskList tasks, Ui ui) {
        tasks.add(task);
        ui.showTaskAdded(task, tasks.size());
    }

    @Override
    public boolean isMutating() {
        return true;
    }
}
