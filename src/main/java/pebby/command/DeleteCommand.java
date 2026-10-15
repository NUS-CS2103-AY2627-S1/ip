package pebby.command;

import pebby.storage.Storage;
import pebby.task.Task;
import pebby.task.TaskList;
import pebby.ui.Ui;

/**
 * Deletes the task identified by a user-supplied one-based task number.
 */
public class DeleteCommand extends Command {
    private final String argument;

    /**
     * Creates a delete command using the text after the {@code delete} keyword.
     */
    public DeleteCommand(String argument) {
        this.argument = argument;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        try {
            Task deletedTask = tasks.delete(taskIndexFrom(argument, tasks));
            String output = "Noted. I've removed this task: \n" + deletedTask;
            ui.showLine(output + '\n' + taskCountMessage(tasks) + saveTasks(tasks, storage));
        } catch (IllegalArgumentException exception) {
            ui.showLine("Invalid delete: " + exception.getMessage());
        }
    }

    /** Converts a validated one-based task number into a zero-based list index. */
    private int taskIndexFrom(String taskNumberText, TaskList tasks) {
        try {
            int taskNumber = Integer.parseInt(taskNumberText);
            if (taskNumber < 1 || taskNumber > tasks.size()) {
                throw new IllegalArgumentException(
                        "Please choose a task number from 1 to " + tasks.size() + ".");
            }
            int taskIndex = taskNumber - 1;
            assert taskIndex >= 0 && taskIndex < tasks.size()
                    : "A validated task number must produce a valid zero-based index.";
            return taskIndex;
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("Please provide a whole task number.");
        }
    }

    /** Returns Pebby's message stating the current number of tasks. */
    private String taskCountMessage(TaskList tasks) {
        return "Now you have " + tasks.size() + " tasks in the list.";
    }
}
