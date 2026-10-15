package duchess.parse;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Defines the commands that the Duchess is able to understand.
 * <p> Additionally, parses the input string to determine which command is being called.
 *
 */
public enum CommandType {
    LIST("list", false),
    MARK("mark", true),
    UNMARK("unmark", true),
    DELETE("delete", true),
    TODO("todo", true),
    DEADLINE("deadline", true),
    EVENT("event", true),
    RECURRING("recurring", true),
    FIND_EXACT("find -e", true),
    FIND("find", true),
    BYE("bye", false),
    UNKNOWN("", false);

    private final String command;
    private final boolean acceptsArguments;

    CommandType(String command, boolean acceptsArguments) {
        this.command = command;
        this.acceptsArguments = acceptsArguments;
    }

    /**
     * Parses the input string to determine which command is being called.
     * <p> Returns {@code UNKNOWN} if the command is not one of the predefined types.
     *
     * @param input The command entered by the user.
     * @return The CommandType corresponding to the command.
     */
    public static CommandType parse(String input) {
        if (input == null) {
            return UNKNOWN;
        }
        String commandLower = input.stripLeading().toLowerCase();

        for (CommandType commandType : values()) {
            if (commandType == UNKNOWN) {
                continue;
            }
            if (commandType.canRead(commandLower)) {
                return commandType;
            }
        }

        return UNKNOWN;
    }

    /**
     * Checks whether this command type can read the supplied input.
     *
     * @param input The command entered by the user.
     * @return {@code true} if this command type matches the input.
     */
    private boolean canRead(String input) {
        return acceptsArguments
                ? input.startsWith(command)
                : input.equals(command);
    }

    /**
     * Returns a comma-separated list of commands understood by the Duchess.
     *
     * @return The understood command keywords.
     */
    public static String getUnderstoodCommands() {
        return Arrays.stream(values())
                .filter(commandType -> commandType != UNKNOWN)
                .map(commandType -> commandType.command)
                .collect(Collectors.joining(", "));
    }

    /**
     * Checks whether the command is one of those which will change
     * the tasks being stored.
     *
     * @param commandType The type of command.
     * @return {@code true}, if the command will make changes to the tasks.
     *          <p> {@code false}, otherwise.
     */
    public static boolean isMutator(CommandType commandType) {
        assert (commandType != null) : "Command type cannot be null";

        return switch (commandType) {
            case MARK, UNMARK, DELETE,
                 TODO, DEADLINE, EVENT, RECURRING -> true;

            case LIST, FIND, FIND_EXACT, UNKNOWN, BYE -> false;
        };
    }
}
