package jelly.command;

/** Converts raw user input into a recognized Jelly command type. */
public class Parser {
    /**
     * Identifies the command represented by an input line.
     *
     * @param command the raw command entered by the user.
     * @return the recognized command type, or {@link CommandType#INVALID} if it is not recognized.
     */
    public CommandType parse(String command) {
        if (command == null || command.isBlank() || !command.equals(command.trim())
                || command.contains("  ")) {
            return CommandType.INVALID;
        }
        if (command.equals("bye")) {
            return CommandType.BYE;
        } else if (command.equals("list")) {
            return CommandType.LIST;
        } else if (matchesCommand(command, "todo")) {
            return CommandType.TODO;
        } else if (matchesCommand(command, "deadline")) {
            return CommandType.DEADLINE;
        } else if (matchesCommand(command, "event")) {
            return CommandType.EVENT;
        } else if (matchesCommand(command, "mark")) {
            return CommandType.MARK;
        } else if (matchesCommand(command, "unmark")) {
            return CommandType.UNMARK;
        } else if (matchesCommand(command, "delete")) {
            return CommandType.DELETE;
        } else if (matchesCommand(command, "find")) {
            return CommandType.FIND;
        } else if (matchesCommand(command, "tag")) {
            return CommandType.TAG;
        } else if (matchesCommand(command, "untag")) {
            return CommandType.UNTAG;
        } else if (command.equals("help")) {
            return CommandType.HELP;
        }
        return CommandType.INVALID;
    }

    /** Returns whether input is a command by itself or followed by an argument. */
    private static boolean matchesCommand(String input, String command) {
        return input.equals(command) || input.startsWith(command + " ");
    }
}
