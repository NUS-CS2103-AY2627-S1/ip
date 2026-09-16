package chattingheads.parser;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import chattingheads.command.AddDeadlineCommand;
import chattingheads.command.AddEventCommand;
import chattingheads.command.AddTodoCommand;
import chattingheads.command.Command;
import chattingheads.command.DeleteCommand;
import chattingheads.command.ExitCommand;
import chattingheads.command.FindCommand;
import chattingheads.command.ListCommand;
import chattingheads.command.MarkCommand;
import chattingheads.command.RescheduleDeadlineCommand;
import chattingheads.command.RescheduleEventCommand;
import chattingheads.command.UnmarkCommand;
import chattingheads.exception.ChattingHeadsException;
import chattingheads.exception.InvalidCommandException;
import chattingheads.exception.InvalidInputException;

/**
 * Parses user input into executable commands.
 */
public class Parser {

    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/uuuu HH:mm").withResolverStyle(ResolverStyle.STRICT);

    /**
     * Parses user input into the corresponding command.
     *
     * @param input User input to parse.
     * @return Command represented by the input.
     * @throws ChattingHeadsException If the command is unrecognised or its arguments are invalid or missing.
     */
    public Command parse(String input) throws ChattingHeadsException {
        if (input.isBlank()) {
            throw InvalidInputException.invalidInput("command");
        }

        String[] tokens = input.strip().split("\\s+");
        String commandName = tokens[0];
        String[] arguments = Arrays.copyOfRange(tokens, 1, tokens.length);

        return switch (commandName) {
            case "todo" -> parseTodo(arguments);
            case "deadline" -> parseDeadline(arguments);
            case "event" -> parseEvent(arguments);
            case "list" -> new ListCommand();
            case "find" -> parseFind(arguments);
            case "mark" -> new MarkCommand(parseTaskNumber(arguments));
            case "unmark" -> new UnmarkCommand(parseTaskNumber(arguments));
            case "reschedule" -> parseReschedule(arguments);
            case "delete" -> new DeleteCommand(parseTaskNumber(arguments));
            case "bye" -> new ExitCommand();
            default -> throw new InvalidCommandException();
        };
    }

    /**
     * Parses the arguments of a todo command.
     *
     * @param arguments Arguments of the command.
     * @return Command for adding the todo.
     * @throws InvalidInputException If the description is missing.
     */
    private AddTodoCommand parseTodo(String[] arguments) throws InvalidInputException {
        String description = joinTokens(arguments, 0, arguments.length);

        if (description.isEmpty()) {
            throw InvalidInputException.invalidInput("description");
        }

        return new AddTodoCommand(description);
    }

    /**
     * Parses the arguments of a deadline command.
     *
     * @param arguments Arguments of the command.
     * @return Command for adding the deadline.
     * @throws InvalidInputException If the description or deadline is invalid or missing.
     */
    private AddDeadlineCommand parseDeadline(String[] arguments) throws InvalidInputException {
        validateAddDeadlineSyntax(List.of(arguments));

        int byMarkerIndex = arguments.length;

        for (int i = 0; i < arguments.length; i++) {
            if (arguments[i].equals("/by")) {
                byMarkerIndex = i;
                break;
            }
        }
        List<String> invalidInputs = new ArrayList<>();
        String description = joinTokens(arguments, 0, byMarkerIndex);
        LocalDateTime deadline = parseDateTime(arguments, byMarkerIndex + 1, arguments.length);

        if (description.isEmpty()) {
            invalidInputs.add("description");
        }
        if (deadline == null) {
            invalidInputs.add("deadline");
        }
        if (!invalidInputs.isEmpty()) {
            throw InvalidInputException.invalidInput(invalidInputs.toArray(String[]::new));
        }

        return new AddDeadlineCommand(description, deadline);
    }

