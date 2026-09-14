package arrodes.command;

import arrodes.exception.ArrodesException;
import arrodes.storage.Storage;
import arrodes.task.Task;
import arrodes.task.TaskList;
import arrodes.ui.Ui;


/** Command that assigns a tag to a task in the task list. */
public class TagCommand extends Command {
    /** One-based number of the task to tag. */
    private final int taskNumber;
    /** Tag to assign to the task. */
    private final String tag;

    /**
     * Creates a command for tagging a task.
     *
     * @param taskNumber one-based task number
     * @param tag tag to assign to the task
     */
    public TagCommand(int taskNumber, String tag) {
        assert tag != null : "Task tag must not be null.";
        this.taskNumber = taskNumber;
        this.tag = tag;
    }
    /**
     * Tags the task, persists the list, and reports the result.
     * @param ui interface used for output
     * @param taskList list to update
     * @param storage persistence layer
     */
    @Override
    public void execute(Ui ui, TaskList taskList, Storage storage) {
        Task task = taskList.getTaskByNumber(taskNumber);
        String previousTag = task.getTag();
        task.tagWith(tag);
        try {
            storage.save(taskList);
        } catch (ArrodesException exception) {
            task.tagWith(previousTag);
            throw exception;
        }
        ui.showMessage(
                "Arrodes seals this record with the "
                + tag
                + " sigil:"
                + "\n"
                + "  "
                + task);
    }
}
