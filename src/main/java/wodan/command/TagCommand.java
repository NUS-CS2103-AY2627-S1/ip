package wodan.command;

import wodan.WodanException;
import wodan.parser.Parser;
import wodan.storage.Storage;
import wodan.task.Task;
import wodan.task.TaskList;
import wodan.ui.Ui;

/**
 * Sets a task's category and saves.
 */
public class TagCommand extends Command {
    private final String arguments;

    /**
     * Creates a tag command from the text after {@code tag}.
     *
     * @param arguments Expected to be a 1-based task number and a category name.
     */
    public TagCommand(String arguments) {
        this.arguments = arguments;
    }

    /**
     * Sets the numbered task's category, saves the list, and shows the updated task.
     *
     * @param tasks The task list to change.
     * @param ui The user interface for the reply.
     * @param storage The save file to update.
     * @throws WodanException If the number or category is invalid, or the list cannot be saved.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws WodanException {
        int taskNumber = Parser.parseTagNumber(arguments, tasks);
        String category = Parser.parseTagCategory(arguments);
        assert taskNumber >= 1 && taskNumber <= tasks.size()
                : "parseTagNumber must return a number that exists in the list";
        Task currentTask = tasks.get(taskNumber - 1);
        currentTask.setCategory(category);
        storage.save(tasks.getTasks());
        ui.show("    The ravens branded this quest " + currentTask.getCategory() + ".",
                "     " + currentTask.toString());
    }
}
