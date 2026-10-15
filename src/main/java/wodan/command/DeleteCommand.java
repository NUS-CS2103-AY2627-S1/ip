package wodan.command;

import wodan.WodanException;
import wodan.parser.Parser;
import wodan.storage.Storage;
import wodan.task.Task;
import wodan.task.TaskList;
import wodan.ui.Ui;

/**
 * Deletes a task from the list and saves.
 */
public class DeleteCommand extends Command {
    private final String arguments;

    /**
     * Creates a delete command from the text after {@code delete}.
     *
     * @param arguments Expected to be a 1-based task number.
     */
    public DeleteCommand(String arguments) {
        this.arguments = arguments;
    }

    /**
     * Deletes the numbered task from {@code tasks}, saves the list, and shows the removed task.
     *
     * @param tasks The task list to change.
     * @param ui The user interface for the reply.
     * @param storage The save file to update.
     * @throws WodanException If the number is invalid or the list cannot be saved.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws WodanException {
        int taskNumber = Parser.parseDeleteNumber(arguments, tasks);
        Task removed = tasks.delete(taskNumber - 1);
        storage.save(tasks.getTasks());
        ui.show("     Noted. I've removed this task:",
                "       " + removed.toString(),
                "     Now you have " + Ui.formatTaskCount(tasks.size()) + " in the list.");
    }
}
