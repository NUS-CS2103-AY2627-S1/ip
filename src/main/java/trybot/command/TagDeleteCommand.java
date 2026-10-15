package trybot.command;

import trybot.exception.TryBotException;
import trybot.storage.Storage;
import trybot.task.Task;
import trybot.task.TaskList;
import trybot.ui.Ui;

/** Removes all tags attached to one task. */
public class TagDeleteCommand extends Command {
    private final int taskNumber;

    /**
     * Creates a tag-delete command.
     *
     * @param taskNumber one-based task number.
     */
    public TagDeleteCommand(int taskNumber) {
        this.taskNumber = taskNumber;
    }

    /**
     * Removes all tags, saves the updated list, and reports the result.
     *
     * @param tasks current task list.
     * @param ui user-interface handler.
     * @param storage task persistence handler.
     * @throws TryBotException if the task number is outside the list
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws TryBotException {
        if (taskNumber < 1 || taskNumber > tasks.size()) {
            throw new TryBotException("That task number does not exist. Use list to see your task numbers.");
        }
        Task task = tasks.get(taskNumber - 1);
        task.clearTags();
        saveTasks(tasks, ui, storage);
        ui.showTagsDeleted(taskNumber);
    }

    /** @return always false because removing tags does not end the session. */
    @Override
    public boolean isExit() {
        return false;
    }
}
