package avon.parser;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import avon.command.AddCommand;
import avon.command.Command;
import avon.command.CommandType;
import avon.command.DeleteCommand;
import avon.command.ExitCommand;
import avon.command.FindCommand;
import avon.command.HelpCommand;
import avon.command.ListCommand;
import avon.command.MarkCommand;
import avon.command.UnmarkCommand;
import avon.exception.AvonException;
import avon.exception.EmptyDescriptionException;
import avon.exception.InvalidTaskFormatException;
import avon.exception.InvalidTaskNumberException;
import avon.task.Deadline;
import avon.task.Event;
import avon.task.Todo;
import avon.util.DateTimeParser;

/**
 * Interprets user commands and converts their arguments into domain objects.
 */
public final class Parser {
    private static final String DEADLINE_DELIMITER = "/by";
    private static final String EVENT_START_DELIMITER = "/from";
    private static final String EVENT_END_DELIMITER = "/to";

    private Parser() {
    }

    /**
     * Parses a complete user instruction into an executable command.
     *
     * @param command the command entered by the user.
     * @return the command object that represents the instruction.
     * @throws AvonException if the command or its arguments are invalid.
     */
    public static Command parse(String command) throws AvonException {
        String normalizedCommand = command.strip();
        CommandType commandType = CommandType.parse(normalizedCommand);
        switch (commandType) {
            case TODO:
                return new AddCommand(parseTodo(normalizedCommand));
            case DEADLINE:
                return new AddCommand(parseDeadline(normalizedCommand));
            case EVENT:
                return new AddCommand(parseEvent(normalizedCommand));
            case LIST:
                return new ListCommand();
            case FIND:
                return new FindCommand(parseFindKeyword(normalizedCommand));
            case MARK:
                return new MarkCommand(parseTaskNumber(normalizedCommand, commandType));
            case UNMARK:
                return new UnmarkCommand(parseTaskNumber(normalizedCommand, commandType));
            case DELETE:
                return new DeleteCommand(parseTaskNumber(normalizedCommand, commandType));
            case HELP:
                return new HelpCommand();
            case BYE:
                return new ExitCommand();
            default:
                throw new IllegalArgumentException("Unsupported command type.");
        }
    }

    /**
     * Parses the one-based task number in a task-number command.
     *
     * @param command the complete command entered by the user.
     * @param commandType the type of task-number command.
     * @return the parsed one-based task number.
     * @throws InvalidTaskNumberException if the number is missing or malformed.
     */
    private static int parseTaskNumber(String command, CommandType commandType)
            throws InvalidTaskNumberException {
        String keyword = commandType.getKeyword();
        String taskNumberText = command.substring(keyword.length()).trim();
        if (taskNumberText.isEmpty()) {
            throw new InvalidTaskNumberException(keyword,
                    "Add a task number after '" + keyword + "'.");
        }

        try {
            return Integer.parseInt(taskNumberText);
        } catch (NumberFormatException exception) {
            throw new InvalidTaskNumberException(keyword,
                    "Use one whole task number greater than zero.");
        }
    }

    /**
     * Extracts the required keyword from a find command.
     *
     * @param command the complete find command.
     * @return the non-empty keyword to search for.
     * @throws EmptyDescriptionException if the command has no keyword.
     */
    private static String parseFindKeyword(String command) throws EmptyDescriptionException {
        String findKeyword = CommandType.FIND.getKeyword();
        return extractDescription(command, findKeyword, "find KEYWORD");
    }

    /**
     * Parses a todo command into a todo task.
     *
     * @param command the todo command entered by the user.
     * @return the parsed todo task.
     * @throws EmptyDescriptionException if the todo description is empty.
     */
    private static Todo parseTodo(String command) throws EmptyDescriptionException {
        String todoKeyword = CommandType.TODO.getKeyword();
        return new Todo(extractDescription(command, todoKeyword, "todo DESCRIPTION"));
    }

    /**
     * Parses a deadline command into a deadline task.
     *
     * @param command the deadline command entered by the user.
     * @return the parsed deadline task.
     * @throws AvonException if required details are missing or the date is invalid.
     */
    private static Deadline parseDeadline(String command) throws AvonException {
        String deadlineKeyword = CommandType.DEADLINE.getKeyword();
        String example = "deadline DESCRIPTION /by yyyy-MM-dd [HHmm]";
        String details = extractDescription(command, deadlineKeyword, example);
        List<Integer> byIndexes = findDelimiterIndexes(details, DEADLINE_DELIMITER);
        if (byIndexes.isEmpty()) {
            throw new InvalidTaskFormatException(deadlineKeyword,
                    "Include '/by' before the deadline date or time.", example);
        }
        if (byIndexes.size() > 1) {
            throw new InvalidTaskFormatException(deadlineKeyword,
                    "Use '/by' exactly once.", example);
        }
        int byIndex = byIndexes.get(0);
        if (byIndex == 0) {
            throw new EmptyDescriptionException(deadlineKeyword, example);
        }

        String description = details.substring(0, byIndex).trim();
        String by = requireTaskDetail(details.substring(byIndex + DEADLINE_DELIMITER.length()).trim(),
                deadlineKeyword, "Add a date or time after '/by'.", example);
        try {
            return new Deadline(description, DateTimeParser.parse(by));
        } catch (DateTimeParseException exception) {
            throw new InvalidTaskFormatException(deadlineKeyword,
                    "Use a real date and optional 24-hour time in yyyy-MM-dd [HHmm] format.",
                    example);
        }
    }

