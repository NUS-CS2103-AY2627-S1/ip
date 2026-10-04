package kelore.parser;

import kelore.exception.KeloreInputException;

/** Interprets commands entered by the user. */
public class Parser {
    /** Creates a parser for Kelore commands. */
    public Parser() {
    }

    /**
     * Returns the command represented by the user's full input.
     *
     * @param input Complete user input to interpret.
     * @return Command invoked by the input.
     * @throws KeloreInputException If the input does not invoke a supported command.
     */
    public Command parseCommand(String input) throws KeloreInputException {
        String normalizedInput = normalizeInput(input);
        for (Command command : Command.values()) {
            if (command.matches(normalizedInput)) {
                return command;
            }
        }
        throw new KeloreInputException(
                "I don't recognise that command. Check the command and try again.");
    }

    /**
     * Removes insignificant whitespace around a complete command.
     *
     * @param input Complete user input to normalize.
     * @return Input without leading or trailing whitespace.
     * @throws KeloreInputException If the input is null or blank.
     */
    public String normalizeInput(String input) throws KeloreInputException {
        if (input == null || input.isBlank()) {
            throw new KeloreInputException("Please enter a command.");
        }
        return input.strip();
    }

    /**
     * Returns the one-based task number following a command.
     *
     * @param input Complete user input containing a task number.
     * @return Parsed one-based task number.
     * @throws KeloreInputException If the task number is not a valid integer.
     */
    public int parseTaskNumber(String input) throws KeloreInputException {
        String normalizedInput = normalizeInput(input);
        int argumentIndex = findFirstWhitespace(normalizedInput);
        try {
            if (argumentIndex < 0) {
                throw new NumberFormatException("Task number is missing");
            }
            return Integer.parseInt(normalizedInput.substring(argumentIndex).trim());
        } catch (NumberFormatException e) {
            throw new KeloreInputException(
                    "Please provide a valid task number after the command.");
        }
    }

    private int findFirstWhitespace(String input) {
        for (int i = 0; i < input.length(); i++) {
            if (Character.isWhitespace(input.charAt(i))) {
                return i;
            }
        }
        return -1;
    }
}
