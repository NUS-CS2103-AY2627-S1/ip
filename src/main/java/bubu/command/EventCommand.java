package bubu.command;

import java.time.LocalDateTime;

import bubu.exception.BubuException;
import bubu.storage.Storage;
import bubu.task.Event;
import bubu.task.TaskList;
import bubu.ui.Ui;

/**
 * Adds an event task.
 */
public class EventCommand extends AddTaskCommand {
    private final String description;
    private final LocalDateTime start;
    private final LocalDateTime end;

    /**
     * Creates an event command with its parsed start and end date-times.
     */
    public EventCommand(String description, LocalDateTime start, LocalDateTime end) {
        this.description = description;
        this.start = start;
        this.end = end;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws BubuException {
        addTask(new Event(description, start, end), tasks, ui, storage);
    }
}
