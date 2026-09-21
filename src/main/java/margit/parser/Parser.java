package margit.parser;

/** Interprets a raw user command as a command type and its argument. */
public class Parser {
    private static final String BYE_COMMAND = "bye";
    private static final String LIST_COMMAND = "list";
    private static final String FIND_COMMAND = "find";
    private static final String ON_COMMAND = "on";
    private static final String MARK_COMMAND = "mark";
    private static final String UNMARK_COMMAND = "unmark";
    private static final String DELETE_COMMAND = "delete";
    private static final String TODO_COMMAND = "todo";
    private static final String DEADLINE_COMMAND = "deadline";
    private static final String EVENT_COMMAND = "event";
    private static final String SORT_COMMAND = "sort";
    private static final String BY_MARKER = "/by";
    private static final String FROM_MARKER = "/from";
    private static final String TO_MARKER = "/to";
    private static final String COMMAND_SEPARATOR = " ";
    private static final int ONE_BASED_INDEX_OFFSET = 1;

    /** Recognized commands accepted by the application. */
    public enum CommandType {
        BYE, LIST, FIND, ON, MARK, UNMARK, DELETE, TODO, DEADLINE, EVENT, SORT, UNKNOWN
    }

    /** Parsed result containing the command type and its remaining argument. */
    public static class Command {
        public final CommandType type;
        public final String argument;
        /** Zero-based task index for indexed commands, or {@code null} when it is invalid or irrelevant. */
        public final Integer taskIndex;

        private Command(CommandType type, String argument) {
            this(type, argument, null);
        }

        private Command(CommandType type, String argument, Integer taskIndex) {
            this.type = type;
            this.argument = argument;
            this.taskIndex = taskIndex;
        }
    }

    /** Parsed description from a todo command. */
    public static class TodoArguments {
        public final boolean isValid;
        public final String description;

        private TodoArguments(boolean isValid, String description) {
            this.isValid = isValid;
            this.description = description;
        }
    }

    /** Validation outcomes for a deadline command's required components. */
    public enum DeadlineStatus {
        VALID, MISSING_SEPARATOR, MISSING_COMPONENT
    }

    /** Parsed description and date text from a deadline command. */
    public static class DeadlineArguments {
        public final DeadlineStatus status;
        public final String description;
        public final String byRaw;

        private DeadlineArguments(DeadlineStatus status, String description, String byRaw) {
            this.status = status;
            this.description = description;
            this.byRaw = byRaw;
        }
    }

    /** Parsed description and time text from an event command. */
    public static class EventArguments {
        public final boolean isValid;
        public final String description;
        public final String fromRaw;
        public final String toRaw;

        private EventArguments(boolean isValid, String description, String fromRaw, String toRaw) {
            this.isValid = isValid;
            this.description = description;
            this.fromRaw = fromRaw;
            this.toRaw = toRaw;
        }
    }

    /** Parses an input line without validating command-specific arguments. */
    public static Command parse(String input) {
        if (input.equals(BYE_COMMAND)) {
            return new Command(CommandType.BYE, "");
        }
        if (input.equals(LIST_COMMAND)) {
            return new Command(CommandType.LIST, "");
        }
        if (hasOptionalArgument(input, FIND_COMMAND)) {
            return commandWithTrimmedArgument(CommandType.FIND, input, FIND_COMMAND);
        }
        if (hasOptionalArgument(input, ON_COMMAND)) {
            return commandWithTrimmedArgument(CommandType.ON, input, ON_COMMAND);
        }
        if (input.startsWith(MARK_COMMAND + COMMAND_SEPARATOR)) {
            return indexCommand(CommandType.MARK, input.substring(MARK_COMMAND.length()));
        }
        if (input.startsWith(UNMARK_COMMAND + COMMAND_SEPARATOR)) {
            return indexCommand(CommandType.UNMARK, input.substring(UNMARK_COMMAND.length()));
        }
        if (input.startsWith(DELETE_COMMAND + COMMAND_SEPARATOR)) {
            return indexCommand(CommandType.DELETE, input.substring(DELETE_COMMAND.length()));
        }
        if (hasOptionalArgument(input, TODO_COMMAND)) {
            return commandWithTrimmedArgument(CommandType.TODO, input, TODO_COMMAND);
        }
        if (hasOptionalArgument(input, DEADLINE_COMMAND)) {
            return commandWithTrimmedArgument(CommandType.DEADLINE, input, DEADLINE_COMMAND);
        }
        if (hasOptionalArgument(input, EVENT_COMMAND)) {
            return commandWithTrimmedArgument(CommandType.EVENT, input, EVENT_COMMAND);
        }
        if (hasOptionalArgument(input, SORT_COMMAND)) {
            return commandWithTrimmedArgument(CommandType.SORT, input, SORT_COMMAND);
        }
        return new Command(CommandType.UNKNOWN, "");
    }

    /** Parses and validates the required description of a todo command. */
    public static TodoArguments parseTodo(String argument) {
        return new TodoArguments(!argument.isEmpty(), argument);
    }

    /** Parses the description and required {@code /by} value of a deadline command. */
    public static DeadlineArguments parseDeadline(String argument) {
        int byIndex = argument.indexOf(BY_MARKER);
        if (argument.isEmpty() || byIndex == -1) {
            return new DeadlineArguments(DeadlineStatus.MISSING_SEPARATOR, "", "");
        }
        String description = argument.substring(0, byIndex).trim();
        String byRaw = argument.substring(byIndex + BY_MARKER.length()).trim();
        if (description.isEmpty() || byRaw.isEmpty()) {
            return new DeadlineArguments(DeadlineStatus.MISSING_COMPONENT, description, byRaw);
        }
        return new DeadlineArguments(DeadlineStatus.VALID, description, byRaw);
    }

    /** Parses the description and required {@code /from} and {@code /to} values of an event command. */
    public static EventArguments parseEvent(String argument) {
        int fromIndex = argument.indexOf(FROM_MARKER);
        int toIndex = argument.indexOf(TO_MARKER);
        if (argument.isEmpty() || fromIndex == -1 || toIndex == -1 || toIndex < fromIndex) {
            return new EventArguments(false, "", "", "");
        }
        String description = argument.substring(0, fromIndex).trim();
        String fromRaw = argument.substring(fromIndex + FROM_MARKER.length(), toIndex).trim();
        String toRaw = argument.substring(toIndex + TO_MARKER.length()).trim();
        boolean isValid = !description.isEmpty() && !fromRaw.isEmpty() && !toRaw.isEmpty();
        return new EventArguments(isValid, description, fromRaw, toRaw);
    }

    /** Returns whether an input consists of a command keyword with an optional argument. */
    private static boolean hasOptionalArgument(String input, String commandKeyword) {
        return input.equals(commandKeyword) || input.startsWith(commandKeyword + COMMAND_SEPARATOR);
    }

    /** Extracts and trims the text following a command keyword. */
    private static Command commandWithTrimmedArgument(CommandType type, String input, String commandKeyword) {
        String argument = input.length() > commandKeyword.length()
                ? input.substring(commandKeyword.length()).trim() : "";
        return new Command(type, argument);
    }

    /** Parses the one-based task number supplied to an indexed command. */
    private static Command indexCommand(CommandType type, String argument) {
        try {
            return new Command(type, argument, Integer.parseInt(argument.trim()) - ONE_BASED_INDEX_OFFSET);
        } catch (NumberFormatException e) {
            return new Command(type, argument, null);
        }
    }
}
