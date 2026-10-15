package thomas.command;

import thomas.ThomasException;
import thomas.parser.Parser;
import thomas.storage.Storage;
import thomas.task.Task;
import thomas.task.TaskList;
import thomas.ui.Ui;

/**
 * Stores a new task: the {@code todo}, {@code deadline} and {@code event}
 * commands.
 * <p>
 * One class for all three, because once the line has been read they differ only
 * in which kind of {@link Task} was built -- and appending it, saving and
 * announcing it are the same either way. Which subclass it is stays the task's
 * own business, so a fourth kind of task would need no change here.
 */
public class AddCommand extends Command {
    /**
     * The task to store, built by {@link Parser} from the typed line.
     * <p>
     * Already complete when this command is created, so a task whose arguments
     * did not parse never reaches the list: the failure happens while the
     * command is being built, before there is anything to execute.
     */
    private final Task task;

    /**
     * Creates the command for one new task.
     *
     * @param task The task to store.
     */
    public AddCommand(Task task) {
        this.task = task;
    }

    /**
     * Appends the task, saves, and reports the new size of the list.
     *
     * @param tasks The list to append to.
     * @param ui Used to word the confirmation.
     * @param storage Where the longer list is written.
     * @param history Told how to take the new task back out again.
     * @return The confirmation, behind a warning if the save failed.
     * @throws ThomasException If the same task is already on the list.
     */
    @Override
    public String execute(TaskList tasks, Ui ui, Storage storage, History history)
            throws ThomasException {
        // A duplicate is refused by the list before anything is recorded or
        // saved, so a rejected add leaves no undo entry behind, exactly as a
        // line the parser refused leaves none.
        tasks.add(task);
        // The new task is on the end, so its number is the new size of the list.
        history.pushRemove(tasks.size());
        String saveWarning = save(tasks, ui, storage);
        return saveWarning + ui.getAddedMessage(task, tasks.size());
    }
}
