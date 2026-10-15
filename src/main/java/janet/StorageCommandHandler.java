package janet;

/**
 * Represents a handler for processing a command and its arguments.
 */
@FunctionalInterface
interface StorageCommandHandler {

    /**
     * Processes the specified command arguments and returns the result.
     *
     * @param taskField <code>TaskField</code> params provided with the command.
     * @return Result of processing the command.
     * @throws JanetFileException If the command cannot be processed due to malformed storage format.
     */
    StorageTaskParser handle(StorageTaskParser.TaskField taskField) throws JanetFileException;
}
