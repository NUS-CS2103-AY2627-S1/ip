package woofer.parser;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

import woofer.exception.WooferException;
import woofer.task.Deadline;
import woofer.task.Event;
import woofer.task.Task;
import woofer.task.Todo;

/**
 * Interprets user commands and creates the corresponding task objects.
 */
public class Parser {
    private static final DateTimeFormatter INPUT_DATE_FORMAT = DateTimeFormatter.ISO_LOCAL_DATE;
    private static final int TODO_PREFIX_LENGTH = 5;
    private static final int DEADLINE_PREFIX_LENGTH = 9;
    private static final int EVENT_PREFIX_LENGTH = 6;
    private static final int DELETE_PREFIX_LENGTH = 7;
    private static final int FIND_PREFIX_LENGTH = 5;
    private static final int MARK_PREFIX_LENGTH = 5;
    private static final int UNMARK_PREFIX_LENGTH = 7;
    private static final String BY_MARKER = " /by ";
    private static final String FROM_MARKER = " /from ";
    private static final String TO_MARKER = " /to ";
    private static final String TODO_FORMAT = "todo <description>";
    private static final String DEADLINE_FORMAT = "deadline <description> /by <yyyy-MM-dd>";
    private static final String EVENT_FORMAT = "event <description> /from <yyyy-MM-dd>"
            + " /to <yyyy-MM-dd>";
    private static final String FIND_FORMAT = "find <keyword>";
    private static final String DELETE_FORMAT = "delete <number>";
    private static final String MARK_FORMAT = "mark <number>";
    private static final String UNMARK_FORMAT = "unmark <number>";
    private static final String UNDO_FORMAT = "undo";

    /**
     * Creates a parser for Woofer commands.
     */
    public Parser() {
    }

    /**
     * The command categories understood by Woofer.
     */
    public enum CommandType {
        /** Indicates that the user wants to exit Woofer. */
        EXIT,
        /** Indicates that the user wants to undo the latest task-changing command. */
        UNDO,
        /** Indicates that the user wants to view all tasks. */
        LIST,
        /** Indicates that the user wants to find tasks by keyword. */
        FIND,
        /** Indicates that the user wants to delete a task. */
        DELETE,
        /** Indicates that the user wants to mark a task as done. */
        MARK,
        /** Indicates that the user wants to mark a task as not done. */
        UNMARK,
        /** Indicates that the user wants to add a task. */
        ADD
    }

    /**
     * Identifies the type of a user command.
     *
     * @param command command entered by the user.
     * @return the command category.
     * @throws WooferException when the command is empty or has unexpected arguments.
     */
    public CommandType parseCommandType(String command) throws WooferException {
        command = normalizeCommand(command);
        String verb = command.split(" ", 2)[0];
        if (List.of("bye", "undo", "list").contains(verb) && !command.equals(verb)) {
            throw invalidFormat(verb);
        }
        if ("bye".equals(command)) {
            return CommandType.EXIT;
        }
        if ("undo".equals(command)) {
            return CommandType.UNDO;
        }
        if ("list".equals(command)) {
            return CommandType.LIST;
        }
        if ("find".equals(command) || command.startsWith("find ")) {
            return CommandType.FIND;
        }
        if ("delete".equals(command) || command.startsWith("delete ")) {
            return CommandType.DELETE;
        }
        if ("unmark".equals(command) || command.startsWith("unmark ")) {
            return CommandType.UNMARK;
        }
        if ("mark".equals(command) || command.startsWith("mark ")) {
            return CommandType.MARK;
        }
        return CommandType.ADD;
    }

    /**
     * Extracts the keyword from a find command.
     *
     * @param command command containing a search keyword.
     * @return the search keyword.
     * @throws WooferException when the command does not contain a keyword.
     */
    public String parseFindKeyword(String command) throws WooferException {
        command = normalizeCommand(command);
        String keyword = command.length() > FIND_PREFIX_LENGTH
                ? command.substring(FIND_PREFIX_LENGTH).trim()
                : "";
        if (keyword.isBlank()) {
            throw invalidFormat(FIND_FORMAT);
        }
        return keyword;
    }

