package kaykay.command;

import java.time.LocalDateTime;

import kaykay.model.Event;
import kaykay.model.Task;
import kaykay.ui.Ui;

/**
 * Adds an event task.
 */
public final class EventCommand extends AddCommand {
    /** Event start date and time. */
    private final LocalDateTime startDateTime;

    /** Event end date and time. */
    private final LocalDateTime endDateTime;

    /**
     * Creates an event command with its description and date/time range.
     *
     * @param description description of the event.
     * @param startDateTime date and time when the event starts.
     * @param endDateTime date and time when the event ends.
     */
    public EventCommand(String description, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        super(description);
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
    }

    /**
     * Creates the event represented by this command.
     *
     * @return an event with the command's description and date/time range.
     */
    @Override
    protected Task createTask() {
        return new Event(getDescription(), startDateTime, endDateTime);
    }

    /** Shows an event-specific confirmation after the event is saved. */
    @Override
    protected void showConfirmation(Ui ui, Task addedTask, int taskCount) {
        ui.showScheduledEvent(addedTask, taskCount);
    }
}
