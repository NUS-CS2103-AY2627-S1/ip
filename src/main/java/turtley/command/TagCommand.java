package turtley.command;

import java.util.List;

import turtley.model.Task;
import turtley.model.TaskList;
import turtley.storage.Storage;
import turtley.ui.Ui;

/**
 * Adds one or more tags to an existing task.
 */
public class TagCommand extends IndexedCommand {

    private static final String FORMAT = "Invalid format. Use: tag <task number> #tag1 [#tag2 ...]";
    private final String input;

    /**
     * Creates a tag command.
     *
     * @param input the task number and tags.
     */
    public TagCommand(String input) {
        this.input = input;
    }

    /**
     * Adds tags to the selected task and persists the updated task list.
     *
     * @param tasks the application's task list.
     * @param ui the application's user interface.
     * @param storage the application's persistence service.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        TagParser.IndexedTags parsedInput = TagParser.parseIndexedTags(input, FORMAT);
        int taskIndex = requireTaskIndex(parsedInput.taskNumber(), tasks);
        Task task = tasks.get(taskIndex);
        List<String> addedTags = task.addTags(parsedInput.tags());
        saveOrRollback(tasks, storage, () -> task.removeTags(addedTags));
        ui.showTaskTagged(task);
    }
}
