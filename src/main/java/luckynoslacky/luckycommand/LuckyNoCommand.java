package luckynoslacky.luckycommand;

import luckynoslacky.CommandResult;
import luckynoslacky.ResponseContent;
import luckynoslacky.ResponseTone;
import luckynoslacky.luckytask.TaskMaster;

/**
 * Represents a parsed chatbot command.
 */
public abstract class LuckyNoCommand {
    /**
     * Creates a command.
     */
    protected LuckyNoCommand() {
    }

    /**
     * Validates a task master dependency before storing it in a command.
     *
     * @param taskMaster task master required by the command
     * @return the non-null task master
     * @throws IllegalArgumentException if {@code taskMaster} is null
     */
    protected static TaskMaster requireTaskMaster(TaskMaster taskMaster) {
        if (taskMaster == null) {
            throw new IllegalArgumentException("Task master cannot be null.");
        }
        return taskMaster;
    }

    /**
     * Executes this command and returns its complete response.
     *
     * @return result produced by the command
     */
    public final CommandResult execute() {
        return new CommandResult(
                executeContent(), shouldExit(), responseTone());
    }

    /**
     * Executes the command-specific operation and returns its reply content.
     *
     * @return reply content produced by the command
     */
    protected abstract ResponseContent executeContent();

    /**
     * Returns the semantic tone for a successful execution of this command.
     *
     * @return response tone for the command's reply
     */
    protected abstract ResponseTone responseTone();

    /**
     * Indicates whether the chat loop should terminate after execution.
     *
     * @return true if the command requests termination
     */
    protected boolean shouldExit() {
        return false;
    }
}
