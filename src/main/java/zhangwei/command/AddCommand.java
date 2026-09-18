package zhangwei.command;

import zhangwei.ZhangWeiException;
import zhangwei.storage.Storage;
import zhangwei.task.Task;
import zhangwei.task.TaskList;
import zhangwei.ui.Ui;

/**
 * Adds one already-built task to the list, whatever its type.
 *
 * <p>A todo, a deadline and an event differ only in how they are read from the
 * user's text, which the parser has already done by this point; adding them is
 * identical, so one command serves all three.
 */
public class AddCommand extends Command {

    private final Task task;

    /**
     * Creates a command that will add the given task.
     *
     * @param task the task to add, already built by the parser.
     */
    public AddCommand(Task task) {
        this.task = task;
    }

    /**
     * Adds the task to the end of the list, confirms it to the user, and saves
     * the updated list so the task survives the next restart.
     *
     * @param tasks the task list to add to.
     * @param ui used to confirm the addition to the user.
     * @param storage used to save the updated list.
     * @throws ZhangWeiException if the updated list could not be saved.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws ZhangWeiException {
        assert task != null : "The parser builds the task before it creates this command.";

        tasks.add(task);
        ui.showTaskAdded(task, tasks.size());
        storage.saveTasks(tasks);
    }
}