    /**
     * Parses the arguments of an event command.
     *
     * @param arguments Arguments of the command.
     * @return Command for adding the event.
     * @throws InvalidInputException If the description, start, or end is invalid or missing.
     */
    private AddEventCommand parseEvent(String[] arguments) throws InvalidInputException {
        validateAddEventSyntax(List.of(arguments));

        int fromMarkerIndex = arguments.length;
        int toMarkerIndex = arguments.length;

        for (int i = 0; i < arguments.length; i++) {
            if (arguments[i].equals("/from")) {
                fromMarkerIndex = i;
            } else if (arguments[i].equals("/to")) {
                toMarkerIndex = i;
                break;
            }
        }
        List<String> invalidInputs = new ArrayList<>();
        String description = joinTokens(arguments, 0, fromMarkerIndex);
        LocalDateTime start = parseDateTime(arguments, fromMarkerIndex + 1, toMarkerIndex);
        LocalDateTime end = parseDateTime(arguments, toMarkerIndex + 1, arguments.length);

        if (description.isEmpty()) {
            invalidInputs.add("description");
        }
        if (start == null) {
            invalidInputs.add("start");
        }
        if (end == null) {
            invalidInputs.add("end");
        }
        if (!invalidInputs.isEmpty()) {
            throw InvalidInputException.invalidInput(invalidInputs.toArray(String[]::new));
        }

        return new AddEventCommand(description, start, end);
    }

    private Command parseReschedule(String[] arguments) throws InvalidInputException {
        validateRescheduleSyntax(List.of(arguments));

        if (Arrays.asList(arguments).contains("/by")) {
            return parseRescheduleDeadline(arguments);
        }

        return parseRescheduleEvent(arguments);
    }

    private Command parseRescheduleDeadline(String[] arguments) throws InvalidInputException {
        int byMarkerIndex = List.of(arguments).indexOf("/by");
        List<String> invalidInputs = new ArrayList<>();
        int taskNumber = -1;

        try {
            taskNumber = parseTaskNumber(arguments[0]);
        } catch (InvalidInputException e) {
            invalidInputs.add("task number");
        }

        LocalDateTime deadline = parseDateTime(arguments, byMarkerIndex + 1, arguments.length);

        if (deadline == null) {
            invalidInputs.add("deadline");
        }
        if (!invalidInputs.isEmpty()) {
            throw InvalidInputException.invalidInput(invalidInputs.toArray(String[]::new));
        }

        return new RescheduleDeadlineCommand(taskNumber, deadline);
    }

    private Command parseRescheduleEvent(String[] arguments) throws InvalidInputException {
        int fromMarkerIndex = List.of(arguments).indexOf("/from");
        int toMarkerIndex = List.of(arguments).indexOf("/to");
        List<String> invalidInputs = new ArrayList<>();
        int taskNumber = -1;
        try {
            taskNumber = parseTaskNumber(arguments[0]);
        } catch (InvalidInputException e) {
            invalidInputs.add("task number");
        }

        LocalDateTime start = parseDateTime(arguments, fromMarkerIndex + 1, toMarkerIndex);
        LocalDateTime end = parseDateTime(arguments, toMarkerIndex + 1, arguments.length);

        if (start == null) {
            invalidInputs.add("start");
        }
        if (end == null) {
            invalidInputs.add("end");
        }
        if (!invalidInputs.isEmpty()) {
            throw InvalidInputException.invalidInput(invalidInputs.toArray(String[]::new));
        }

        return new RescheduleEventCommand(taskNumber, start, end);
    }

    private Command parseFind(String[] arguments) throws InvalidInputException {
        String keyword = joinTokens(arguments, 0, arguments.length);

        if (keyword.isEmpty()) {
            throw InvalidInputException.invalidInput("keyword");
        }

        return new FindCommand(keyword);
    }

    private int parseTaskNumber(String[] arguments) throws InvalidInputException {
        if (arguments.length != 1) {
            throw InvalidInputException.invalidInput("task number");
        }

        return parseTaskNumber(arguments[0]);
    }

