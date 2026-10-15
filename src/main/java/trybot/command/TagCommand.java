package trybot.command;

import trybot.exception.TryBotException;
import trybot.storage.Storage;
import trybot.task.Task;
import trybot.task.TaskList;
import trybot.ui.Ui;

/** Attaches a descriptive tag to one task. */
public class TagCommand extends Command {
    private final int taskNumber;
    private final String tagName;

    /**
     * Creates a tag command.
     *
     * @param taskNumber one-based task number.
     * @param tagName tag name to attach.
     */
    public TagCommand(int taskNumber, String tagName) {
        this.taskNumber = taskNumber;
        this.tagName = tagName;
    }

    /**
     * Attaches the tag, saves the updated list, and reports the result.
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
        task.addTag(tagName);
        saveTasks(tasks, ui, storage);
        ui.showTagCreated(taskNumber, tagName);
    }

    /** @return always false because tagging does not end the session. */
    @Override
    public boolean isExit() {
        return false;
    }
}
