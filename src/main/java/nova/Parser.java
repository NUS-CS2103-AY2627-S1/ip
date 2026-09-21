package nova;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;

/**
 * Parses user input into commands understood by Nova.
 */
public class Parser {

    private static final DateTimeFormatter INPUT_DATE_FORMAT =
            DateTimeFormatter.ofPattern("uuuu-MM-dd HHmm")
                    .withResolverStyle(ResolverStyle.STRICT);

    /**
     * Represents the command types supported by Nova.
     */
    public enum CommandType {
        BYE,
        LIST,
        MARK,
        DELETE,
        UPDATE,
        TODO,
        DEADLINE,
        EVENT,
        FIND,
        UNKNOWN
    }

    private Parser() {
    }

    /**
     * Parses user input into a structured command.
     *
     * @param input User input.
     * @return Parsed command.
     * @throws IllegalArgumentException If the command arguments are invalid.
     */
    public static ParsedCommand parse(String input) {
        if (input == null || input.isBlank()) {
            return new ParsedCommand(CommandType.UNKNOWN);
        }

        String[] commandParts = input.strip().split("\\s+", 2);
        String commandWord = commandParts[0];
        String arguments = commandParts.length == 2 ? commandParts[1].strip() : "";

        switch (commandWord) {
            case "bye":
                ensureNoArguments(arguments, "Please use: bye");
                return new ParsedCommand(CommandType.BYE);
            case "list":
                ensureNoArguments(arguments, "Please use: list");
                return new ParsedCommand(CommandType.LIST);
            case "mark":
                return ParsedCommand.withTaskNumber(
                        CommandType.MARK,
                        parseTaskNumber(requireArguments(arguments, "Please use: mark NUMBER")));
            case "delete":
                return ParsedCommand.withTaskNumber(
                        CommandType.DELETE,
                        parseTaskNumber(requireArguments(arguments, "Please use: delete NUMBER")));
            case "update":
                return parseUpdate(requireArguments(
                        arguments, "Please use: update NUMBER DESCRIPTION"));
            case "todo":
                return ParsedCommand.withDescription(
                        CommandType.TODO,
                        parseDescription(arguments, "Please provide a description for the todo."));
            case "deadline":
                return parseDeadline(requireArguments(
                        arguments,
                        "Please use: deadline DESCRIPTION /by yyyy-MM-dd HHmm"));
            case "event":
                return parseEvent(requireArguments(
                        arguments, "Please provide both /from and /to."));
            case "find":
                return ParsedCommand.withDescription(
                        CommandType.FIND,
                        parseDescription(arguments, "Please provide a keyword to find."));
            default:
                return new ParsedCommand(CommandType.UNKNOWN);
        }
    }

    private static ParsedCommand parseUpdate(String content) {
        String[] parts = content.strip().split("\\s+", 2);
        if (parts.length != 2) {
            throw new IllegalArgumentException("Please use: update NUMBER DESCRIPTION");
        }
        return ParsedCommand.withUpdate(
                parseTaskNumber(parts[0]),
                parseSingleLineText(
                        parts[1], "The description cannot contain tabs or line breaks."));
    }

    private static ParsedCommand parseDeadline(String content) {
        String[] parts = content.split("\\s+/by\\s+", -1);

        if (parts.length != 2 || parts[0].isBlank() || parts[1].isBlank()) {
            throw new IllegalArgumentException(
                    "Please use: deadline DESCRIPTION /by yyyy-MM-dd HHmm");
        }

        try {
            LocalDateTime deadline = LocalDateTime.parse(
                    parts[1].strip(),
                    INPUT_DATE_FORMAT);

            return ParsedCommand.withDeadline(
                    parseDescription(
                            parts[0], "Please provide a description for the deadline."),
                    deadline);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException(
                    "Please enter the deadline as yyyy-MM-dd HHmm.");
        }
    }

