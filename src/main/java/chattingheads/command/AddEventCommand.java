package chattingheads.command;

import java.time.LocalDateTime;

import chattingheads.exception.InvalidInputException;
import chattingheads.task.Event;
import chattingheads.task.Task;
import chattingheads.task.TaskList;
import chattingheads.ui.Ui;

/**
 * Represents a command that adds an event task.
 */
public class AddEventCommand extends Command {

    private final String description;
    private final LocalDateTime start;
    private final LocalDateTime end;

    /**
     * Creates a command for adding an event task.
     *
     * @param description Description of the event task.
     * @param start       Start date and time of the event.
     * @param end         End date and time of the event.
     */
    public AddEventCommand(String description, LocalDateTime start, LocalDateTime end) {
        this.description = description;
        this.start = start;
        this.end = end;
    }

    @Override
    public String execute(TaskList taskList, Ui ui) throws InvalidInputException {
        assert taskList != null : "TaskList should not be null";
        assert ui != null : "Ui should not be null";

        Task newTask = new Event(description, start, end);
        taskList.add(newTask);
        return ui.getAddStatus(newTask, taskList);
    }

    @Override
    public boolean shouldSave() {
        return true;
    }
}
