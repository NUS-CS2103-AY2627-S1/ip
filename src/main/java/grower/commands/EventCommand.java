package grower.commands;

import java.time.LocalDateTime;

import grower.tasks.Event;
import grower.tasks.Task;
import grower.tasks.TaskList;
import grower.ui.Ui;

/**
 * Represents a command that adds an event task.
 */
public class EventCommand extends Command {
    private final String description;
    private final LocalDateTime start;
    private final LocalDateTime end;

    /**
     * Creates a command that adds an event task.
     *
     * @param description Description of the event.
     * @param start Date and time at which the event begins.
     * @param end Date and time at which the event ends.
     */
    public EventCommand(String description, LocalDateTime start, LocalDateTime end) {
        this.description = description;
        this.start = start;
        this.end = end;
    }

    @Override
    public boolean execute(TaskList tasks, Ui ui) {
        Task newTask = new Event(description, start, end);
        tasks.addTask(newTask);
        ui.showTaskAdded(newTask);
        return true;
    }
}
