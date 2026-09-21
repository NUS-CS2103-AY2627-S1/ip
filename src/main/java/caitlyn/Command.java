package caitlyn;

import java.io.IOException;
import java.util.List;

/**
 * Represents one command that Caitlyn can execute.
 */
public abstract class Command {
    /** The storage used to persist changes made by this command. */
    private final TaskStorage storage;

    /**
     * Creates a command.
     */
    public Command() {
        this(new TaskStorage());
    }

    /**
     * Creates a command that saves through the supplied storage.
     *
     * @param storage the destination for task changes.
     */
    protected Command(TaskStorage storage) {
        this.storage = storage;
    }

    /**
     * Executes this command against the current task list.
     *
     * @param tasks the current task list.
     * @param ui the UI used to display results.
     * @throws CaitlynException when the command cannot be carried out.
     */
    public abstract void execute(List<Task> tasks, Ui ui) throws CaitlynException;

    /**
     * Returns whether this command ends the application.
     *
     * @return {@code true} only for the exit command.
     */
    public boolean isExit() {
        return false;
    }

    /**
     * Returns whether this command can run without changing tasks or their saved file.
     *
     * @return {@code false} unless the command explicitly declares itself read-only.
     */
    public boolean isReadOnly() {
        return false;
    }

    /**
     * Adds a task and saves the updated list, rolling back if saving fails.
     *
     * @param tasks the current task list.
     * @param task the task to add.
     * @throws CaitlynException if the task cannot be saved.
     */
    protected final void addTask(List<Task> tasks, Task task) throws CaitlynException {
        tasks.add(task);
        try {
            saveTasks(tasks);
        } catch (CaitlynException exception) {
            tasks.remove(tasks.size() - 1);
            throw exception;
        }
    }

    /**
     * Saves the current task list and turns a file-system error into a user-facing error.
     *
     * @param tasks the current task list.
     * @throws CaitlynException if the task file cannot be written.
     */
    protected final void saveTasks(List<Task> tasks) throws CaitlynException {
        try {
            storage.save(tasks);
        } catch (IOException | IllegalArgumentException | SecurityException exception) {
            throw new CaitlynException(
                    "I beg your pardon, master. I could not save your tasks to disk.");
        }
    }

    /**
     * Parses and validates the one-based task number in a command.
     *
     * @param command the complete command entered by the user.
     * @param commandName the command name used in validation examples.
     * @param tasks the current task list.
     * @return the zero-based index of the selected task.
     * @throws CaitlynException if the command does not contain a valid task number.
     */
    protected final int parseTaskIndex(String command, String commandName, List<Task> tasks)
            throws CaitlynException {
        String[] commandParts = command.split("\\s+");
        if (commandParts.length != 2) {
            throw new CaitlynException(
                    "I beg your pardon, master. Please provide a task number, for example: "
                            + commandName + " 2.");
        }

        int taskNumber;
        try {
            taskNumber = Integer.parseInt(commandParts[1]);
        } catch (NumberFormatException exception) {
            throw new CaitlynException(
                    "I beg your pardon, master. Please provide a valid task number, for example: "
                            + commandName + " 2.");
        }

        if (taskNumber < 1 || taskNumber > tasks.size()) {
            throw new CaitlynException(
                    "I beg your pardon, master, but I could not find task " + taskNumber + ".");
        }
        return taskNumber - 1;
    }
}
