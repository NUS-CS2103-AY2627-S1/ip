package ekud.command;

import ekud.EkudException;
import ekud.parser.Parser;
import ekud.storage.Storage;
import ekud.task.Task;
import ekud.task.TaskList;
import ekud.ui.Ui;

/**
 * Marks a task as done, using the 1-based number shown by {@code list}.
 */
public class MarkCommand implements Command {
    private final int oneBasedIndex;

    /**
     * Creates a command that will mark the given task as done.
     *
     * @param oneBasedIndex task number as shown in the list, starting from 1
     */
    private MarkCommand(int oneBasedIndex) {
        this.oneBasedIndex = oneBasedIndex;
    }

    /**
     * Parses {@code mark INDEX}.
     *
     * @param arguments text after the command word
     * @return a mark command for that task number
     * @throws EkudException if the index is missing, not an integer, or extra text is present
     */
    public static MarkCommand parse(String arguments) throws EkudException {
        return new MarkCommand(Parser.parseOneBasedIndex(CommandType.MARK.getKeyword(), arguments));
    }

    /**
     * Marks the selected task as done, saves the list, and shows a confirmation.
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
        Task task = tasks.getByOneBasedIndex(oneBasedIndex);
        task.markAsDone();
        storage.save(tasks);
        ui.showMarked(task);
    }
}
