package luna.command;

import java.io.IOException;

import luna.LunaException;
import luna.storage.Storage;
import luna.task.Task;
import luna.task.TaskList;
import luna.ui.Ui;

/**
 * Represents an executable user command.
 */
public abstract class Command {
    private static final String DUPLICATE_TASK_MESSAGE = "That task is already on your active list.";

    /**
     * Executes this command against the current task list.
     *
     * @param tasks Current tasks in memory.
     * @param ui User interface for showing results.
     * @param storage Storage used to persist task changes.
     * @throws LunaException If the command cannot be executed.
     */
    public abstract void execute(TaskList tasks, Ui ui, Storage storage) throws LunaException;

    /**
     * Returns whether this command should terminate the application.
     *
     * @return {@code true} if the application should exit.
     */
    public boolean isExit() {
        return false;
    }

    /**
     * Returns whether executing this command changes persisted task data.
     *
     * @return {@code true} if the command writes active or archived tasks.
     */
    public boolean isMutating() {
        return false;
    }

    /**
     * Saves the current task list to storage.
     *
     * @param storage Storage used to persist tasks.
     * @param tasks Tasks to save.
     * @throws LunaException If saving fails.
     */
    protected void saveTasks(Storage storage, TaskList tasks) throws LunaException {
        try {
            storage.saveTasks(tasks);
        } catch (IOException | SecurityException e) {
            throw new LunaException("I could not save your tasks to disk.");
        }
    }

    /**
     * Validates and converts a command argument containing one task number.
     *
     * @param input Full command input.
     * @param commandWord Command word preceding the number.
     * @param taskCount Number of tasks currently available.
     * @param action Description of the requested action for error messages.
     * @return Zero-based task index.
     * @throws LunaException If the argument is missing, malformed, too large, or outside the list.
     */
    protected int parseTaskIndex(String input, String commandWord, int taskCount, String action)
            throws LunaException {
        String argument = input.substring(commandWord.length()).trim();
        if (!argument.matches("[1-9][0-9]*")) {
            throw new LunaException("Please provide exactly one positive whole task number to " + action + ".");
        }

        int oneBasedIndex;
        try {
            oneBasedIndex = Integer.parseInt(argument);
        } catch (NumberFormatException e) {
            throw new LunaException("That task number is too large.");
        }

        if (oneBasedIndex > taskCount) {
            if (taskCount == 0) {
                throw new LunaException("There are no active tasks to " + action + ".");
            }
            throw new LunaException("Task " + oneBasedIndex + " does not exist. Choose a number from 1 to "
                    + taskCount + ".");
        }

        return oneBasedIndex - 1;
    }

    /**
     * Saves and adds a task only after checking that its details are unique.
     *
     * @param task Task to add.
     * @param tasks Current in-memory tasks.
     * @param ui User interface for showing the result.
     * @param storage Storage used to persist the updated list.
     * @throws LunaException If the task is a duplicate or cannot be saved.
     */
    protected void addTask(Task task, TaskList tasks, Ui ui, Storage storage) throws LunaException {
        if (tasks.containsTaskWithSameDetails(task)) {
            throw new LunaException(DUPLICATE_TASK_MESSAGE);
        }

        TaskList updatedTasks = new TaskList(tasks.asList());
        updatedTasks.add(task);
        saveTasks(storage, updatedTasks);
        tasks.add(task);
        ui.showAddSuccess(task, tasks.size());
    }
}
