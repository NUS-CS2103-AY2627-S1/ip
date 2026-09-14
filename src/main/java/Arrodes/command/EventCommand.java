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
    /** Event start. */
    private final LocalDateTime from;
    /** Event end. */
    private final LocalDateTime to;
    /** Whether the input included a time at the start. */
    private final boolean isTimeIncludedInStartDate;
    /** Whether the input included a time at the end. */
    private final boolean isTimeIncludedInEndDate;

    /**
     * Creates a command for adding an event.
     * @param description event description
     * @param from event start
     * @param to event end
     * @param isTimeIncludedInStartDate whether the start included a time
     * @param isTimeIncludedInEndDate whether the end included a time
     */
    public EventCommand(String description, LocalDateTime from, LocalDateTime to,
                         boolean isTimeIncludedInStartDate, boolean isTimeIncludedInEndDate) {
        assert description != null : "Event description must not be null.";
        assert from != null : "Event start must not be null.";
        assert to != null : "Event end must not be null.";
        this.description = description;
        this.from = from;
        this.to = to;
        this.isTimeIncludedInStartDate = isTimeIncludedInStartDate;
        this.isTimeIncludedInEndDate = isTimeIncludedInEndDate;
    }

    /**
     * Adds the event, persists it, and reports the new task count.
     * @param ui interface used for output
     * @param taskList list to update
     * @param storage persistence layer
     */
    @Override
    public void execute(Ui ui, TaskList taskList, Storage storage) {
        if (to.isBefore(from)) {
            throw new ArrodesException(ArrodesException.INVALID_EVENT_TIME);
        }
        taskList.insert(new Event(description, from, to, isTimeIncludedInStartDate, isTimeIncludedInEndDate));
        storage.save(taskList);
        ui.showMessage("Your request has been inscribed in Arrodes' annals:\n"
                + "   " + taskList.getTaskByNumber(taskList.getSize()).toString() + "\n"
                + taskList.getSize() + " tasks are being tracked");
    }
}
