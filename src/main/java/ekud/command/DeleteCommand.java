package ekud.command;

import ekud.EkudException;
import ekud.parser.Parser;
import ekud.storage.Storage;
import ekud.task.Task;
import ekud.task.TaskList;
import ekud.ui.Ui;

/**
 * Deletes a task, using the 1-based number shown by {@code list}.
 */
public class DeleteCommand implements Command {
    private final int oneBasedIndex;

    /**
     * Creates a command that will delete the given task.
     *
     * @param oneBasedIndex task number as shown in the list, starting from 1
     */
    private DeleteCommand(int oneBasedIndex) {
        this.oneBasedIndex = oneBasedIndex;
    }

    /**
     * Parses {@code delete INDEX}.
     *
     * @param arguments text after the command word
     * @return a delete command for that task number
     * @throws EkudException if the index is missing, not an integer, or extra text is present
     */
    public static DeleteCommand parse(String arguments) throws EkudException {
        return new DeleteCommand(Parser.parseOneBasedIndex(CommandType.DELETE.getKeyword(), arguments));
    }

    /**
     * Removes the selected task, saves the list, and shows a confirmation.
     *
     * @param tasks   the list containing the task
     * @param ui      used to show the confirmation
     * @param storage used to persist the updated list
     * @throws EkudException if the task number does not exist or the list cannot be saved
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws EkudException {
        // Ekud always passes the objects it constructed; a null collaborator is a bug.
        assert tasks != null : "Task list must be provided to execute a command";
        assert ui != null : "Ui must be provided to execute a command";
        assert storage != null : "Storage must be provided to execute a command";
        Task removed = tasks.removeByOneBasedIndex(oneBasedIndex);
        storage.save(tasks);
        ui.showDeleted(removed, tasks.size());
    }
}
