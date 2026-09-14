package arrodes.parser;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;

import arrodes.command.ByeCommand;
import arrodes.command.Command;
import arrodes.command.DeadlineCommand;
import arrodes.command.DeleteCommand;
import arrodes.command.EventCommand;
import arrodes.command.FindCommand;
import arrodes.command.ListCommand;
import arrodes.command.MarkCommand;
import arrodes.command.TagCommand;
import arrodes.command.TodoCommand;
import arrodes.command.UnmarkCommand;
import arrodes.command.UpcomingCommand;
import arrodes.exception.ArrodesException;

/** Converts raw CLI text into validated command objects. */
public class CommandParser {

    /** Creates a parser; parsing operations are available as static methods. */
    public CommandParser() {
    }

    /**
     * Splits raw input into a command word, description, and slash-prefixed parameters.
     * @param userInput input entered at the CLI prompt
     * @return tokenized representation of the input
     * @throws ArrodesException if the input is null or blank, or has malformed parameters
     */
    public static TokenizedCommand tokenize(String userInput) throws ArrodesException {
        if (userInput == null || userInput.isBlank()) {
            throw new ArrodesException(ArrodesException.NO_INPUT);
        }

        String[] splitInput = userInput.split(" ", 2);

        String command = splitInput[0];
        String description = "";
        Map<String, String> parameters = new HashMap<>();

        if (splitInput.length < 2) {
            return new TokenizedCommand(command, description, parameters);
        }

        String remainingInput = splitInput[1];
        String[] sections = splitByFlag(remainingInput);
        description = sections[0];
        parameters = formParameters(sections);
        return new TokenizedCommand(command, description, parameters);
    }

    /**
     * Validates tokenized input and creates the corresponding command.
     * @param userInput raw command entered by the user
     * @return executable command
     * @throws ArrodesException if the command or its arguments are invalid
     */
    public static Command parse(String userInput) {
        TokenizedCommand tokenizedCommand = tokenize(userInput);
        return createCommand(tokenizedCommand);
    }

    /** Creates a command from its tokenized representation. */
    private static Command createCommand(TokenizedCommand tokenizedCommand) {
        assert tokenizedCommand != null : "tokenization returned empty unexpectedly";
        return switch (tokenizedCommand.getCommand()) {
            case "bye" -> parseBye(tokenizedCommand);
            case "mark" -> new MarkCommand(parseTaskNumber(tokenizedCommand));
            case "unmark" -> new UnmarkCommand(parseTaskNumber(tokenizedCommand));
            case "delete" -> new DeleteCommand(parseTaskNumber(tokenizedCommand));
            case "todo" -> parseTodo(tokenizedCommand);
            case "deadline" -> parseDeadline(tokenizedCommand);
            case "event" -> parseEvent(tokenizedCommand);
            case "list" -> parseList(tokenizedCommand);
            case "find" -> parseFind(tokenizedCommand);
            case "upcoming" -> parseUpcoming(tokenizedCommand);
            case "tag" -> parseTag(tokenizedCommand);
            default -> throw new ArrodesException(ArrodesException.UNKNOWN_COMMAND);
        };
    }
    /** Creates an exit command after validating that it has no arguments. */
    private static Command parseBye(TokenizedCommand tokenizedCommand) {
        if (tokenizedCommand.hasDescription()) {
            throw new ArrodesException(ArrodesException.INCORRECT_PARAMS);
        }
        return new ByeCommand();
    }

    /** Parses a one-based task number shared by task-number commands. */
    private static int parseTaskNumber(TokenizedCommand tokenizedCommand) {
        validateDescriptionWithoutParameters(tokenizedCommand);
        return parseTaskNumberValue(tokenizedCommand.getDescription());
    }

    /** Converts a task number string into a validated integer. */
    private static int parseTaskNumberValue(String taskNumber) {
        try {
            return Integer.parseInt(taskNumber);
        } catch (NumberFormatException exception) {
            throw new ArrodesException(ArrodesException.NOT_A_NUMBER);
        }
    }

    /** Creates a todo command after validating its description. */
    private static Command parseTodo(TokenizedCommand tokenizedCommand) {
        validateDescriptionWithoutParameters(tokenizedCommand);
        return new TodoCommand(tokenizedCommand.getDescription());
    }

    /** Creates a deadline command from its {@code /by} parameter. */
    private static Command parseDeadline(TokenizedCommand tokenizedCommand) {
        validateDescription(tokenizedCommand);
        String dueBy = requireParameter(tokenizedCommand, "by");
        return new DeadlineCommand(tokenizedCommand.getDescription(), parseDateTime(dueBy));
    }

    /** Creates an event command from its {@code /from} and {@code /to} parameters. */
    private static Command parseEvent(TokenizedCommand tokenizedCommand) {
        validateDescription(tokenizedCommand);
        String from = requireParameter(tokenizedCommand, "from");
        String to = requireParameter(tokenizedCommand, "to");
        if (from.contains("T") ^ to.contains("T")) {
            throw new ArrodesException(ArrodesException.INCONSISTENT_TIME_FORMAT);
        }
        return new EventCommand(tokenizedCommand.getDescription(), parseDateTime(from), parseDateTime(to),
                from.contains("T"), to.contains("T"));
    }

