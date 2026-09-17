package yachiyo.command;

import static yachiyo.exception.ErrorCategory.INVALID_OPERATION;

import java.time.LocalDateTime;

import yachiyo.exception.YachiyoException;
import yachiyo.task.Event;
import yachiyo.task.Task;

/**
 * Replaces the start date-time of a selected event.
 */
public class EditEventStartDateTimeCommand extends EditCommand {
    private final LocalDateTime from;

    /**
     * Creates a command that replaces the start date-time of a selected event.
     *
     * @param taskNumber one-based number of the event to edit.
     * @param from replacement start date and time.
     */
    public EditEventStartDateTimeCommand(int taskNumber, LocalDateTime from) {
        super(taskNumber);
        assert from != null : "Replacement event start date-time must not be null";

        this.from = from;
    }

    @Override
    protected Task createEditedTask(Task task) throws YachiyoException {
        if (!(task instanceof Event event)) {
            throw new YachiyoException(INVALID_OPERATION,
                    "Only event tasks have a /from start time to edit.");
        }
        if (!event.getTo().isAfter(from)) {
            throw new YachiyoException(INVALID_OPERATION,
                    "Hmm, the event should start before it ends.");
        }
        return event.withFrom(from);
    }
}
