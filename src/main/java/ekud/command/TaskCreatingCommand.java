package ekud.command;

import ekud.EkudException;
import ekud.storage.Storage;
import ekud.task.Task;
import ekud.task.TaskList;
import ekud.ui.Ui;

/**
 * An add command that can build a {@link Task} from its already-parsed arguments.
 * {@link Storage} uses this when loading the save file so it can reuse
 * {@link ekud.parser.Parser} without calling {@link #execute}, which would print
 * "added" messages for every saved task.
 */
public interface TaskCreatingCommand extends Command {
    /**
     * Builds the task this command would add, without changing the list or UI.
     *
     * @return a new task matching the parsed arguments
     */
    Task createTask();

    /**
     * Adds the task to {@code tasks}, saves the list, and shows a confirmation.
     *
     * @param tasks   the list to add to
     * @param ui      used to show the confirmation
     * @param storage used to persist the updated list
     * @throws EkudException if the list cannot be saved
     */
    @Override
    default void execute(TaskList tasks, Ui ui, Storage storage) throws EkudException {
        // Ekud always passes the objects it constructed; a null collaborator is a bug.
        assert tasks != null : "Task list must be provided to execute a command";
        assert ui != null : "Ui must be provided to execute a command";
        assert storage != null : "Storage must be provided to execute a command";
        Task task = createTask();
        tasks.add(task);
        storage.save(tasks);
        ui.showAdded(task, tasks.size());
    }
}
