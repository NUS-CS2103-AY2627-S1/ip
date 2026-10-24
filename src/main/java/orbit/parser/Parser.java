package orbit.parser;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import orbit.exception.OrbitException;
import orbit.task.Deadline;
import orbit.task.Event;
import orbit.task.Todo;

/**
 * Converts raw user input into validated Orbit commands.
 */
public class Parser {
    /**
     * Creates a parser for Orbit's supported command syntax.
     */
    public Parser() {
    }

    /**
     * Parses one input line.
     *
     * @param rawInput input supplied by the user
     * @return validated command and its arguments
     * @throws OrbitException if the command or any argument is invalid
     */
    public ParsedCommand parse(String rawInput) throws OrbitException {
        String input = rawInput.trim();
        if (input.isEmpty()) {
            throw new OrbitException("Please enter a command.");
        }

        CommandType type = CommandType.fromInput(input);
        String arguments = input.substring(type.getKeyword().length()).trim();
        switch (type) {
            case BYE:
                // Fallthrough
            case LIST:
                // Fallthrough
            case SORT:
                requireNoArguments(arguments);
                return ParsedCommand.withoutArguments(type);
            case MARK:
                // Fallthrough
            case UNMARK:
                // Fallthrough
            case DELETE:
                return ParsedCommand.withTaskNumber(type, parseTaskNumber(arguments));
            case TODO:
                return ParsedCommand.withTask(type, parseTodo(arguments));
            case DEADLINE:
                return ParsedCommand.withTask(type, parseDeadline(arguments));
            case EVENT:
                return ParsedCommand.withTask(type, parseEvent(arguments));
            case FIND:
                return ParsedCommand.withKeyword(type, parseKeyword(arguments));
            default:
                throw new OrbitException("I don't know that command.");
        }
    }

    private String parseKeyword(String arguments) throws OrbitException {
        if (arguments.isEmpty()) {
            throw new OrbitException("The keyword for a find command cannot be empty.");
        }
        return arguments;
    }

    private void requireNoArguments(String arguments) throws OrbitException {
        if (!arguments.isEmpty()) {
            throw new OrbitException("I don't know that command.");
        }
    }

    private int parseTaskNumber(String arguments) throws OrbitException {
        if (arguments.isEmpty() || arguments.split("\\s+").length != 1) {
            throw new OrbitException("Please provide exactly one task number.");
        }
        try {
            return Integer.parseInt(arguments);
        } catch (NumberFormatException exception) {
            throw new OrbitException("Please provide a valid task number.");
        }
    }

    private Todo parseTodo(String arguments) throws OrbitException {
        if (arguments.isEmpty()) {
            throw new OrbitException("The description of a todo cannot be empty.");
        }
        return new Todo(arguments);
    }

    private Deadline parseDeadline(String arguments) throws OrbitException {
        String marker = "/by";
        int markerIndex = findOnlyMarker(arguments, marker);
        if (markerIndex < 0) {
            throw new OrbitException("Use: deadline <description> /by <yyyy-MM-dd>.");
        }

        String description = arguments.substring(0, markerIndex).trim();
        String byText = arguments.substring(markerIndex + marker.length()).trim();
        if (description.isEmpty()) {
            throw new OrbitException("The description of a deadline cannot be empty.");
        }
        if (byText.isEmpty()) {
            throw new OrbitException("The date of a deadline cannot be empty.");
        }
        return new Deadline(description, parseDate(byText));
    }

    private Event parseEvent(String arguments) throws OrbitException {
        String fromMarker = "/from";
        String toMarker = "/to";
        int fromIndex = findOnlyMarker(arguments, fromMarker);
        int toIndex = findOnlyMarker(arguments, toMarker);
        if (fromIndex < 0 || toIndex <= fromIndex) {
            throw new OrbitException("Use: event <description> /from <yyyy-MM-dd> /to <yyyy-MM-dd>.");
        }

        String description = arguments.substring(0, fromIndex).trim();
        String fromText = arguments.substring(fromIndex + fromMarker.length(), toIndex).trim();
        String toText = arguments.substring(toIndex + toMarker.length()).trim();
        if (description.isEmpty()) {
            throw new OrbitException("The description of an event cannot be empty.");
        }
        if (fromText.isEmpty()) {
            throw new OrbitException("The start of an event cannot be empty.");
        }
        if (toText.isEmpty()) {
            throw new OrbitException("The end of an event cannot be empty.");
        }
        LocalDate from = parseDate(fromText);
        LocalDate to = parseDate(toText);
        try {
            return new Event(description, from, to);
        } catch (IllegalArgumentException exception) {
            throw new OrbitException(exception.getMessage());
        }
    }

    private LocalDate parseDate(String value) throws OrbitException {
        try {
            return LocalDate.parse(value);
        } catch (DateTimeParseException exception) {
            throw new OrbitException("Please enter dates as yyyy-MM-dd.");
        }
    }

    private int findOnlyMarker(String text, String marker) {
        int markerIndex = -1;
        int searchIndex = 0;
        while (searchIndex < text.length()) {
            int candidateIndex = text.indexOf(marker, searchIndex);
            if (candidateIndex < 0) {
                break;
            }
            int candidateEnd = candidateIndex + marker.length();
            boolean hasLeftBoundary = candidateIndex == 0
                    || Character.isWhitespace(text.charAt(candidateIndex - 1));
            boolean hasRightBoundary = candidateEnd == text.length()
                    || Character.isWhitespace(text.charAt(candidateEnd));
            if (hasLeftBoundary && hasRightBoundary) {
                if (markerIndex >= 0) {
                    return -2;
                }
                markerIndex = candidateIndex;
            }
            searchIndex = candidateEnd;
        }
        return markerIndex;
    }
}
