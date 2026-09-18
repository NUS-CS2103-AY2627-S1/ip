package benjamin.command;

import benjamin.BenjaminException;
import benjamin.storage.Storage;
import benjamin.task.Task;
import benjamin.task.TaskList;
import benjamin.ui.Ui;

/** Attaches a label to one task. */
public class TagCommand extends Command {
    private final int taskNumber;
    private final String tag;

    /**
     * Creates a command that will tag the given task.
     *
     * @param taskNumber the one based position the user typed, checked when
     *     the command runs.
     * @param tag the label to attach, already stripped of any leading hash.
     */
    public TagCommand(int taskNumber, String tag) {
        this.taskNumber = taskNumber;
        this.tag = tag;
    }

    /**
     * Attaches the label and saves, or says so if the task already had it.
     *
     * <p>Tagging twice is not an error, so nothing is saved in that case.
     *
     * @throws BenjaminException if there is no such task, or it cannot be saved.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws BenjaminException {
        int index = toIndex(taskNumber, "tag", tasks);
        Task task = tasks.get(index);

        if (!task.addTag(tag)) {
            ui.showAlreadyTagged(tag);
            return;
        }

        ui.showTagged(task);
        storage.save(tasks);
    }
}
