package zhangwei.command;

import zhangwei.ZhangWeiException;
import zhangwei.storage.Storage;
import zhangwei.task.TaskList;
import zhangwei.ui.Ui;

/**
 * A command that acts on the one task the user picked by number.
 *
 * <p>Marking, unmarking and deleting differ only in what they do to that task.
 * Everything around it is the same: check the number against the list, then
 * save the result. Doing that here leaves each subclass holding only the step
 * that makes it different, and means a numbered command added later cannot
 * forget to save.
 */
public abstract class TaskNumberCommand extends Command {

    /** The 1-based number of the task to act on, as the user sees it. */
    private final int taskNumber;

    /**
     * Creates a command that will act on the task with the given number.
     *
     * @param taskNumber the 1-based number of the task, as the user sees it in
     *     the list.
     */
    protected TaskNumberCommand(int taskNumber) {
        this.taskNumber = taskNumber;
    }

    /**
     * Checks the number against the list, carries this command out, and saves
     * the list it left behind.
     *
     * @param tasks the task list holding the task to act on.
     * @param ui used to tell the user what happened.
     * @param storage used to save the updated list.
     * @throws ZhangWeiException if the number does not refer to a task, or if
     *     the updated list could not be saved.
     */
    @Override
    public final void execute(TaskList tasks, Ui ui, Storage storage) throws ZhangWeiException {
        // Whether the number refers to a task can only be judged against the
        // list, which does not exist yet when the command is parsed.
        tasks.requireTask(taskNumber);
        actOnTask(taskNumber, tasks, ui);
        storage.saveTasks(tasks);
    }

    /**
     * Does whatever makes this command different, to the task the user picked.
     * By the time this is called the number is known to refer to a task.
     *
     * @param taskNumber the 1-based number of the task to act on.
     * @param tasks the task list holding it.
     * @param ui used to show the user what became of it.
     */
    protected abstract void actOnTask(int taskNumber, TaskList tasks, Ui ui);
}
