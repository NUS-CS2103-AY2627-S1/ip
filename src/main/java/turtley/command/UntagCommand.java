package turtley.command;

import java.util.List;

import turtley.model.Task;
import turtley.model.TaskList;
import turtley.storage.Storage;
import turtley.ui.Ui;

/**
 * Removes one or more tags from an existing task.
 */
public class UntagCommand extends IndexedCommand {

    private static final String FORMAT = "Invalid format. Use: untag <task number> #tag1 [#tag2 ...]";
    private final String input;

    /**
     * Creates an untag command.
     *
     * @param input the task number and tags.
     */
    public UntagCommand(String input) {
        this.input = input;
    }

    /**
     * Removes tags from the selected task and persists the updated task list.
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
        List<String> removedTags = task.removeTags(parsedInput.tags());
        saveOrRollback(tasks, storage, () -> task.addTags(removedTags));
        ui.showTaskUntagged(task);
    }
}
