package remy.parser;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;

import remy.command.AddCommand;
import remy.command.Command;
import remy.command.DeleteCommand;
import remy.command.ExitCommand;
import remy.command.FindCommand;
import remy.command.ListCommand;
import remy.command.MarkCommand;
import remy.command.SortCommand;
import remy.exception.RemyException;
import remy.task.Deadline;
import remy.task.Event;
import remy.task.Task;
import remy.task.Todo;

/**
 * Interprets user commands and converts their arguments into application values.
 *
 * @author LEE-wz
 */
public class Parser {
    /** Command keyword for ending the application. */
    private static final String BYE_COMMAND = "bye";

    /** Command keyword for listing tasks. */
    private static final String LIST_COMMAND = "list";

    /** Command keyword for finding tasks. */
    private static final String FIND_COMMAND = "find";

    /** Command keyword for sorting tasks. */
    private static final String SORT_COMMAND = "sort";

    /** Guidance shown when a sort command does not follow the supported syntax. */
    private static final String INVALID_SORT_MESSAGE = "Invalid sort command.\n"
            + "Use: sort /by date [/order asc|desc]\n"
            + "Example: sort /by date /order asc";

    /** Guidance shown when command whitespace does not follow the supported syntax. */
    private static final String INVALID_SPACING_MESSAGE = "Invalid command spacing.\n"
            + "Use one space between command parts and remove leading or trailing spaces.";

    /** Guidance shown when a deadline command has misplaced or repeated parameters. */
    private static final String INVALID_DEADLINE_COMMAND_MESSAGE = "Invalid deadline command.\n"
            + "Use: deadline DESCRIPTION /by DATE\n"
            + "Specify /by exactly once.";

    /** Guidance shown when an event command has misplaced, unknown, or repeated parameters. */
    private static final String INVALID_EVENT_COMMAND_MESSAGE = "Invalid event command.\n"
            + "Use: event DESCRIPTION /from START /to END\n"
            + "Specify /from and /to exactly once and in that order.";

    /** Error shown when a deadline endpoint is not a real supported date. */
    private static final String INVALID_DEADLINE_DATE_MESSAGE =
            "The deadline date is invalid. Use a real date in a supported format.";

    /** Error shown when event endpoints are not real dates in the same supported format. */
    private static final String INVALID_EVENT_DATE_MESSAGE =
            "The event dates are invalid. Use real dates in the same supported format.";

    /** Error shown when an event does not end after it starts. */
    private static final String INVALID_EVENT_RANGE_MESSAGE = "An event must start before it ends.";

    /** Command keyword for deleting a task. */
    private static final String DELETE_COMMAND = "delete";

    /** Command keyword for marking a task as completed. */
    private static final String MARK_COMMAND = "mark";

    /** Command keyword for marking a task as incomplete. */
    private static final String UNMARK_COMMAND = "unmark";

    /** Command keyword for creating a to-do task. */
    private static final String TODO_COMMAND = "todo";

    /** Command keyword for creating a deadline task. */
    private static final String DEADLINE_COMMAND = "deadline";

    /** Command keyword for creating an event task. */
    private static final String EVENT_COMMAND = "event";

    /** Delimiter introducing a deadline endpoint. */
    private static final String DEADLINE_DELIMITER = "/by";

    /** Delimiter introducing an event's start endpoint. */
    private static final String EVENT_START_DELIMITER = "/from";

    /** Delimiter introducing an event's end endpoint. */
    private static final String EVENT_END_DELIMITER = "/to";

