package gunna.command;

import gunna.DukeException;
import gunna.Storage;
import gunna.TaskList;
import gunna.Ui;
import gunna.task.Event;
import gunna.task.Task;

/**
 * Represents a command to add an event task.
 */
public class EventCommand extends Command {
    private String description;
    private String from;
    private String to;

    /**
     * Constructs an EventCommand with the specified description, start time, and end time.
     *
     * @param description The description of the event.
     * @param from The start time of the event.
     * @param to The end time of the event.
     */
    public EventCommand(String description, String from, String to) {
        this.description = description;
        this.from = from;
        this.to = to;
    }

    /**
     * Executes the event command by creating and adding a new event task.
     *
     * @param tasks The task list to add the task to.
     * @param ui The UI to display success message.
     * @param storage The storage to save the updated task list.
     * @throws DukeException If the description or time fields are empty.
     */
    @Override
    public String execute(TaskList tasks, Ui ui, Storage storage) throws DukeException {
        if (description.isEmpty()) {
            throw new DukeException("Task details are required for an event.");
        }
        if (from.isEmpty() || to.isEmpty()) {
            throw new DukeException("Event start and end times are required.");
        }
        Task newTask = new Event(description, from, to);
        tasks.add(newTask);
        ui.showTaskAdded(newTask, tasks.size());
        storage.saveTasks(tasks.getTasks());
        return "Task secured:\n  " + newTask
                + "\nQueue now holds " + tasks.size() + " task(s).";
    }
}
