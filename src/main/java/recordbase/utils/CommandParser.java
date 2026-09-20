package recordbase.utils;

import recordbase.exceptions.RecordException;
import recordbase.types.CommandType;
import recordbase.types.ParsedCommand;

/**
 * Parses raw user input input into a command type and its arguments.
 */
public final class CommandParser {
    private static final int MAX_COMMAND_LENGTH = 10_100;

    private CommandParser() {
        // Prevents instantiation of this utility class.
    }

    /**
     * Parses a line of user input.
     *
     * <p>Only the command word is normalized. The remaining arguments retain
     * their original capitalization for task descriptions.</p>
     *
     * @param input the raw user input
     * @return the parsed command
     * @throws RecordException if the input is null or blank
     */
    public static ParsedCommand parse(String input) {
        if (input == null || input.isBlank()) {
            throw new RecordException("Please enter a command.");
        }
        if (input.length() > MAX_COMMAND_LENGTH) {
            throw new RecordException("That command is too long. Keep it under "
                    + MAX_COMMAND_LENGTH + " characters.");
        }

        String trimmedInput = input.strip();
        int firstSpaceIndex = trimmedInput.indexOf(' ');

        if (firstSpaceIndex == -1) {
            return new ParsedCommand(CommandType.fromString(trimmedInput), "");
        }

        String commandWord = trimmedInput.substring(0, firstSpaceIndex);
        String arguments = trimmedInput.substring(firstSpaceIndex + 1).strip();

        return new ParsedCommand(CommandType.fromString(commandWord), arguments);
    }
}
