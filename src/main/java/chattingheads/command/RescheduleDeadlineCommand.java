package chattingheads.command;

import java.time.LocalDateTime;

import chattingheads.exception.InvalidTaskNumberException;
import chattingheads.exception.InvalidTaskTypeException;
import chattingheads.task.TaskList;
import chattingheads.ui.Ui;

/**
 * Represents a command that reschedules a deadline task.
 */
public class RescheduleDeadlineCommand extends Command {

    private final int taskNumber;
    private final LocalDateTime deadline;

    /**
     * Creates a command to reschedule the specified deadline.
     *
     * @param taskNumber Task number to reschedule.
     * @param deadline   New deadline date and time.
     */
    public RescheduleDeadlineCommand(int taskNumber, LocalDateTime deadline) {
        this.taskNumber = taskNumber;
        this.deadline = deadline;
    }

    @Override
    public String execute(TaskList taskList, Ui ui)
            throws InvalidTaskNumberException, InvalidTaskTypeException {
        assert taskList != null : "TaskList should not be null";
        assert ui != null : "Ui should not be null";

        taskList.rescheduleDeadline(taskNumber - 1, deadline);
        return ui.getRescheduleStatus(taskList.get(taskNumber - 1), "deadline");
    }

    public int getTaskNumber() {
        return taskNumber;
    }

    @Override
    public boolean shouldSave() {
        return true;
    }
}
