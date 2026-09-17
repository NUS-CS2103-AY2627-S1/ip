package yachiyo.command;

import static yachiyo.exception.ErrorCategory.INVALID_OPERATION;

import java.time.LocalDateTime;

import yachiyo.exception.YachiyoException;
import yachiyo.task.Event;
import yachiyo.task.Task;

/**
 * Replaces the end date-time of a selected event.
 */
public class EditEventEndDateTimeCommand extends EditCommand {
    private final LocalDateTime to;

    /**
     * Creates a command that replaces the end date-time of a selected event.
     *
     * @param taskNumber one-based number of the event to edit.
     * @param to replacement end date and time.
     */
    public EditEventEndDateTimeCommand(int taskNumber, LocalDateTime to) {
        super(taskNumber);
        assert to != null : "Replacement event end date-time must not be null";

        this.to = to;
    }

    @Override
    protected Task createEditedTask(Task task) throws YachiyoException {
        if (!(task instanceof Event event)) {
            throw new YachiyoException(INVALID_OPERATION,
                    "Only event tasks have a /to end time to edit.");
        }
        if (!to.isAfter(event.getFrom())) {
            throw new YachiyoException(INVALID_OPERATION,
                    "Hmm, the event should end after it starts.");
        }
        return event.withTo(to);
    }
}
