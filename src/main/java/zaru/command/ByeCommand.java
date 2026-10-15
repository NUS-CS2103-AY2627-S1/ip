package zaru.command;

import zaru.task.TaskList;

/** Executes the {@code bye} command. */
public class ByeCommand extends Command {
    /**
     * Returns the command word represented by this command.
     *
     * @return The {@code bye} command word.
     */
    @Override
    public String getCommand() {
        return "bye";
    }

    /** Returns {@code false} because the {@code bye} command takes no additional text. */
    @Override
    public boolean acceptsPositionalArgument() {
        return false;
    }

    /**
     * Returns the goodbye message.
     *
     * @param tasks Current task list, unused by this command.
     * @return Goodbye response message.
     */
    @Override
    public String execute(TaskList tasks) {
        return "Bye! I'll be curled up here when you need me.";
    }

    /** Returns {@code true} to indicate that this command is an exit command. */
    @Override
    public boolean isExit() {
        return true;
    }
}
