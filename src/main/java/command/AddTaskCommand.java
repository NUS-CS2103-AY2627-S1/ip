package command;

import exception.CbtException;
import task.Task;
import task.TaskList;

/** Provides behavior shared by commands that add a task. */
abstract class AddTaskCommand implements Command {
    private static final int MAXIMUM_DESCRIPTION_LENGTH = 300;

    /**
     * Adds a task and returns the standard result for a successful addition.
     *
     * @param tasks task list to update.
     * @param task task to add.
     * @return result describing the added task.
     */
    protected CommandResult addTask(TaskList tasks, Task task) throws CbtException {
        validateDescription(task.getDescription());
        tasks.addTaskIfUnique(task);
        String message = "Task locked into orbit:\n"
                + "  " + task + "\n"
                + "Your flight plan now has " + tasks.getSize() + " task"
                + (tasks.getSize() == 1 ? "" : "s") + ".";
        return new CommandResult(message, true, false);
    }

    /** Rejects descriptions that cannot be displayed or stored safely. */
    private void validateDescription(String description) throws CbtException {
        if (description.isBlank()) {
            throw new CbtException("A task description cannot be empty.");
        }
        if (description.contains("|")) {
            throw new CbtException("A task description cannot contain the reserved '|' character.");
        }
        if (description.length() > MAXIMUM_DESCRIPTION_LENGTH) {
            throw new CbtException("A task description cannot exceed "
                    + MAXIMUM_DESCRIPTION_LENGTH + " characters.");
        }
    }
}
