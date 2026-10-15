package janet;

/**
 * Represents a handler for processing a command and its arguments.
 */
@FunctionalInterface
interface CommandHandler {

    /**
     * Processes the specified command arguments and returns the result.
     *
     * @param argsLine Arguments provided with the command.
     * @return Result of processing the command.
     * @throws JanetException If the command cannot be processed.
     */
    TaskList.CommandResult handle(String argsLine) throws JanetException;
}
