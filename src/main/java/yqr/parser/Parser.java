package yqr.parser;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import yqr.command.AddCommand;
import yqr.command.Command;
import yqr.command.DeleteCommand;
import yqr.command.ExitCommand;
import yqr.command.FindCommand;
import yqr.command.ListCommand;
import yqr.command.MarkCommand;
import yqr.command.UndoCommand;
import yqr.command.UnmarkCommand;
import yqr.exception.YqrException;
import yqr.task.Deadline;
import yqr.task.Event;
import yqr.task.EventTimeValidator;
import yqr.task.Todo;

/**
 * Parses user commands and converts their arguments into values used by the application.
 */
public class Parser {
    private static final List<String> COMMAND_WORDS = List.of(
            "todo", "deadline", "event", "list", "mark", "unmark", "delete", "find", "undo", "bye");
    private static final Pattern BY_PARAMETER = parameterPattern("/by");
    private static final Pattern FROM_PARAMETER = parameterPattern("/from");
    private static final Pattern TO_PARAMETER = parameterPattern("/to");

    /** Prevents creation of this utility class. */
    private Parser() {
    }

    /**
     * Parses a full user command into a command that can be executed.
     *
     * @param fullCommand command entered by the user.
     * @return command corresponding to the user input.
     * @throws YqrException if the command or its arguments are invalid.
     */
    public static Command parse(String fullCommand) throws YqrException {
        if (fullCommand == null || fullCommand.isBlank()) {
            throw new YqrException("Please enter a command");
        }
        if (fullCommand.indexOf('\n') >= 0 || fullCommand.indexOf('\r') >= 0) {
            throw new YqrException("Please enter one command at a time");
        }
        String command = fullCommand.trim();
        String commandWord = getCommandWord(command);

        switch (commandWord) {
            case "list":
                if (command.equals("list")) {
                    return new ListCommand();
                }
                throw new YqrException("The list command does not accept parameters");
            case "mark":
                return new MarkCommand(parseTaskNumber(command, "mark"));
            case "unmark":
                return new UnmarkCommand(parseTaskNumber(command, "unmark"));
            case "delete":
                return new DeleteCommand(parseTaskNumber(command, "delete"));
            case "todo":
                return new AddCommand(parseTodo(command));
            case "deadline":
                return new AddCommand(parseDeadline(command));
            case "event":
                return new AddCommand(parseEvent(command));
            case "find":
                return new FindCommand(parseFindKeyword(command));
            case "undo":
                if (command.equals("undo")) {
                    return new UndoCommand();
                }
                throw new YqrException("The undo command does not accept parameters");
            case "bye":
                if (command.equals("bye")) {
                    return new ExitCommand();
                }
                throw new YqrException("The bye command does not accept parameters");
            default:
                break;
        }

        throw new YqrException(createUnknownCommandMessage(commandWord));
    }

    /** Returns an actionable message for an unrecognised command word. */
    private static String createUnknownCommandMessage(String commandWord) {
        String suggestion = findClosestCommand(commandWord);
        if (suggestion != null) {
            return "Unknown command '" + commandWord + "'. Did you mean '" + suggestion + "'?";
        }
        return "Unknown command '" + commandWord + "'. Available commands: "
                + String.join(", ", COMMAND_WORDS);
    }

    /** Returns a known command when the input is likely to be a small typo. */
    private static String findClosestCommand(String commandWord) {
        String normalizedCommand = commandWord.toLowerCase(Locale.ROOT);
        String closestCommand = null;
        int closestDistance = Integer.MAX_VALUE;
        for (String candidate : COMMAND_WORDS) {
            int distance = calculateEditDistance(normalizedCommand, candidate);
            if (distance < closestDistance) {
                closestDistance = distance;
                closestCommand = candidate;
            }
        }
        int maximumDistance = normalizedCommand.length() <= 3 ? 1 : 2;
        return closestDistance <= maximumDistance ? closestCommand : null;
    }

    /** Calculates the Levenshtein edit distance between two command words. */
    private static int calculateEditDistance(String source, String target) {
        int[] previousRow = new int[target.length() + 1];
        for (int column = 0; column <= target.length(); column++) {
            previousRow[column] = column;
        }

        for (int row = 1; row <= source.length(); row++) {
            int[] currentRow = new int[target.length() + 1];
            currentRow[0] = row;
            for (int column = 1; column <= target.length(); column++) {
                int replacementCost = source.charAt(row - 1) == target.charAt(column - 1) ? 0 : 1;
                currentRow[column] = Math.min(
                        Math.min(currentRow[column - 1] + 1, previousRow[column] + 1),
                        previousRow[column - 1] + replacementCost);
            }
            previousRow = currentRow;
        }
        return previousRow[target.length()];
    }

    /**
     * Returns the first word of a command, which identifies the requested operation.
     *
     * @param command command entered by the user.
     * @return command word, or an empty string when the command is empty.
     */
    private static String getCommandWord(String command) {
        return command.isEmpty() ? "" : command.split("\\s+", 2)[0];
    }

    /**
     * Creates a todo from a command after validating its description.
     *
     * @param command todo command entered by the user.
     * @return parsed todo.
     * @throws YqrException if the description is missing.
     */
    private static Todo parseTodo(String command) throws YqrException {
        String description = command.substring("todo".length()).trim();
        if (description.isEmpty()) {
            throw new YqrException("Please input task description");
        }
        rejectParameter(description, BY_PARAMETER, "Todo format: todo DESCRIPTION");
        rejectParameter(description, FROM_PARAMETER, "Todo format: todo DESCRIPTION");
        rejectParameter(description, TO_PARAMETER, "Todo format: todo DESCRIPTION");
        validateTaskText(description);
        return new Todo(description);
    }

