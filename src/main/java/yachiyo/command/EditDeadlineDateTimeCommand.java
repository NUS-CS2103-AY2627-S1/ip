package yachiyo.command;

import static yachiyo.exception.ErrorCategory.INVALID_OPERATION;

import java.time.LocalDateTime;

import yachiyo.exception.YachiyoException;
import yachiyo.task.Deadline;
import yachiyo.task.Task;

/**
 * Replaces the due date-time of a selected deadline.
 */
public class EditDeadlineDateTimeCommand extends EditCommand {
    private final LocalDateTime by;

    /**
     * Creates a command that replaces the due date-time of a selected deadline.
     *
     * @param taskNumber one-based number of the deadline to edit.
     * @param by replacement due date and time.
     */
    public EditDeadlineDateTimeCommand(int taskNumber, LocalDateTime by) {
        super(taskNumber);
        assert by != null : "Replacement deadline date-time must not be null";

        this.by = by;
    }

    @Override
    protected Task createEditedTask(Task task) throws YachiyoException {
        if (!(task instanceof Deadline deadline)) {
            throw new YachiyoException(INVALID_OPERATION,
                    "Only deadline tasks have a /by date to edit.");
        }
        return deadline.withBy(by);
    }
}