    /**
     * Creates a typed task from a user command.
     *
     * @param command command entered by the user.
     * @return a typed task.
     * @throws WooferException when the command is unknown or malformed.
     */
    public Task parseTask(String command) throws WooferException {
        command = normalizeCommand(command);
        if (command.contains("|")) {
            throw new WooferException("Task descriptions cannot contain | because it separates saved fields.");
        }
        if ("todo".equals(command) || command.startsWith("todo ")) {
            String description = command.length() > TODO_PREFIX_LENGTH
                    ? command.substring(TODO_PREFIX_LENGTH).trim() : "";
            if (description.isBlank()) {
                throw invalidFormat(TODO_FORMAT);
            }
            return new Todo(description);
        }
        if (command.startsWith("todo")) {
            throw invalidFormat(TODO_FORMAT);
        }

        if ("deadline".equals(command) || command.startsWith("deadline ")) {
            String details = command.length() > DEADLINE_PREFIX_LENGTH
                    ? command.substring(DEADLINE_PREFIX_LENGTH).trim() : "";
            requireMarkers(details, List.of("/by"), DEADLINE_FORMAT);
            int byMarker = details.indexOf(BY_MARKER);
            if (byMarker < 0) {
                throw invalidFormat(DEADLINE_FORMAT);
            }
            String description = details.substring(0, byMarker).trim();
            String by = details.substring(byMarker + BY_MARKER.length()).trim();
            if (description.isBlank()) {
                throw invalidFormat(DEADLINE_FORMAT);
            }
            if (by.isBlank()) {
                throw invalidFormat(DEADLINE_FORMAT);
            }
            return new Deadline(description, parseDate(by, DEADLINE_FORMAT));
        }
        if (command.startsWith("deadline")) {
            throw invalidFormat(DEADLINE_FORMAT);
        }

        if ("event".equals(command) || command.startsWith("event ")) {
            String details = command.length() > EVENT_PREFIX_LENGTH
                    ? command.substring(EVENT_PREFIX_LENGTH).trim() : "";
            requireMarkers(details, List.of("/from", "/to"), EVENT_FORMAT);
            int fromMarker = details.indexOf(FROM_MARKER);
            int toMarker = details.indexOf(TO_MARKER, fromMarker + FROM_MARKER.length());
            if (fromMarker < 0 || toMarker < 0 || toMarker <= fromMarker) {
                throw invalidFormat(EVENT_FORMAT);
            }
            String description = details.substring(0, fromMarker).trim();
            String from = details.substring(fromMarker + FROM_MARKER.length(), toMarker).trim();
            String to = details.substring(toMarker + TO_MARKER.length()).trim();
            if (description.isBlank()) {
                throw invalidFormat(EVENT_FORMAT);
            }
            if (from.isBlank() || to.isBlank()) {
                throw invalidFormat(EVENT_FORMAT);
            }
            LocalDate start = parseDate(from, EVENT_FORMAT);
            LocalDate end = parseDate(to, EVENT_FORMAT);
            if (!end.isAfter(start)) {
                throw new WooferException("The event end date must be after its start date.");
            }
            return new Event(description, start, end);
        }
        if (command.startsWith("event")) {
            throw invalidFormat(EVENT_FORMAT);
        }

        throw invalidFormat(TODO_FORMAT, DEADLINE_FORMAT, EVENT_FORMAT, "list", FIND_FORMAT,
                MARK_FORMAT, UNMARK_FORMAT, DELETE_FORMAT, UNDO_FORMAT, "bye");
    }

    /**
     * Extracts the task number from a command.
     *
     * @param command command containing a task number.
     * @param commandType category of the command.
     * @return the one-based task number.
     * @throws WooferException when the command does not contain a valid task number.
     */
    public int parseTaskNumber(String command, CommandType commandType) throws WooferException {
        command = normalizeCommand(command);
        assert commandType != null : "Command type must not be null";
        int prefixLength = switch (commandType) {
            case DELETE -> DELETE_PREFIX_LENGTH;
            case MARK -> MARK_PREFIX_LENGTH;
            case UNMARK -> UNMARK_PREFIX_LENGTH;
            default -> throw new WooferException("That command does not contain a task number.");
        };

        String commandFormat = switch (commandType) {
            case DELETE -> DELETE_FORMAT;
            case MARK -> MARK_FORMAT;
            case UNMARK -> UNMARK_FORMAT;
            default -> "";
        };
        if (command.length() <= prefixLength) {
            throw invalidFormat(commandFormat);
        }

        try {
            String number = command.substring(prefixLength).trim();
            if (!number.matches("[0-9]+")) {
                throw invalidFormat(commandFormat);
            }
            int taskNumber = Integer.parseInt(number);
            if (taskNumber < 1) {
                throw new WooferException("Task numbers start at 1. Use list to see your tasks.");
            }
            return taskNumber;
        } catch (NumberFormatException exception) {
            throw invalidFormat(commandFormat);
        }
    }

    /**
     * Parses a date entered in ISO local-date format.
     *
     * @param date date entered by the user.
     * @param commandFormat complete command format to show when parsing fails.
     * @return the parsed date.
     * @throws WooferException when the date does not use the yyyy-MM-dd format.
     */
    private LocalDate parseDate(String date, String commandFormat) throws WooferException {
        try {
            return LocalDate.parse(date, INPUT_DATE_FORMAT);
        } catch (DateTimeParseException exception) {
            throw invalidFormat(commandFormat);
        }
    }

    /**
     * Normalizes spaces and tabs consistently for both interfaces and rejects multiline input.
     *
     * @param command raw command.
     * @return a trimmed command with single spaces.
     * @throws WooferException when input is missing or contains unsupported control characters.
     */
    public String normalizeCommand(String command) throws WooferException {
        if (command == null || command.isBlank()) {
            throw new WooferException("Give me a command, buddy! Try list or todo walk the dog.");
        }
        if (command.chars().anyMatch(character -> Character.isISOControl(character) && character != '\t')) {
            throw new WooferException("Please enter one command on a single line without control characters.");
        }
        return command.strip().replaceAll("[ \t]+", " ");
    }

    /**
     * Requires exactly the expected date markers in order, including markers with missing values.
     *
     * @param details task description and date parameters.
     * @param expected expected marker sequence.
     * @param format command syntax to report on failure.
     * @throws WooferException when a date marker is repeated, misplaced, or unexpected.
     */
    private void requireMarkers(String details, List<String> expected, String format) throws WooferException {
        List<String> markers = List.of(details.split(" ")).stream()
                .filter(token -> List.of("/by", "/from", "/to").contains(token))
                .toList();
        if (!markers.equals(expected)) {
            throw invalidFormat(format);
        }
    }

    /**
     * Creates a parser error that states the complete required command format.
     *
     * @param commandFormats one or more accepted command formats.
     * @return parser error.
     */
    private WooferException invalidFormat(String... commandFormats) {
        return new WooferException("Required format: " + String.join(" or ", commandFormats) + ".");
    }
}