    /**
     * Creates a deadline from a command after validating its description and deadline.
     *
     * @param command deadline command entered by the user.
     * @return parsed deadline.
     * @throws YqrException if the description or deadline is missing or invalid.
     */
    private static Deadline parseDeadline(String command) throws YqrException {
        String taskDetails = command.substring("deadline".length()).trim();
        if (taskDetails.isEmpty()) {
            throw new YqrException("Please input task description");
        }
        rejectParameter(taskDetails, FROM_PARAMETER,
                "Deadline format: deadline DESCRIPTION /by yyyy-MM-dd");
        rejectParameter(taskDetails, TO_PARAMETER,
                "Deadline format: deadline DESCRIPTION /by yyyy-MM-dd");
        int[] byBounds = findSingleParameter(taskDetails, BY_PARAMETER, "/by",
                "Please input the deadline using /by yyyy-MM-dd");
        String description = taskDetails.substring(0, byBounds[0]).trim();
        String by = taskDetails.substring(byBounds[1]).trim();
        if (description.isEmpty()) {
            throw new YqrException("Please input task description");
        }
        if (by.isEmpty()) {
            throw new YqrException("Please input the deadline");
        }
        validateTaskText(description);
        try {
            return new Deadline(description, LocalDate.parse(by));
        } catch (DateTimeParseException e) {
            throw new YqrException("Please input a valid deadline in yyyy-MM-dd format");
        }
    }

    /**
     * Creates an event from a command after validating its description and time details.
     *
     * @param command event command entered by the user.
     * @return parsed event.
     * @throws YqrException if the description or event times are missing.
     */
    private static Event parseEvent(String command) throws YqrException {
        String taskDetails = command.substring("event".length()).trim();
        if (taskDetails.isEmpty()) {
            throw new YqrException("Please input task description");
        }
        rejectParameter(taskDetails, BY_PARAMETER,
                "Event format: event DESCRIPTION /from START /to END");
        int[] fromBounds = findSingleParameter(taskDetails, FROM_PARAMETER, "/from",
                "Please input event details using /from START /to END");
        int[] toBounds = findSingleParameter(taskDetails, TO_PARAMETER, "/to",
                "Please input event details using /from START /to END");
        if (fromBounds[0] > toBounds[0]) {
            throw new YqrException("Event format: event DESCRIPTION /from START /to END");
        }
        String description = taskDetails.substring(0, fromBounds[0]).trim();
        String from = taskDetails.substring(fromBounds[1], toBounds[0]).trim();
        String to = taskDetails.substring(toBounds[1]).trim();
        if (description.isEmpty()) {
            throw new YqrException("Please input task description");
        }
        if (from.isEmpty() || to.isEmpty()) {
            throw new YqrException("Please input the starting and ending details");
        }
        validateTaskText(description);
        validateTaskText(from);
        validateTaskText(to);
        EventTimeValidator.validate(from, to);
        return new Event(description, from, to);
    }

    /**
     * Extracts and validates the keyword from a find command.
     *
     * @param command find command entered by the user.
     * @return keyword to search for.
     * @throws YqrException if the keyword is missing.
     */
    private static String parseFindKeyword(String command) throws YqrException {
        String keyword = command.substring("find".length()).trim();
        if (keyword.isEmpty()) {
            throw new YqrException("Please input a keyword to search for");
        }
        return keyword;
    }

    /**
     * Extracts a task number from a command.
     *
     * @param command command containing a task number.
     * @param commandWord command word to remove before parsing.
     * @return parsed task number.
     * @throws YqrException if the task number is missing or not an integer.
     */
    private static int parseTaskNumber(String command, String commandWord) throws YqrException {
        String numberText = command.substring(commandWord.length()).trim();
        if (!numberText.matches("[1-9]\\d*")) {
            throw new YqrException("Please input a positive task number");
        }
        try {
            return Integer.parseInt(numberText);
        } catch (NumberFormatException e) {
            throw new YqrException("The task number is too large");
        }
    }

    /**
     * Finds one command parameter and rejects duplicate occurrences.
     *
     * @param text command text containing the parameter.
     * @param pattern pattern matching the parameter as a complete token.
     * @param parameterName parameter displayed in an error message.
     * @param missingMessage message used when the parameter is absent.
     * @return start and end indexes of the parameter.
     * @throws YqrException if the parameter is missing or duplicated.
     */
    private static int[] findSingleParameter(String text, Pattern pattern, String parameterName,
                                             String missingMessage) throws YqrException {
        Matcher matcher = pattern.matcher(text);
        if (!matcher.find()) {
            throw new YqrException(missingMessage);
        }
        int[] bounds = {matcher.start(), matcher.end()};
        if (matcher.find()) {
            throw new YqrException("Please specify " + parameterName + " only once");
        }
        return bounds;
    }

    /** Rejects a parameter that is not valid for the current command. */
    private static void rejectParameter(String text, Pattern pattern, String message) throws YqrException {
        if (pattern.matcher(text).find()) {
            throw new YqrException(message);
        }
    }

    /** Rejects text that cannot be represented safely in the storage format. */
    private static void validateTaskText(String text) throws YqrException {
        if (text.indexOf('|') >= 0) {
            throw new YqrException("Task details cannot contain the '|' character");
        }
    }

    /** Creates a pattern matching a command parameter as a complete token. */
    private static Pattern parameterPattern(String parameter) {
        return Pattern.compile("(?<!\\S)" + Pattern.quote(parameter) + "(?!\\S)");
    }
}
