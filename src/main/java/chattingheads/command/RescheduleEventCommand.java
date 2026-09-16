package chattingheads.command;

import java.time.LocalDateTime;

import chattingheads.exception.InvalidInputException;
import chattingheads.exception.InvalidTaskNumberException;
import chattingheads.exception.InvalidTaskTypeException;
import chattingheads.task.TaskList;
import chattingheads.ui.Ui;

/**
 * Represents a command that reschedules an event task.
 */
public class RescheduleEventCommand extends Command {

    private final int taskNumber;
    private final LocalDateTime start;
    private final LocalDateTime end;

    /**
     * Creates a command to reschedule the specified event.
     *
     * @param taskNumber Task number to reschedule.
     * @param start      New start date and time of the event.
     * @param end        New end date and time of the event.
     */
    public RescheduleEventCommand(int taskNumber, LocalDateTime start, LocalDateTime end) {
        this.taskNumber = taskNumber;
        this.start = start;
        this.end = end;
    }

    @Override
    public String execute(TaskList taskList, Ui ui)
            throws InvalidTaskNumberException, InvalidTaskTypeException, InvalidInputException {
        assert taskList != null : "TaskList should not be null";
        assert ui != null : "Ui should not be null";

        taskList.rescheduleEvent(taskNumber - 1, start, end);
        return ui.getRescheduleStatus(taskList.get(taskNumber - 1), "event");
    }

    public int getTaskNumber() {
        return taskNumber;
    }

    @Override
    public boolean shouldSave() {
        return true;
    }
}
