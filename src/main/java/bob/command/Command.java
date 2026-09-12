package bob.command;

import java.io.IOException;
import java.util.List;

import bob.ResponseType;
import bob.exception.BobException;
import bob.storage.Storage;
import bob.task.Task;
import bob.task.TaskList;

/**
 * Represents an executable user command.
 */
public abstract class Command {
    /**
     * Creates a command.
     */
    protected Command() {
    }

    /** Formats the fixed add-task response. */
    protected String addResponse(Task task, int count) {
        return "Excellent! I've added this task:\nadded: " + task
                + "\nYou now have " + count + " tasks to conquer.";
    }

    /** Formats the fixed delete-task response. */
    protected String deleteResponse(Task task, int count) {
        String label = count == 1 ? "task" : "tasks";
        return "Consider it cleared:\n" + task + "\nThere are " + count + " " + label + " left in your list.";
    }

    /** Formats the fixed completion response. */
    protected String markResponse(Task task) {
        return "Great work! This task is complete:\n  " + task;
    }

    /** Formats the fixed reopening response. */
    protected String unmarkResponse(Task task) {
        return "No problem—I've reopened this task:\n  " + task;
    }

    /**
     * Applies this command and returns the resulting response.
     *
     * @param tasks task list on which the command operates.
     * @param storage storage used to persist task changes.
     * @return response to display to the user.
     * @throws BobException if the command cannot be applied.
     * @throws IOException if updated tasks cannot be saved.
     */
    public abstract String execute(TaskList tasks, Storage storage) throws BobException, IOException;

    /**
     * Returns the semantic type of the response produced by this command.
     *
     * @return response type for presentation purposes.
     */
    public abstract ResponseType getResponseType();

    /**
     * Returns whether this command should end the chatbot session.
     *
     * @return true if the chatbot should exit.
     */
    public boolean isExit() {
        return false;
    }

    /**
     * Formats tasks as a numbered response under the supplied heading.
     *
     * @param heading response heading.
     * @param tasks tasks to include in the response.
     * @return formatted response containing the numbered tasks.
     */
    protected String formatTasks(String heading, List<Task> tasks) {
        StringBuilder response = new StringBuilder(heading);

        for (int i = 0; i < tasks.size(); i++) {
            response.append('\n')
                    .append(i + 1)
                    .append('.')
                    .append(tasks.get(i));
        }

        return response.toString();
    }

    /**
     * Returns the task identified by a one-based command number.
     *
     * @param taskList task list to search.
     * @param taskNumber one-based task number.
     * @return task at the requested number.
     * @throws BobException if the task number does not identify a task.
     */
    protected Task getTask(TaskList taskList, int taskNumber) throws BobException {
        return taskList.get(taskNumber - 1);
    }

    /**
     * Saves the current task list.
     *
     * @param taskList task list to save.
     * @param storage storage used to persist the task list.
     * @throws IOException if the task list cannot be saved.
     */
    protected void saveTasks(TaskList taskList, Storage storage) throws IOException {
        storage.save(taskList.getTasks());
    }
}
