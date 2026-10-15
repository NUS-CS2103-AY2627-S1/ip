package gunna.command;

import gunna.DukeException;
import gunna.Storage;
import gunna.TaskList;
import gunna.Ui;
import gunna.task.Task;
import gunna.task.Todo;

/**
 * Represents a command to add a todo task.
 */
public class TodoCommand extends Command {
    private String description;

    /**
     * Constructs a TodoCommand with the specified task description.
     *
     * @param description The description of the todo task.
     */
    public TodoCommand(String description) {
        this.description = description;
    }

    /**
     * Executes the todo command by creating and adding a new todo task.
     *
     * @param tasks The task list to add the task to.
     * @param ui The UI to display success message.
     * @param storage The storage to save the updated task list.
     * @throws DukeException If the description is empty.
     */
    @Override
    public String execute(TaskList tasks, Ui ui, Storage storage) throws DukeException {
        if (description.isEmpty()) {
            throw new DukeException("Task details are required for a todo.");
        }
        Task newTask = new Todo(description);
        tasks.add(newTask);
        ui.showTaskAdded(newTask, tasks.size());
        storage.saveTasks(tasks.getTasks());
        return "Task secured:\n  " + newTask
                + "\nQueue now holds " + tasks.size() + " task(s).";
    }
}