    /**
     * Parses a user message into an executable command.
     *
     * @param message user message to inspect
     * @return command represented by the user message
     * @throws RemyException if the message is unknown or its arguments are invalid
     */
    public Command parse(String message) {
        validateCommandSpacing(message);
        CommandType commandType = parseCommandType(message);
        return switch (commandType) {
            case BYE -> {
                validateNoArguments(message, BYE_COMMAND);
                yield new ExitCommand();
            }
            case LIST -> {
                validateNoArguments(message, LIST_COMMAND);
                yield new ListCommand();
            }
            case FIND -> new FindCommand(parseFindKeyword(message));
            case SORT -> new SortCommand(parseSortOrder(message));
            case DELETE -> new DeleteCommand(parseTaskIndex(message, commandType));
            case MARK -> new MarkCommand(parseTaskIndex(message, commandType), true);
            case UNMARK -> new MarkCommand(parseTaskIndex(message, commandType), false);
            case TODO, DEADLINE, EVENT -> new AddCommand(parseTask(message, commandType));
            case UNKNOWN -> throw new RemyException();
        };
    }

    /**
     * Identifies the command represented by a user message.
     *
     * @param message user message to inspect
     * @return the command type represented by the message, or UNKNOWN when it is not recognised
     */
    private CommandType parseCommandType(String message) {
        if (message == null) {
            return CommandType.UNKNOWN;
        }

        if (startsWithCommandKeyword(message, BYE_COMMAND)) {
            return CommandType.BYE;
        }
        if (startsWithCommandKeyword(message, LIST_COMMAND)) {
            return CommandType.LIST;
        }
        if (startsWithCommandKeyword(message, FIND_COMMAND)) {
            return CommandType.FIND;
        }
        if (startsWithCommandKeyword(message, SORT_COMMAND)) {
            return CommandType.SORT;
        }
        if (startsWithCommandKeyword(message, DELETE_COMMAND)) {
            return CommandType.DELETE;
        }
        if (startsWithCommandKeyword(message, MARK_COMMAND)) {
            return CommandType.MARK;
        }
        if (startsWithCommandKeyword(message, UNMARK_COMMAND)) {
            return CommandType.UNMARK;
        }
        if (startsWithCommandKeyword(message, TODO_COMMAND)) {
            return CommandType.TODO;
        }
        if (startsWithCommandKeyword(message, DEADLINE_COMMAND)) {
            return CommandType.DEADLINE;
        }
        if (startsWithCommandKeyword(message, EVENT_COMMAND)) {
            return CommandType.EVENT;
        }

        return CommandType.UNKNOWN;
    }

    /**
     * Returns whether a message starts with the given complete command keyword.
     *
     * @param message user message to inspect
     * @param commandKeyword command keyword to match
     * @return true when the message is the keyword or starts with the keyword followed by a space
     */
    private boolean startsWithCommandKeyword(String message, String commandKeyword) {
        return message.equals(commandKeyword) || message.startsWith(commandKeyword + " ");
    }

    /**
     * Rejects ambiguous whitespace and control characters in a command.
     *
     * @param message user message to inspect
     * @throws RemyException if the message contains unsupported spacing or control characters
     */
    private void validateCommandSpacing(String message) {
        if (message == null) {
            return;
        }

        boolean hasOuterSpaces = message.startsWith(" ") || message.endsWith(" ");
        boolean hasRepeatedSpaces = message.contains("  ");
        boolean hasUnsupportedCharacter = message.codePoints().anyMatch(character ->
                Character.isISOControl(character)
                        || (character != ' '
                        && (Character.isWhitespace(character) || Character.isSpaceChar(character))));
        if (hasOuterSpaces || hasRepeatedSpaces || hasUnsupportedCharacter) {
            throw new RemyException(INVALID_SPACING_MESSAGE);
        }
    }

    /**
     * Rejects arguments supplied to a command that accepts none.
     *
     * @param message user message to inspect
     * @param commandKeyword command keyword that must appear alone
     * @throws RemyException if text follows the command keyword
     */
    private void validateNoArguments(String message, String commandKeyword) {
        if (!message.equals(commandKeyword)) {
            throw new RemyException("The " + commandKeyword + " command does not accept parameters.\n"
                    + "Use: " + commandKeyword);
        }
    }

    /**
     * Parses the keyword supplied to a find command.
     *
     * @param message user message containing the keyword
     * @return the search keyword
     * @throws RemyException if no keyword is supplied
     */
    private String parseFindKeyword(String message) {
        String keyword = message.substring(FIND_COMMAND.length()).strip();
        if (keyword.isEmpty()) {
            throw new RemyException("You need to provide a keyword to find.");
        }
        return keyword;
    }

