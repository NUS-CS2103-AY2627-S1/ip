package bibi.command;

import bibi.Storage;
import bibi.Ui;
import bibi.task.Task;
import bibi.task.TaskList;

/**
 * Adds one already-built task to the list.
 *
 * <p>The task arrives ready-made from {@link Parser}, so this command is the
 * same whether the user typed a ToDo, a deadline, or an event.
 */
public class AddCommand extends Command {
    private final Task task;

    /**
     * Creates a command that will add the supplied task.
     *
     * @param task the task to add
     */
    public AddCommand(Task task) {
        this.task = task;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        tasks.add(task);
        ui.showMessage("Got it. I've added this task:");
        ui.showDetail(task.toString());
        ui.showPlain("Now you have " + tasks.size() + " tasks in the list.");
        saveTasks(tasks, ui, storage);
    }
}
