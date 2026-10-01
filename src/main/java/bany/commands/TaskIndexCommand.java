package bany.commands;

import java.util.Map;
import java.util.Optional;

import bany.TaskStorage;
import bany.gui.Responder;
import bany.tasks.Task;
import bany.utilities.Helper;

/**
 * Base class for commands that operate on a numbered task.
 */
public abstract class TaskIndexCommand extends Command {
    /** Parsed command values, including the user-provided task number. */
    protected final Map<String, String> values;

    /** Result of validating a task index supplied to an action command. */
    public enum CheckTaskResult {
        /** Index is missing or negative. */
        INVALID_INDEX,
        /** Index is beyond the last task in the list. */
        INDEX_OUT_OF_BOUND,
        /** Index identifies an existing task. */
        SUCCESS,
    }

    /**
     * Creates a task-index command from parsed command values.
     *
     * @param values values extracted from the user's input.
     */
    protected TaskIndexCommand(Map<String, String> values) {
        this.values = values;
    }

    /**
     * Converts the one-based number typed by the user into a zero-based index.
     *
     * @return parsed integer minus one, or null if the input is missing or is not a 32-bit integer;
     *         callers must separately check whether the result identifies an existing task.
     */
    protected Integer getTaskIndex() {
        String description = values.get("description");
        if (description == null || description.isBlank()
                || !Helper.isInteger(description)) {
            return null;
        }
        return Integer.parseInt(description) - 1;
    }

    /**
     * Checks whether a zero-based index identifies an existing task.
     *
     * @param taskIndex index to validate, or null if parsing failed.
     * @param responder response builder supplied by the caller; currently unused.
     * @param tasks task storage against which to check the index.
     * @param s caller-supplied action label; currently unused.
     * @return whether the index is invalid, beyond the list, or valid.
     */
    protected CheckTaskResult checkTaskIndex(
            Integer taskIndex,
            Responder responder,
            TaskStorage tasks, String s) {

        if (taskIndex == null || taskIndex < 0) {
            return CheckTaskResult.INVALID_INDEX;
        }

        Optional<Task> task = tasks.getTask(taskIndex);

        if (task.isEmpty()) {
            return CheckTaskResult.INDEX_OUT_OF_BOUND;
        }

        return CheckTaskResult.SUCCESS;
    }

}