    /**
     * Parses whether a sort command requests ascending chronological order.
     *
     * @param message sort command to parse
     * @return true for ascending order, or false for descending order
     * @throws RemyException if the command does not follow the supported sort syntax
     */
    private boolean parseSortOrder(String message) {
        String arguments = message.substring(SORT_COMMAND.length()).strip();
        String[] argumentTokens = arguments.isEmpty() ? new String[0] : arguments.split("\\s+");

        boolean hasValidCriterion = argumentTokens.length >= 2
                && argumentTokens[0].equals("/by")
                && argumentTokens[1].equals("date");
        if (!hasValidCriterion) {
            throw new RemyException(INVALID_SORT_MESSAGE);
        }

        if (argumentTokens.length == 2) {
            return true;
        }

        boolean hasValidOrder = argumentTokens.length == 4
                && argumentTokens[2].equals("/order")
                && (argumentTokens[3].equals("asc") || argumentTokens[3].equals("desc"));
        if (!hasValidOrder) {
            throw new RemyException(INVALID_SORT_MESSAGE);
        }

        return argumentTokens[3].equals("asc");
    }

    /**
     * Parses a task index for a mark, unmark, or delete command.
     *
     * @param message user message containing the task index
     * @param commandType type of indexed command
     * @return the one-based task index supplied by the user
     * @throws RemyException if the index is missing or is not an integer
     */
    private int parseTaskIndex(String message, CommandType commandType) {
        assert commandType == CommandType.MARK || commandType == CommandType.UNMARK
                || commandType == CommandType.DELETE
                : "Task index parsing requires an indexed command";

        return switch (commandType) {
            case MARK -> parseTaskIndex(message, MARK_COMMAND,
                    "you forgot which task to mark as done -_-.", "you have to put an integer :0");
            case UNMARK -> parseTaskIndex(message, UNMARK_COMMAND,
                    "you forgot which task to unmark as undone -_-.", "you have to put an integer :0");
            case DELETE -> parseTaskIndex(message, DELETE_COMMAND,
                    "You forgot which task to delete -_-.", "You have to put an integer :0");
            default -> throw new IllegalArgumentException("Command does not contain a task index: " + commandType);
        };
    }

    /**
     * Parses an indexed command's integer argument.
     *
     * @param message user message containing the index
     * @param commandKeyword command keyword before the index
     * @param missingIndexMessage error message for an omitted index
     * @param invalidIndexMessage error message for a non-integer index
     * @return parsed one-based task index
     */
    private int parseTaskIndex(String message, String commandKeyword, String missingIndexMessage,
            String invalidIndexMessage) {
        if (message.length() == commandKeyword.length()) {
            throw new RemyException(missingIndexMessage);
        }

        String index = message.substring(commandKeyword.length() + 1);
        if (!index.matches("[0-9]+")) {
            throw new RemyException(invalidIndexMessage);
        }

        try {
            return Integer.parseInt(index);
        } catch (NumberFormatException e) {
            throw new RemyException(invalidIndexMessage);
        }
    }

    /**
     * Parses a task-creation command and creates the requested task.
     *
     * @param message user message containing a task description and details
     * @param commandType type of task to create
     * @return newly created task
     * @throws RemyException if required task details are invalid or missing
     */
    private Task parseTask(String message, CommandType commandType) {
        assert commandType == CommandType.TODO || commandType == CommandType.DEADLINE
                || commandType == CommandType.EVENT
                : "Task parsing requires a task-creation command";

        return switch (commandType) {
            case TODO -> parseTodo(message);
            case DEADLINE -> parseDeadline(message);
            case EVENT -> parseEvent(message);
            default -> throw new IllegalArgumentException("Command does not create a task: " + commandType);
        };
    }

