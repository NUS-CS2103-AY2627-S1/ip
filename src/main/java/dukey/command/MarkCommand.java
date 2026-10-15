package dukey.command;

import java.time.Clock;
import java.time.LocalDateTime;

import dukey.exception.DukeyException;
import dukey.storage.Storage;
import dukey.task.Task;
import dukey.task.TaskList;
import dukey.ui.Ui;

/**
 * Marks a task as done.
 */
public class MarkCommand extends Command {
    private final int taskNumber;
    private final Clock clock;

    /**
     * Creates a command that marks the given task using the system clock.
     *
     * @param taskNumber One-based task number to mark.
     */
    public MarkCommand(int taskNumber) {
        this(taskNumber, Clock.systemDefaultZone());
    }

    /**
     * Creates a command that marks the given task using the given clock.
     *
     * @param taskNumber One-based task number to mark.
     * @param clock Clock used to record the completion time.
     */
    public MarkCommand(int taskNumber, Clock clock) {
        this.taskNumber = taskNumber;
        this.clock = clock;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws DukeyException {
        if (!tasks.isValidTaskNumber(this.taskNumber)) {
            throw new DukeyException("Please provide a valid task number.");
        }

        Task task = tasks.get(this.taskNumber);
        task.markAsDone(LocalDateTime.now(this.clock));
        storage.save(tasks);
        ui.showTaskMarked(task);
    }

    @Override
    public String getResponseStyleClass() {
        return STYLE_STATUS;
    }
}
