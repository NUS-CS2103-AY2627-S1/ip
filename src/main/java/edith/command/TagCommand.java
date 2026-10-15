package edith.command;

import java.util.List;

import edith.exception.EdithException;
import edith.task.Task;
import edith.task.TaskList;
import edith.ui.Ui;

/** Adds one or more tags to a task in the full task list. */
public class TagCommand extends Command {
    private final int taskNumber;
    private final List<String> tags;

    /**
     * Creates a command for a one-based task number and validated tags.
     *
     * @param taskNumber the task's position in the full list
     * @param tags the tags to add
     */
    public TagCommand(int taskNumber, List<String> tags) {
        this.taskNumber = taskNumber;
        this.tags = List.copyOf(tags);
    }

    /**
     * Adds missing tags, saves a changed task list, and displays the result.
     *
     * @param tasks the full task list containing the selected task
     * @param ui the user interface used to display the result
     * @throws EdithException if the task number is invalid or changed tasks cannot be saved
     */
    @Override
    public void execute(TaskList tasks, Ui ui) throws EdithException {
        if (tasks.isEmpty()) {
            throw new EdithException("There are no tasks to tag. Add a task first.");
        }
        if (taskNumber < 1 || taskNumber > tasks.size()) {
            throw new EdithException("Please provide a task number from 1 to " + tasks.size() + ".");
        }

        Task task = tasks.get(taskNumber - 1);
        List<String> previousTags = task.getTags();
        boolean hasChanged = false;
        for (String tag : tags) {
            if (task.addTag(tag)) {
                hasChanged = true;
            }
        }
        if (hasChanged) {
            saveTasks(tasks, () -> task.restoreTags(previousTags));
        }
        ui.showTagsAdded(task, hasChanged);
    }
}
