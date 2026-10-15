package wodan.command;

import wodan.WodanException;
import wodan.parser.Parser;
import wodan.storage.Storage;
import wodan.task.Task;
import wodan.task.TaskList;
import wodan.ui.Ui;

/**
 * Marks a task as not done and saves.
 */
public class UnmarkCommand extends Command {
    private final String arguments;

    /**
     * Creates an unmark command from the text after {@code unmark}.
     *
     * @param arguments Expected to be a 1-based task number.
     */
    public UnmarkCommand(String arguments) {
        this.arguments = arguments;
    }

    /**
     * Marks the numbered task as not done, saves the list, and shows the updated task.
     *
     * @param tasks The task list to change.
     * @param ui The user interface for the reply.
     * @param storage The save file to update.
     * @throws WodanException If the number is invalid or the list cannot be saved.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws WodanException {
        int taskNumber = Parser.parseUnmarkNumber(arguments, tasks);
        assert taskNumber >= 1 && taskNumber <= tasks.size()
                : "parseUnmarkNumber must return number that exists in the list";
        Task currentTask = tasks.get(taskNumber - 1);
        currentTask.markAsUndone();
        assert " ".equals(currentTask.getStatusIcon())
                : "markAsUndone should leave the task undone";
        storage.save(tasks.getTasks());
        ui.show("    The ravens retract their approval.",
                "     " + currentTask.toString());
    }
}
