package zaru.command;

import zaru.exception.ZaruException;
import zaru.task.TaskList;

/** Executes the {@code sort} command. */
public class SortCommand extends Command {
    /**
     * Returns the command word represented by this command.
     *
     * @return The {@code sort} command word.
     */
    @Override
    public String getCommand() {
        return "sort";
    }

    /** Returns {@code false} because the {@code sort} command takes no additional text. */
    @Override
    public boolean acceptsPositionalArgument() {
        return false;
    }

    /**
     * Sorts all tasks by type and chronological order.
     *
     * @param tasks Current task list.
     * @return Sorted task-list response message.
     * @throws ZaruException If the sorted task list cannot be saved.
     */
    @Override
    public String execute(TaskList tasks) throws ZaruException {
        tasks.sort();

        if (tasks.size() == 0) {
            return "You have no tasks to sort!";
        }

        return "Here are your sorted tasks:\n%s".formatted(tasks);
    }
}