    /** Creates a list command after validating that it has no arguments. */
    private static Command parseList(TokenizedCommand tokenizedCommand) {
        if (tokenizedCommand.hasDescription()) {
            throw new ArrodesException(ArrodesException.UNNECESSARY_DESCRIPTION);
        }
        if (tokenizedCommand.hasParameters()) {
            throw new ArrodesException(ArrodesException.INCORRECT_PARAMS);
        }
        return new ListCommand();
    }

    /** Creates a find command after validating its description. */
    private static Command parseFind(TokenizedCommand tokenizedCommand) {
        validateDescriptionWithoutParameters(tokenizedCommand);
        return new FindCommand(tokenizedCommand.getDescription());
    }

    /** Creates an upcoming command from its {@code /on} parameter. */
    private static Command parseUpcoming(TokenizedCommand tokenizedCommand) {
        if (tokenizedCommand.hasDescription()) {
            throw new ArrodesException(ArrodesException.UNNECESSARY_DESCRIPTION);
        }
        String on = requireParameter(tokenizedCommand, "on");
        return new UpcomingCommand(parseDateTime(on), on.contains("T"));
    }

    /** Creates a tag command from a task number and its {@code /with} parameter. */
    private static Command parseTag(TokenizedCommand tokenizedCommand) {
        validateDescription(tokenizedCommand);
        if (tokenizedCommand.getParameters().size() != 1) {
            throw new ArrodesException(ArrodesException.INCORRECT_PARAMS);
        }
        String tag = requireParameter(tokenizedCommand, "with");
        return new TagCommand(parseTaskNumberValue(tokenizedCommand.getDescription()), tag);
    }

    /** Validates that a command has a non-empty description. */
    private static void validateDescription(TokenizedCommand tokenizedCommand) {
        if (!tokenizedCommand.hasDescription()) {
            throw new ArrodesException(ArrodesException.EMPTY_DESCRIPTION);
        }
    }

    /** Validates that a command has a description and no parameters. */
    private static void validateDescriptionWithoutParameters(TokenizedCommand tokenizedCommand) {
        validateDescription(tokenizedCommand);
        if (tokenizedCommand.hasParameters()) {
            throw new ArrodesException(ArrodesException.INCORRECT_PARAMS);
        }
    }

    /** Returns a required command parameter. */
    private static String requireParameter(TokenizedCommand tokenizedCommand, String parameterName) {
        String parameter = tokenizedCommand.getParameters().get(parameterName);
        if (parameter == null) {
            throw new ArrodesException(ArrodesException.INCORRECT_PARAMS);
        }
        return parameter;
    }

    /**
     * Splits the portion after the command word at slash separators and trims each section.
     * @param remainingInput content after the command keyword
     * @return description followed by parameter sections
     */
    private static String[] splitByFlag(String remainingInput) {
        String[] sections = remainingInput.split("/");
        for (int i = 0; i < sections.length; i++) {
            sections[i] = sections[i].strip();
        }
        return sections;
    }

    /**
     * Converts parameter sections into flag-to-value mappings.
     * @param sections description followed by parameter sections
     * @return parsed parameter mappings
     * @throws ArrodesException if a parameter does not contain both a flag and value
     */
    private static Map<String, String> formParameters(String[] sections) throws ArrodesException {
        Map<String, String> parameters = new HashMap<>();
        try {
            for (int i = 1; i < sections.length; i++) {
                String[] flagAndValue = sections[i].split(" ", 2);
                String flag = flagAndValue[0];
                String value = flagAndValue[1];
                parameters.put(flag, value);
            }
        } catch (Exception e) {
            throw new ArrodesException(ArrodesException.INCORRECT_PARAMS);
        }
        return parameters;
    }
    /**
     * Parses an ISO date or date-time, normalising date-only values to midnight.
     * @param value date in {@code yyyy-MM-dd} or date-time in {@code yyyy-MM-ddTHH:mm} form
     * @return parsed local date-time
     * @throws ArrodesException if the value has an invalid format or date
     */
    private static LocalDateTime parseDateTime(String value) throws ArrodesException {
        boolean hasExpectedShape = value != null
                && value.matches("\\d{4}-\\d{2}-\\d{2}(T\\d{2}:\\d{2})?");
        try {
            return LocalDateTime.parse(value);
        } catch (DateTimeParseException exception) {
            try {
                return LocalDate.parse(value).atStartOfDay();
            } catch (DateTimeParseException ignored) {
                if (hasExpectedShape) {
                    throw new ArrodesException(ArrodesException.INVALID_DATE);
                }
                throw new ArrodesException(ArrodesException.INVALID_DATE_FORMAT);
            }
        }
    }
}
