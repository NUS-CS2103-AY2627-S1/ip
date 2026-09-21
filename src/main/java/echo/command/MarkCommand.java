package echo.command;

import java.time.LocalDateTime;

import echo.exception.EchoException;
import echo.storage.Storage;
import echo.task.Task;
import echo.task.TaskList;
import echo.ui.Ui;

/**
 * Updates the completion status of the task at a zero-based index.
 * Marking done and marking not done share this class because they differ
 * only in the status written and the wording of the confirmation.
 */
public class MarkCommand extends Command {
    private static final String MARK_DONE_MESSAGE = "Mission complete. Nicely landed:";
    private static final String MARK_NOT_DONE_MESSAGE = "Mission reopened. It's back on the radar:";
    private final int index;
    private final boolean isDone;

    /**
     * Creates a command that updates one task's completion status.
     *
     * @param index zero-based index of the task to update
     * @param done true to mark as done, false to mark as not done
     */
    public MarkCommand(int index, boolean done) {
        this.index = index;
        this.isDone = done;
    }

    /**
     * Updates the task's status, saves the list, and confirms the change.
     * See: {@link Command#execute(TaskList, Ui, Storage)}
     */
    @Override
    public String execute(TaskList taskList, Ui ui, Storage storage) throws EchoException {
        Task task = taskList.getTask(index);
        LocalDateTime previousCompletionTime = task.getCompletedAt();
        if (this.isDone) {
            task.markDone();
        } else {
            task.markNotDone();
        }
        try {
            storage.save(taskList.getAll());
        } catch (EchoException exception) {
            restoreCompletionTime(task, previousCompletionTime);
            throw exception;
        }

        String confirmation = isDone
                ? MARK_DONE_MESSAGE
                : MARK_NOT_DONE_MESSAGE;
        return ui.joinLines(confirmation, task.toString());
    }

    private void restoreCompletionTime(Task task, LocalDateTime previousCompletionTime) {
        if (previousCompletionTime == null) {
            task.markNotDone();
        } else {
            task.markDone(previousCompletionTime);
        }
    }
}
