package zaru.command;

import zaru.task.TaskList;

/** Executes the {@code list} command. */
public class ListCommand extends Command {
    /**
     * Returns the command word represented by this command.
     *
     * @return The {@code list} command word.
     */
    @Override
    public String getCommand() {
        return "list";
    }

    /** Returns {@code false} because the {@code list} command takes no additional text. */
    @Override
    public boolean acceptsPositionalArgument() {
        return false;
    }

    /**
     * Returns either the empty-list message or all current tasks.
     *
     * @param tasks Current task list.
     * @return Task-list response message.
     */
    @Override
    public String execute(TaskList tasks) {
        if (tasks.size() == 0) {
            return "Your task list is empty. Nothing to pounce on yet!";
        }

        return "Here are your tasks:\n%s".formatted(tasks);
    }
}
