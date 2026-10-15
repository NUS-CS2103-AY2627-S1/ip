package judey.command;

import judey.exception.JudeyException;
import judey.storage.Storage;
import judey.task.Event;
import judey.task.Task;
import judey.tasklist.TaskList;
import judey.ui.Ui;

import java.time.LocalDateTime;

/**
 * Represents the action of adding an Event task
 */
public class AddEventCommand extends Command {
    private final String description;
    private final String from;
    private final String to;

    /**
     * Creates an Event task
     * @param description represents what the task is about
     * @param from represents the time at which the event starts
     * @param to represents the time at which the event ends
     */
    public AddEventCommand(String description, String from, String to) {
        this.description = description;
        this.from = from;
        this.to = to;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws JudeyException {
        Event task = new Event(description, from, to);
        task.validateStartIsNotPast(LocalDateTime.now());
        tasks.add(task);
        storage.save(tasks.getTasks());
        ui.showTaskAdded(task, tasks.getSize());
    }
}