    private int parseTaskNumber(String argument) throws InvalidInputException {
        try {
            return Integer.parseInt(argument);
        } catch (NumberFormatException e) {
            throw InvalidInputException.invalidInput("task number");
        }
    }

    private String joinTokens(String[] tokens, int start, int end) {
        if (start >= 0 && end <= tokens.length && start <= end) {
            return String.join(" ", Arrays.copyOfRange(tokens, start, end));
        }
        return "";
    }

    private LocalDateTime parseDateTime(String[] tokens, int start, int end) {
        try {
            return LocalDateTime.parse(joinTokens(tokens, start, end), DATE_TIME_FORMATTER);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    private static void validateAddDeadlineSyntax(List<String> arguments)
            throws InvalidInputException {
        validateNoDuplicatePrefix(arguments, "/by");
        validateRequiredPrefix(arguments, "/by");
    }

    private static void validateAddEventSyntax(List<String> arguments)
            throws InvalidInputException {
        validateNoDuplicatePrefix(arguments, "/from");
        validateNoDuplicatePrefix(arguments, "/to");
        validateRequiredPair(arguments, "/from", "/to");
        validatePrefixOrder(arguments, "/from", "/to");
    }

    private static void validateRescheduleSyntax(List<String> arguments)
            throws InvalidInputException {
        if (arguments.isEmpty()) {
            throw InvalidInputException.invalidInput("task number");
        }

        validateNoDuplicatePrefix(arguments, "/by");
        validateNoDuplicatePrefix(arguments, "/from");
        validateNoDuplicatePrefix(arguments, "/to");
        validatePairedPrefixes(arguments, "/from", "/to");
        validatePrefixOrder(arguments, "/from", "/to");

        boolean hasBy = arguments.contains("/by");
        boolean hasFrom = arguments.contains("/from");

        if (hasBy && hasFrom) {
            throw InvalidInputException.incompatiblePrefixes("/by", "/from");
        }

        if (!hasBy && !hasFrom) {
            throw InvalidInputException.invalidInput("/by or /from and /to");
        }

        if (hasBy && arguments.indexOf("/by") != 1) {
            throw InvalidInputException.unexpectedInput();
        }

        if (hasFrom && arguments.indexOf("/from") != 1) {
            throw InvalidInputException.unexpectedInput();
        }
    }

    private static void validateRequiredPrefix(
            List<String> arguments, String prefix)
            throws InvalidInputException {
        if (!arguments.contains(prefix)) {
            throw InvalidInputException.invalidInput(prefix);
        }
    }

    private static void validateRequiredPair(
            List<String> arguments, String first, String second)
            throws InvalidInputException {
        boolean hasFirst = arguments.contains(first);
        boolean hasSecond = arguments.contains(second);

        if (!hasFirst && !hasSecond) {
            throw InvalidInputException.invalidInput(first, second);
        }

        if (!hasFirst) {
            throw InvalidInputException.invalidInput(first);
        }

        if (!hasSecond) {
            throw InvalidInputException.invalidInput(second);
        }
    }

    private static void validatePairedPrefixes(
            List<String> arguments, String first, String second)
            throws InvalidInputException {
        boolean hasFirst = arguments.contains(first);
        boolean hasSecond = arguments.contains(second);

        if (hasFirst != hasSecond) {
            throw InvalidInputException.invalidInput(
                    hasFirst ? second : first);
        }
    }

    private static void validateNoDuplicatePrefix(
            List<String> arguments, String prefix)
            throws InvalidInputException {
        if (Collections.frequency(arguments, prefix) > 1) {
            throw InvalidInputException.duplicatePrefix(prefix);
        }
    }

    private static void validatePrefixOrder(
            List<String> arguments, String first, String second)
            throws InvalidInputException {
        int firstIndex = arguments.indexOf(first);
        int secondIndex = arguments.indexOf(second);

        if (firstIndex != -1 && secondIndex != -1 && firstIndex > secondIndex) {
            throw InvalidInputException.invalidPrefixOrder(first, second);
        }
    }
}