    /**
     * Parses a todo command.
     *
     * @param message user message containing a todo description
     * @return a todo task
     */
    private Todo parseTodo(String message) {
        if (message.length() == TODO_COMMAND.length()) {
            throw RemyException.createForMissingTodoDescription();
        }

        String description = message.substring(TODO_COMMAND.length() + 1);
        return new Todo(description);
    }

    /**
     * Parses a deadline command.
     *
     * @param message user message containing a deadline description and date
     * @return a deadline task
     */
    private Deadline parseDeadline(String message) {
        String[] commandTokens = message.split(" ");
        int delimiterCount = countToken(commandTokens, DEADLINE_DELIMITER);
        if (delimiterCount > 1 || hasUnexpectedParameter(commandTokens, DEADLINE_DELIMITER)) {
            throw new RemyException(INVALID_DEADLINE_COMMAND_MESSAGE);
        }

        int delimiterIndex = findTokenIndex(commandTokens, DEADLINE_DELIMITER);
        if (delimiterIndex == -1) {
            boolean hasDescription = commandTokens.length > 1;
            throw RemyException.createForMissingDeadlineDetails(hasDescription, false);
        }

        String description = joinTokens(commandTokens, 1, delimiterIndex);
        String deadline = joinTokens(commandTokens, delimiterIndex + 1, commandTokens.length);
        boolean isMissingDescription = description.isEmpty();
        boolean isMissingDeadline = deadline.isEmpty();
        if (isMissingDescription || isMissingDeadline) {
            throw RemyException.createForMissingDeadlineDetails(
                    !isMissingDescription, !isMissingDeadline);
        }

        return createDeadline(description, deadline);
    }

    /**
     * Creates a deadline from validated command details.
     *
     * @param description deadline description
     * @param deadline deadline endpoint to parse
     * @return a deadline containing the parsed endpoint
     * @throws RemyException if the endpoint has an unsupported date format
     */
    private Deadline createDeadline(String description, String deadline) {
        LocalDateTime deadlineDateTime = DateParser.parseDateTime(deadline);
        if (deadlineDateTime != null) {
            return new Deadline(description, deadlineDateTime);
        }

        LocalDate deadlineDate = DateParser.parseDate(deadline);
        if (deadlineDate != null) {
            return new Deadline(description, deadlineDate);
        }

        throw new RemyException(INVALID_DEADLINE_DATE_MESSAGE);
    }

    /**
     * Parses an event command.
     *
     * @param message user message containing an event description and endpoints
     * @return an event task
     */
    private Event parseEvent(String message) {
        String[] commandTokens = message.split(" ");
        int startDelimiterCount = countToken(commandTokens, EVENT_START_DELIMITER);
        int endDelimiterCount = countToken(commandTokens, EVENT_END_DELIMITER);
        boolean hasRepeatedParameter = startDelimiterCount > 1 || endDelimiterCount > 1;
        if (hasRepeatedParameter || hasUnexpectedParameter(
                commandTokens, EVENT_START_DELIMITER, EVENT_END_DELIMITER)) {
            throw new RemyException(INVALID_EVENT_COMMAND_MESSAGE);
        }

        int startDelimiterIndex = findTokenIndex(commandTokens, EVENT_START_DELIMITER);
        int endDelimiterIndex = findTokenIndex(commandTokens, EVENT_END_DELIMITER);
        if (startDelimiterIndex == -1 || endDelimiterIndex == -1) {
            int descriptionEndIndex = findFirstParameterIndex(commandTokens);
            boolean hasDescription = descriptionEndIndex > 1;
            throw RemyException.createForMissingEventDetails(
                    hasDescription, startDelimiterIndex != -1, endDelimiterIndex != -1);
        }
        if (startDelimiterIndex > endDelimiterIndex) {
            throw new RemyException(INVALID_EVENT_COMMAND_MESSAGE);
        }

        String description = joinTokens(commandTokens, 1, startDelimiterIndex);
        String start = joinTokens(commandTokens, startDelimiterIndex + 1, endDelimiterIndex);
        String end = joinTokens(commandTokens, endDelimiterIndex + 1, commandTokens.length);
        boolean isMissingDescription = description.isEmpty();
        boolean isMissingStart = start.isEmpty();
        boolean isMissingEnd = end.isEmpty();
        if (isMissingDescription || isMissingStart || isMissingEnd) {
            throw RemyException.createForMissingEventDetails(
                    !isMissingDescription, !isMissingStart, !isMissingEnd);
        }

        return createEvent(description, start, end);
    }

