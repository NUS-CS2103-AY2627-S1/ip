package rex.command;

import rex.TaskList;
import rex.Ui;
import rex.task.Task;

/** Adds a task to the list. Covers todos, deadlines and events alike. */
public class AddCommand extends Command {
    private final Task task;

    /**
     * Creates a command that adds the given task.
     *
     * @param task the task the user described, already built by the parser.
     */
    public AddCommand(Task task) {
        this.task = task;
    }

    /** Adds the task to the list and confirms it, giving the new total. */
    @Override
    public void execute(TaskList tasks, Ui ui) {
        tasks.add(task);
        ui.showAdded(task, tasks.size());
    }

    /** Returns true: adding a task always changes the list. */
    @Override
    public boolean isTaskListChanged() {
        return true;
    }
}
