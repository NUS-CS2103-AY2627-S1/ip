package benji;

/**
 * Parses user input and identifies the corresponding command.
 */
public class Parser {

    /**
     * Checks whether input consists of a command keyword with an optional argument.
     *
     * @param input upper-case user input
     * @param command upper-case command keyword
     * @return true if the input is the command or starts with the command and a space
     */
    private static boolean matchesCommand(String input, String command) {
        return input.equals(command) || input.startsWith(command + " ");
    }

    /**
     * Converts a user command into the corresponding {@link Command}.
     *
     * @param input the command entered by the user
     * @return command represented by the input
     */
    public static Command getCommand(String input) {
        String upperInput = input.toUpperCase();
        if (upperInput.equals("BYE")) {
            return Command.BYE;
        } else if (upperInput.equals("LIST")) {
            return Command.LIST;
        } else if (matchesCommand(upperInput, "MARK")) {
            return Command.MARK;
        } else if (matchesCommand(upperInput, "UNMARK")) {
            return Command.UNMARK;
        } else if (matchesCommand(upperInput, "TODO")) {
            return Command.TODO;
        } else if (matchesCommand(upperInput, "DEADLINE")) {
            return Command.DEADLINE;
        } else if (matchesCommand(upperInput, "EVENT")) {
            return Command.EVENT;
        } else if (matchesCommand(upperInput, "DELETE")) {
            return Command.DELETE;
        } else if (matchesCommand(upperInput, "FIND")) {
            return Command.FIND;
        } else if (upperInput.equals("HELP")) {
            return Command.HELP;
        } else {
            return Command.UNKNOWN;
        }
    }
}
