package panda.parser.commandparser;

import panda.exception.parser.InvalidArgumentException;

/**
 * Parses the task numbers accepted by task-modification commands.
 */
final class TaskNumberParser {
    private TaskNumberParser() {
        // Utility class: prevent instantiation.
    }

    /**
     * Parses a digit-only task number within the integer range.
     *
     * @param arguments Task-number text supplied by the user.
     * @param commandKeyword Command whose argument is being parsed.
     * @return Parsed task number.
     * @throws InvalidArgumentException If the input is empty, non-numeric, or exceeds the integer range.
     */
    static int parse(String arguments, String commandKeyword) throws InvalidArgumentException {
        String usage = commandKeyword + " TASK_NUMBER";
        if (!arguments.matches("\\d+")) {
            throw new InvalidArgumentException(commandKeyword, usage);
        }

        try {
            return Integer.parseInt(arguments);
        } catch (NumberFormatException exception) {
            throw new InvalidArgumentException(commandKeyword, usage);
        }
    }
}
