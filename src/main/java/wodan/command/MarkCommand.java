package wodan.command;

import wodan.WodanException;
import wodan.parser.Parser;
import wodan.storage.Storage;
import wodan.task.Task;
import wodan.task.TaskList;
import wodan.ui.Ui;

/**
 * Marks a task as done and saves.
 */
public class MarkCommand extends Command {
    private final String arguments;

    /**
     * Creates a mark command from the text after {@code mark}.
     *
     * @param arguments Expected to be a 1-based task number.
     */
    public MarkCommand(String arguments) {
        this.arguments = arguments;
    }

    /**
     * Marks the numbered task as done, saves the list, and shows the updated task.
     *
     * @param tasks The task list to change.
     * @param ui The user interface for the reply.
     * @param storage The save file to update.
     * @throws WodanException If the number is invalid or the list cannot be saved.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws WodanException {
        int taskNumber = Parser.parseMarkNumber(arguments, tasks);
        assert taskNumber >= 1 && taskNumber <= tasks.size()
                : "parseMarkNumber must return a number that exists in the list";
        Task currentTask = tasks.get(taskNumber - 1);
        currentTask.markAsDone();
        assert "X".equals(currentTask.getStatusIcon())
                : "markAsDone should leave the task done";
        storage.save(tasks.getTasks());
        ui.show("    One less burden to carry.",
                "     " + currentTask.toString(),
                "");
    }
}
