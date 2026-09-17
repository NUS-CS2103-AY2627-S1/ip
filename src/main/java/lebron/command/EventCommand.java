package lebron.command;

import java.time.LocalDateTime;

import lebron.exception.LeBronException;
import lebron.task.Event;
import lebron.task.Task;
import lebron.task.TaskList;
import lebron.ui.Ui;

/**
 * Represents the "event" command, which adds an Event to the TaskList.
 */
public class EventCommand implements Command {
    private final String description;
    private final LocalDateTime start;
    private final LocalDateTime end;

    /**
     * Constructs an EventCommand with the given description and start/end date/time.
     *
     * @param description The Event's description.
     * @param start The date/time the Event starts.
     * @param end The date/time the Event ends.
     */
    public EventCommand(String description, LocalDateTime start, LocalDateTime end) {
        this.description = description;
        this.start = start;
        this.end = end;
    }

    @Override
    public String execute(TaskList taskList, Ui ui) throws LeBronException {
        Task task = new Event(description, start, end);
        taskList.addTask(task);
        return ui.showEventAdded(task, taskList.size());
    }
}
