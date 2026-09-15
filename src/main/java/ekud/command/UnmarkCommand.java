package ekud.command;

import ekud.EkudException;
import ekud.parser.Parser;
import ekud.storage.Storage;
import ekud.task.Task;
import ekud.task.TaskList;
import ekud.ui.Ui;

/**
 * Marks a task as not done, using the 1-based number shown by {@code list}.
 */
public class UnmarkCommand implements Command {
    private final int oneBasedIndex;

    /**
     * Creates a command that will mark the given task as not done.
     *
     * @param oneBasedIndex task number as shown in the list, starting from 1
     */
    private UnmarkCommand(int oneBasedIndex) {
        this.oneBasedIndex = oneBasedIndex;
    }

    /**
     * Parses {@code unmark INDEX}.
     *
     * @param arguments text after the command word
     * @return an unmark command for that task number
     * @throws EkudException if the index is missing, not an integer, or extra text is present
     */
    public static UnmarkCommand parse(String arguments) throws EkudException {
        return new UnmarkCommand(Parser.parseOneBasedIndex(CommandType.UNMARK.getKeyword(), arguments));
    }

    /**
     * Marks the selected task as not done, saves the list, and shows a confirmation.
     *
     * @param tasks   the list containing the task
     * @param ui      used to show the confirmation
     * @param storage used to persist the updated list
     * @throws EkudException if the task number does not exist or the list cannot be saved
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws EkudException {
        Task task = tasks.getByOneBasedIndex(oneBasedIndex);
        task.markAsNotDone();
        storage.save(tasks);
        ui.showUnmarked(task);
    }
}