    /**
     * Counts exact occurrences of a parameter token.
     *
     * @param commandTokens command split on its single spaces
     * @param parameter parameter token to count
     * @return number of exact parameter occurrences
     */
    private int countToken(String[] commandTokens, String parameter) {
        int count = 0;
        for (String token : commandTokens) {
            if (token.equals(parameter)) {
                count++;
            }
        }
        return count;
    }

    /**
     * Returns the index of an exact parameter token.
     *
     * @param commandTokens command split on its single spaces
     * @param parameter parameter token to find
     * @return token index, or -1 when the parameter is absent
     */
    private int findTokenIndex(String[] commandTokens, String parameter) {
        for (int index = 0; index < commandTokens.length; index++) {
            if (commandTokens[index].equals(parameter)) {
                return index;
            }
        }
        return -1;
    }

    /**
     * Returns the index of the first parameter-like token.
     *
     * @param commandTokens command split on its single spaces
     * @return first parameter index, or the token count when no parameter exists
     */
    private int findFirstParameterIndex(String[] commandTokens) {
        for (int index = 1; index < commandTokens.length; index++) {
            if (commandTokens[index].startsWith("/")) {
                return index;
            }
        }
        return commandTokens.length;
    }

    /**
     * Returns whether a command contains an unrecognized parameter-like token.
     *
     * @param commandTokens command split on its single spaces
     * @param allowedParameters parameters allowed by the command
     * @return true when a slash-prefixed token is not an allowed parameter
     */
    private boolean hasUnexpectedParameter(String[] commandTokens, String... allowedParameters) {
        for (int index = 1; index < commandTokens.length; index++) {
            String token = commandTokens[index];
            boolean isAllowed = Arrays.asList(allowedParameters).contains(token);
            if (token.startsWith("/") && !isAllowed) {
                return true;
            }
        }
        return false;
    }

    /**
     * Joins a range of command tokens using one space.
     *
     * @param commandTokens command split on its single spaces
     * @param startIndex inclusive starting index
     * @param endIndex exclusive ending index
     * @return joined token range, or an empty string for an empty range
     */
    private String joinTokens(String[] commandTokens, int startIndex, int endIndex) {
        return String.join(" ", Arrays.copyOfRange(commandTokens, startIndex, endIndex));
    }

    /**
     * Creates an event from validated command details.
     *
     * @param description event description
     * @param start event start endpoint to parse
     * @param end event end endpoint to parse
     * @return an event containing the parsed endpoints
     * @throws RemyException if the endpoints do not share a supported date format
     */
    private Event createEvent(String description, String start, String end) {
        LocalDateTime startDateTime = DateParser.parseDateTime(start);
        LocalDateTime endDateTime = DateParser.parseDateTime(end);
        LocalDate startDate = DateParser.parseDate(start);
        LocalDate endDate = DateParser.parseDate(end);
        if (startDateTime != null && endDateTime != null) {
            validateEventOrder(startDateTime.isBefore(endDateTime));
            return new Event(description, startDateTime, endDateTime);
        }
        if (startDate != null && endDate != null) {
            validateEventOrder(startDate.isBefore(endDate));
            return new Event(description, startDate, endDate);
        }

        throw new RemyException(INVALID_EVENT_DATE_MESSAGE);
    }

    /**
     * Ensures that an event starts before it ends.
     *
     * @param isStartBeforeEnd whether the parsed start precedes the parsed end
     * @throws RemyException if the event range is empty or reversed
     */
    private void validateEventOrder(boolean isStartBeforeEnd) {
        if (!isStartBeforeEnd) {
            throw new RemyException(INVALID_EVENT_RANGE_MESSAGE);
        }
    }
}
