package verity.command;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import verity.VerityException;
import verity.storage.Storage;
import verity.task.Task;
import verity.task.TaskList;

/**
 * Represents a single user command: something that can be executed against
 * the current task list, returning a response to show the user and
 * persisting any changes through storage.
 */
public abstract class Command {

    /**
     * Executes this command.
     *
     * @param tasks Task list to act on.
     * @param storage Storage to persist any changes through.
     * @return Response to show the user.
     * @throws VerityException If the command can't be carried out, e.g. an invalid task index.
     */
    public abstract String execute(TaskList tasks, Storage storage) throws VerityException;

    /**
     * Returns whether this command should end the program after executing.
     * Overridden by {@link ExitCommand}; every other command keeps the program running.
     *
     * @return {@code true} if the program should exit.
     */
    public boolean isExit() {
        return false;
    }

    /**
     * Saves the task list to disk, returning a warning to append to the response instead of
     * failing if it can't be saved. Shared by every command that mutates the task list.
     *
     * @param tasks Task list to save.
     * @param storage Storage to save through.
     * @return A warning message to append to the response if saving failed, or an empty
     *         string if it succeeded.
     */
    protected String save(TaskList tasks, Storage storage) {
        try {
            storage.save(tasks.getTasks());
            return "";
        } catch (IOException e) {
            return "\nWarning: could not save tasks to disk (" + e.getMessage() + ")";
        }
    }

    /**
     * Checks that the given 0-based index refers to an existing task, throwing an error
     * worded the same way for every command that takes a task index.
     *
     * @param tasks Task list to check the index against.
     * @param index 0-based index to validate.
     * @throws VerityException If no task exists at that index.
     */
    protected void requireValidIndex(TaskList tasks, int index) throws VerityException {
        if (!tasks.isValidIndex(index)) {
            String taskWord = tasks.size() == 1 ? "task" : "tasks";
            throw new VerityException("There is no task " + (index + 1)
                    + ", you currently only have " + tasks.size() + " " + taskWord + ".");
        }
    }

    /**
     * Returns the given tasks numbered from 1, one per line.
     *
     * @param tasks Tasks to format.
     * @return Numbered, newline-separated listing of the tasks.
     */
    protected String formatNumberedList(List<Task> tasks) {
        return IntStream.range(0, tasks.size())
                .mapToObj(i -> (i + 1) + "." + tasks.get(i))
                .collect(Collectors.joining("\n"));
    }
}
