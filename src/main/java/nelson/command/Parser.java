package nelson.command;

import nelson.exception.NelsonException;

/** Converts raw user input into structured Nelson commands. */
public class Parser {
    /** Supported command kinds. */
    public enum Type {

        LIST,

        TODO,

        DEADLINE,

        EVENT,

        MARK,

        UNMARK,

        DELETE,

        SORT,

        FIND

    }

    /** A parsed command and its arguments. */
    public static class Command {
        private final Type type;
        private final String[] arguments;

        protected Command(Type type, String... arguments) {
            assert type != null : "Command type must not be null";
            assert arguments != null : "Command arguments must not be null";
            this.type = type;
            this.arguments = arguments;
        }

        /**
         * Returns the command type.
         * @return the parsed command type
         */
        public Type getType() {
            return type;
        }

        /**
         * Returns an extracted command argument.
         * @param index zero-based argument index
         * @return the argument at {@code index}
         */
        public String getArgument(int index) {
            return arguments[index];
        }
    }

    /**
     * Parses one user command and validates its required notation.
     * @param command raw command entered by the user
     * @return the structured command
     * @throws NelsonException if the command or its arguments are invalid
     */
    public Command parse(String command) throws NelsonException {
        String normalizedCommand = command.trim();
        if (normalizedCommand.equals("list")) {
            return new Command(Type.LIST);
        } else if (normalizedCommand.equals("sort")) {
            return new Command(Type.SORT);
        } else if (normalizedCommand.equals("todo") || normalizedCommand.startsWith("todo ")) {
            String description = normalizedCommand.substring(4).trim();
            if (description.isEmpty()) {
                throw emptyMove();
            }
            return new Command(Type.TODO, description);
        } else if (normalizedCommand.equals("deadline") || normalizedCommand.startsWith("deadline ")) {
            return parseDeadline(normalizedCommand);
        } else if (normalizedCommand.equals("event") || normalizedCommand.startsWith("event ")) {
            return parseEvent(normalizedCommand);
        } else if (normalizedCommand.equals("mark") || normalizedCommand.startsWith("mark ")) {
            return new Command(Type.MARK, normalizedCommand.substring(4).trim());
        } else if (normalizedCommand.equals("unmark") || normalizedCommand.startsWith("unmark ")) {
            return new Command(Type.UNMARK, normalizedCommand.substring(6).trim());
        } else if (normalizedCommand.equals("delete") || normalizedCommand.startsWith("delete ")) {
            return new Command(Type.DELETE, normalizedCommand.substring(6).trim());
        } else if (normalizedCommand.equals("find") || normalizedCommand.startsWith("find ")) {
            return parseFind(normalizedCommand);
        }
        throw new NelsonException("Molo! I don't know what that means. Are you even playing the same game?");
    }

    /**
     * Returns a find command containing the requested keyword.
     *
     * @param command raw find command.
     * @return parsed find command.
     * @throws NelsonException if the keyword is missing.
     */
    private FindCommand parseFind(String command) throws NelsonException {
        String keyword = command.substring(4).trim();
        if (keyword.isEmpty()) {
            throw new NelsonException("Molo! An empty search? You must provide a keyword, you amateur.");
        }
        return new FindCommand(keyword);
    }

    /** Parses a deadline command into its description and date arguments. */
    private Command parseDeadline(String command) throws NelsonException {
        String details = command.substring(8).trim();
        int byIndex = details.indexOf("/by");
        if (details.isEmpty()) {
            throw emptyMove();
        }
        if (byIndex == -1 || byIndex != details.lastIndexOf("/by")) {
            throw invalidTimeParameters();
        }
        String description = details.substring(0, byIndex).trim();
        String by = details.substring(byIndex + 3).trim();
        if (description.isEmpty() || by.isEmpty()) {
            throw invalidTimeParameters();
        }
        return new Command(Type.DEADLINE, description, by);
    }

    /** Parses an event command into its description and date arguments. */
    private Command parseEvent(String command) throws NelsonException {
        String details = command.substring(5).trim();
        int fromIndex = details.indexOf("/from");
        int toIndex = details.indexOf("/to");
        if (details.isEmpty()) {
            throw emptyMove();
        }
        boolean hasDuplicateParameter = fromIndex != details.lastIndexOf("/from")
                || toIndex != details.lastIndexOf("/to");
        if (fromIndex == -1 || toIndex == -1 || fromIndex > toIndex || hasDuplicateParameter) {
            throw invalidTimeParameters();
        }
        String description = details.substring(0, fromIndex).trim();
        String from = details.substring(fromIndex + 5, toIndex).trim();
        String to = details.substring(toIndex + 3).trim();
        if (description.isEmpty() || from.isEmpty() || to.isEmpty()) {
            throw invalidTimeParameters();
        }
        return new Command(Type.EVENT, description, from, to);
    }

    /** Creates the standard error for a command without a description. */
    private NelsonException emptyMove() {
        return new NelsonException("Molo! An empty move? You must provide a description, you amateur.");
    }

    /** Creates the standard error for missing deadline or event parameters. */
    private NelsonException invalidTimeParameters() {
        return new NelsonException("Molo! Invalid notation! You are missing the required time parameters.");
    }
}
