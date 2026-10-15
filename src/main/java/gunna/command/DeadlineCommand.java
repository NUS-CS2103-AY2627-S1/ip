package gunna.command;

import java.time.format.DateTimeParseException;

import gunna.DukeException;
import gunna.Storage;
import gunna.TaskList;
import gunna.Ui;
import gunna.task.Deadline;
import gunna.task.Task;

/**
 * Represents a command to add a deadline task.
 */
public class DeadlineCommand extends Command {
    private String description;
    private String by;

    /**
     * Constructs a DeadlineCommand with the specified description and deadline.
     *
     * @param description The description of the deadline task.
     * @param by The deadline time/date string.
     */
    public DeadlineCommand(String description, String by) {
        this.description = description;
        this.by = by;
    }

    /**
     * Executes the deadline command by creating and adding a new deadline task.
     *
     * @param tasks The task list to add the task to.
     * @param ui The UI to display success message.
     * @param storage The storage to save the updated task list.
     * @throws DukeException If the description or deadline is empty, or if the date format is invalid.
     */
    @Override
    public String execute(TaskList tasks, Ui ui, Storage storage) throws DukeException {
        if (description.isEmpty()) {
            throw new DukeException("Task details are required for a deadline.");
        }
        if (by.isEmpty()) {
            throw new DukeException("Deadline date is required.");
        }
        try {
            Task newTask = Deadline.createWithDateString(description, by);
            tasks.add(newTask);
            ui.showTaskAdded(newTask, tasks.size());
            storage.saveTasks(tasks.getTasks());
            return "Task secured:\n  " + newTask
                    + "\nQueue now holds " + tasks.size() + " task(s).";
        } catch (DateTimeParseException e) {
            throw new DukeException("Date format invalid. Use yyyy-MM-dd (e.g., 2019-12-31).");
        }
    }
}