    /**
     * Parses an event command into an event task.
     *
     * @param command the event command entered by the user.
     * @return the parsed event task.
     * @throws AvonException if required details are missing or misplaced.
     */
    private static Event parseEvent(String command) throws AvonException {
        String eventKeyword = CommandType.EVENT.getKeyword();
        String example = "event DESCRIPTION /from yyyy-MM-dd [HHmm] /to yyyy-MM-dd [HHmm]";
        String details = extractDescription(command, eventKeyword, example);
        List<Integer> fromIndexes = findDelimiterIndexes(details, EVENT_START_DELIMITER);
        List<Integer> toIndexes = findDelimiterIndexes(details, EVENT_END_DELIMITER);
        validateEventDelimiters(fromIndexes, toIndexes, eventKeyword, example);

        int fromIndex = fromIndexes.get(0);
        int toIndex = toIndexes.get(0);
        String description = details.substring(0, fromIndex).trim();
        String fromText = requireTaskDetail(
                details.substring(fromIndex + EVENT_START_DELIMITER.length(), toIndex).trim(),
                eventKeyword, "Add a start date or time after '/from'.", example);
        String toText = requireTaskDetail(
                details.substring(toIndex + EVENT_END_DELIMITER.length()).trim(),
                eventKeyword, "Add an end date or time after '/to'.", example);
        LocalDateTime from;
        LocalDateTime to;
        try {
            from = DateTimeParser.parse(fromText);
            to = DateTimeParser.parse(toText);
        } catch (DateTimeParseException exception) {
            throw new InvalidTaskFormatException(eventKeyword,
                    "Use real dates and optional 24-hour times in yyyy-MM-dd [HHmm] format.",
                    example);
        }

        try {
            return new Event(description, from, to);
        } catch (IllegalArgumentException exception) {
            throw new InvalidTaskFormatException(eventKeyword,
                    "Set '/to' to a time later than '/from'.", example);
        }
    }

    /**
     * Ensures that an event has one start delimiter followed by one end delimiter.
     *
     * @param fromIndexes the indexes of all start delimiters.
     * @param toIndexes the indexes of all end delimiters.
     * @param eventKeyword the event command keyword.
     * @param example a complete example of the expected command.
     * @throws AvonException if a delimiter is missing, duplicated, or misplaced.
     */
    private static void validateEventDelimiters(List<Integer> fromIndexes, List<Integer> toIndexes,
            String eventKeyword, String example) throws AvonException {
        if (fromIndexes.isEmpty()) {
            throw new InvalidTaskFormatException(eventKeyword,
                    "Include '/from' before the start date or time.", example);
        }
        if (toIndexes.isEmpty()) {
            throw new InvalidTaskFormatException(eventKeyword,
                    "Include '/to' before the end date or time.", example);
        }
        if (fromIndexes.size() > 1 || toIndexes.size() > 1) {
            throw new InvalidTaskFormatException(eventKeyword,
                    "Use '/from' and '/to' exactly once each.", example);
        }
        int fromIndex = fromIndexes.get(0);
        int toIndex = toIndexes.get(0);
        if (toIndex < fromIndex) {
            throw new InvalidTaskFormatException(eventKeyword,
                    "Place '/from' before '/to'.", example);
        }
        if (fromIndex == 0) {
            throw new EmptyDescriptionException(eventKeyword, example);
        }
    }

    /**
     * Extracts a non-empty task description after a command keyword.
     *
     * @param command the complete command.
     * @param keyword the command keyword to remove.
     * @param example a complete example of the expected command.
     * @return the task description.
     * @throws EmptyDescriptionException if no description follows the keyword.
     */
    private static String extractDescription(String command, String keyword, String example)
            throws EmptyDescriptionException {
        String description = command.substring(keyword.length()).trim();
        if (description.isEmpty()) {
            throw new EmptyDescriptionException(keyword, example);
        }
        return description;
    }

    /**
     * Ensures that a parsed command component is not empty.
     *
     * @param value the component to validate.
     * @param taskType the type of task being entered.
     * @param problem the explanation shown if the component is empty.
     * @param example a complete example of the expected command.
     * @return the original non-empty component.
     * @throws InvalidTaskFormatException if the component is empty.
     */
    private static String requireTaskDetail(String value, String taskType, String problem,
            String example) throws InvalidTaskFormatException {
        if (value.isEmpty()) {
            throw new InvalidTaskFormatException(taskType, problem, example);
        }
        return value;
    }

    /**
     * Finds delimiters that appear as complete whitespace-separated tokens.
     *
     * @param value the command details to inspect.
     * @param delimiter the delimiter token to find.
     * @return the starting indexes of every complete delimiter token.
     */
    private static List<Integer> findDelimiterIndexes(String value, String delimiter) {
        Pattern delimiterPattern = Pattern.compile(
                "(?<!\\S)" + Pattern.quote(delimiter) + "(?=\\s|$)");
        Matcher matcher = delimiterPattern.matcher(value);
        List<Integer> indexes = new ArrayList<>();
        while (matcher.find()) {
            indexes.add(matcher.start());
        }
        return indexes;
    }

}
