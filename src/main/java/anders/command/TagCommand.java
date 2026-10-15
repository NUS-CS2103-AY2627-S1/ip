package anders.command;

import java.util.Arrays;
import java.util.List;

import anders.AndersException;
import anders.collection.TaskList;
import anders.storage.Storage;
import anders.task.Task;
import anders.ui.Ui;

/** Adds or removes tags from a selected task. */
public class TagCommand extends Command {
    private final String taskNumberText;
    private final List<String> tags;
    private final boolean isAdding;

    /**
     * Creates a command that adds or removes the supplied tags.
     *
     * @param arguments the one-based task number followed by tags
     * @param isAdding whether to add the tags rather than remove them
     */
    public TagCommand(String arguments, boolean isAdding) {
        String[] fields = arguments.trim().split("\\s+");
        this.taskNumberText = fields[0];
        this.tags = List.copyOf(Arrays.asList(Arrays.copyOfRange(fields, 1, fields.length)));
        this.isAdding = isAdding;
    }

    /** Applies the tag changes to the selected task and saves successful changes. */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws AndersException {
        try {
            int index = Integer.parseInt(taskNumberText) - 1;
            if (index < 0 || index >= tasks.size()) {
                ui.showInvalidTaskNumber(tasks.size());
                return;
            }
            Task task = tasks.get(index);
            boolean hasChanged = isAdding ? task.addTags(tags) : task.removeTags(tags);
            if (hasChanged) {
                storage.save(tasks);
            }
            ui.showTagUpdate(task, isAdding, hasChanged);
        } catch (NumberFormatException e) {
            ui.showInvalidTaskNumberFormat();
        }
    }
}