    private static ParsedCommand parseEvent(String content) {
        String[] fromParts = content.split("\\s+/from\\s+", -1);

        if (fromParts.length != 2) {
            throw new IllegalArgumentException(
                    "Please provide both /from and /to.");
        }

        String[] toParts = fromParts[1].split("\\s+/to\\s+", -1);

        if (toParts.length != 2
                || fromParts[0].isBlank()
                || toParts[0].isBlank()
                || toParts[1].isBlank()) {
            throw new IllegalArgumentException(
                    "Please provide both /from and /to.");
        }

        return ParsedCommand.withEvent(
                parseDescription(
                        fromParts[0], "Please provide a description for the event."),
                parseSingleLineText(toParts[0], "Event times must be on one line."),
                parseSingleLineText(toParts[1], "Event times must be on one line."));
    }

    private static int parseTaskNumber(String numberText) {
        try {
            int taskNumber = Integer.parseInt(numberText.strip());
            if (taskNumber < 1) {
                throw new NumberFormatException();
            }
            return taskNumber;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    "That task number does not exist.");
        }
    }

    private static String requireArguments(String arguments, String message) {
        if (arguments.isBlank()) {
            throw new IllegalArgumentException(message);
        }
        return arguments;
    }

    private static void ensureNoArguments(String arguments, String message) {
        if (!arguments.isEmpty()) {
            throw new IllegalArgumentException(message);
        }
    }

    private static String parseDescription(String description, String blankMessage) {
        if (description.isBlank()) {
            throw new IllegalArgumentException(blankMessage);
        }
        return parseSingleLineText(
                description, "Descriptions cannot contain tabs or line breaks.");
    }

    private static String parseSingleLineText(String text, String message) {
        if (text.contains("\t") || text.contains("\n") || text.contains("\r")) {
            throw new IllegalArgumentException(message);
        }
        return text.strip();
    }

    /**
     * Stores information extracted from a parsed command.
     */
    public static class ParsedCommand {

        private final CommandType type;
        private int taskNumber;
        private String description;
        private LocalDateTime deadline;
        private String from;
        private String to;

        /**
         * Creates a parsed command of the specified type.
         *
         * @param type Command type.
         */
        public ParsedCommand(CommandType type) {
            this.type = type;
        }

        /**
         * Creates a command containing a task number.
         *
         * @param type Command type.
         * @param taskNumber Task number.
         * @return Parsed command.
         */
        public static ParsedCommand withTaskNumber(
                CommandType type, int taskNumber) {
            ParsedCommand command = new ParsedCommand(type);
            command.taskNumber = taskNumber;
            return command;
        }

        /**
         * Creates a command containing a description.
         *
         * @param type Command type.
         * @param description Task description.
         * @return Parsed command.
         */
        public static ParsedCommand withDescription(
                CommandType type, String description) {
            ParsedCommand command = new ParsedCommand(type);
            command.description = description;
            return command;
        }

        /**
         * Creates a deadline command.
         *
         * @param description Task description.
         * @param deadline Deadline date and time.
         * @return Parsed command.
         */
        public static ParsedCommand withDeadline(
                String description, LocalDateTime deadline) {
            ParsedCommand command = new ParsedCommand(CommandType.DEADLINE);
            command.description = description;
            command.deadline = deadline;
            return command;
        }

        /**
         * Creates an event command.
         *
         * @param description Event description.
         * @param from Event start.
         * @param to Event end.
         * @return Parsed command.
         */
        public static ParsedCommand withEvent(
                String description, String from, String to) {
            ParsedCommand command = new ParsedCommand(CommandType.EVENT);
            command.description = description;
            command.from = from;
            command.to = to;
            return command;
        }

        /**
         * Creates a command to update a task description.
         *
         * @param taskNumber One-based task number.
         * @param description Replacement description.
         * @return Parsed update command.
         */
        public static ParsedCommand withUpdate(int taskNumber, String description) {
            ParsedCommand command = new ParsedCommand(CommandType.UPDATE);
            command.taskNumber = taskNumber;
            command.description = description;
            return command;
        }

        public CommandType getType() {
            return type;
        }

        public int getTaskNumber() {
            return taskNumber;
        }

        public String getDescription() {
            return description;
        }

        public LocalDateTime getDeadline() {
            return deadline;
        }

        public String getFrom() {
            return from;
        }

        public String getTo() {
            return to;
        }
    }
}
