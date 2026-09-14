package arrodes.command;

import java.time.LocalDateTime;

import arrodes.exception.ArrodesException;
import arrodes.storage.Storage;
import arrodes.task.Event;
import arrodes.task.TaskList;
import arrodes.ui.Ui;


/** Command that adds an event task and saves the updated list. */
public class EventCommand extends Command {
    /** Text describing the event. */
    private final String description;
    /** Event start date and time. */
    private final LocalDateTime startAt;
    /** Event end date and time. */
    private final LocalDateTime endAt;
    /** Whether the input included a time at the start. */
    private final boolean isStartTimeIncluded;
    /** Whether the input included a time at the end. */
    private final boolean isEndTimeIncluded;

    /**
     * Creates a command for adding an event.
     * @param description event description
     * @param startAt event start
     * @param endAt event end
     * @param isStartTimeIncluded whether the start included a time
     * @param isEndTimeIncluded whether the end included a time
     */
    public EventCommand(String description, LocalDateTime startAt, LocalDateTime endAt,
                         boolean isStartTimeIncluded, boolean isEndTimeIncluded) {
        assert description != null : "Event description must not be null.";
        assert startAt != null : "Event start must not be null.";
        assert endAt != null : "Event end must not be null.";
        this.description = description;
        this.startAt = startAt;
        this.endAt = endAt;
        this.isStartTimeIncluded = isStartTimeIncluded;
        this.isEndTimeIncluded = isEndTimeIncluded;
    }

    /**
     * Adds the event, persists it, and reports the new task count.
     * @param ui interface used for output
     * @param taskList list to update
     * @param storage persistence layer
     */
    @Override
    public void execute(Ui ui, TaskList taskList, Storage storage) {
        if (endAt.isBefore(startAt)) {
            throw new ArrodesException(ArrodesException.INVALID_EVENT_TIME);
        }
        Event event = new Event(description, startAt, endAt, isStartTimeIncluded, isEndTimeIncluded);
        addTaskAndSave(event, ui, taskList, storage);
    }
}
