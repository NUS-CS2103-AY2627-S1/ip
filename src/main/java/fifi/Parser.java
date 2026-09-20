package fifi;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.util.Arrays;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fifi.command.AddDeadlineCommand;
import fifi.command.AddEventCommand;
import fifi.command.AddTodoCommand;
import fifi.command.Command;
import fifi.command.DeleteCommand;
import fifi.command.ExitCommand;
import fifi.command.FindCommand;
import fifi.command.ListCommand;
import fifi.command.MarkCommand;
import fifi.command.ShowCommand;
import fifi.command.StatisticsCommand;
import fifi.command.UnmarkCommand;
import fifi.exception.InvalidCommandException;
import fifi.exception.InvalidDescriptionException;
import fifi.task.Deadline;
import fifi.task.Event;
import fifi.task.Task;
import fifi.task.ToDo;

/**
 * Converts raw user input into commands, task numbers, and task objects.
 */
public class Parser {
    private static final String DEADLINE_DATE_MARKER = "/by";
    private static final String EVENT_START_MARKER = "/from";
    private static final String EVENT_END_MARKER = "/to";

    private static final DateTimeFormatter INPUT_DATE_FORMATTER =
            DateTimeFormatter.ofPattern("uuuu-MM-dd").withResolverStyle(ResolverStyle.STRICT);
    private static final DateTimeFormatter DISPLAY_DATE_FORMATTER =
            DateTimeFormatter.ofPattern("MMM dd yyyy", Locale.ENGLISH);

    /**
     * Converts the user's full input into an executable command.
     *
     * @param input the full line typed by the user
     * @return the command represented by the input
     * @throws InvalidCommandException if the command word is not recognized
     * @throws InvalidDescriptionException if a command description is missing or a task number is not an integer
     */
    public static Command parse(String input) throws InvalidCommandException, InvalidDescriptionException {
        input = input.strip();
        String command = parseCommand(input);
        String arguments = parseArguments(input, command);
        if ((command.equals("list") || command.equals("bye")) && !arguments.isEmpty()) {
            throw new InvalidDescriptionException("Oops! Use " + command + " without any arguments.");
        }
        return switch (command) {
            case "bye" -> new ExitCommand();
            case "list" -> new ListCommand();
            case "mark" -> new MarkCommand(parseTaskIndex(arguments, command));
            case "unmark" -> new UnmarkCommand(parseTaskIndex(arguments, command));
            case "todo" -> new AddTodoCommand(parseToDo(arguments));
            case "deadline" -> new AddDeadlineCommand(parseDeadline(arguments));
            case "event" -> new AddEventCommand(parseEvent(arguments));
            case "delete" -> new DeleteCommand(parseTaskIndex(arguments, command));
            case "show" -> new ShowCommand(parseDateArgument(arguments));
            case "find" -> new FindCommand(parseFindKeyword(arguments));
            case "stats" -> parseStatistics(arguments);
            default -> throw new InvalidCommandException("""
                        UhOh, this command is invalid, please enter a valid one!
                        Valid commands include "list, todo, event, deadline, mark, unmark, delete, show, find, stats"\
                        """);
        };
    }

    /**
     * Converts a date string into a {@code LocalDate}.
     *
     * @param input the date string in yyyy-MM-dd format
     * @return the date represented by the input
     * @throws DateTimeException if the input does not match the required format
     */
    public static LocalDate parseDate(String input) throws DateTimeException {
        if (!input.matches("[0-9]{4}-[0-9]{2}-[0-9]{2}") || input.startsWith("0000-")) {
            throw new DateTimeException("Dates must use yyyy-MM-dd with a year from 0001 to 9999.");
        }
        return LocalDate.parse(input, INPUT_DATE_FORMATTER);
    }

    /**
     * Converts the date in a show command into a {@code LocalDate}.
     *
     * @param input the full line typed by the user
     * @return the date requested by the user
     * @throws InvalidDescriptionException if the show date is missing
     * @throws DateTimeException if the date does not match the required format
     */
    public static LocalDate parseShowDate(String input) throws InvalidDescriptionException, DateTimeException {
        input = input.strip();
        if (!parseCommand(input).equals("show")) {
            throw new InvalidDescriptionException("Oops! Use show yyyy-MM-dd.");
        }
        return parseDateArgument(parseArguments(input, "show"));
    }

    private static LocalDate parseDateArgument(String argument) throws InvalidDescriptionException {
        if (argument.isBlank()) {
            throw new InvalidDescriptionException("Oops! You did not provide a date to show");
        }
        return parseDate(argument);
    }

    /**
     * Converts a date back into the format accepted by the chatbot.
     *
     * @param date the date to save
     * @return the saved date string
     */
    public static String formatDateForStorage(LocalDate date) {
        return date.format(INPUT_DATE_FORMATTER);
    }

    /**
     * Converts a date into the format printed to the user.
     *
     * @param date the date to display
     * @return the display date string
     */
    public static String formatDateForDisplay(LocalDate date) {
        return date.format(DISPLAY_DATE_FORMATTER);
    }

    /**
     * Returns the command word from the user's input.
     *
     * @param input the full line typed by the user
     * @return the command word at the start of the input
     */
    private static String parseCommand(String input) {
        return input.split("\\s+", 2)[0];
    }

    private static String parseArguments(String input, String command) {
        // The dispatcher must supply the matching command before this helper removes it.
        assert parseCommand(input).equals(command) : "Argument parsing must use the dispatched command";
        return input.substring(command.length()).strip();
    }

    /**
     * Returns the zero-based task index from a command such as {@code mark 1}.
     *
     * @param arguments the text following the command word
     * @return the zero-based task index
     * @throws InvalidDescriptionException if the task number is missing or is not a valid integer
     */
    private static int parseTaskIndex(String arguments, String command) throws InvalidDescriptionException {
        try {
            if (!arguments.matches("[0-9]+")) {
                throw new NumberFormatException("A task number must contain digits only.");
            }
            int taskNumber = Integer.parseInt(arguments);
            if (taskNumber < 1) {
                throw new NumberFormatException("A task number must be positive.");
            }
            return taskNumber - 1;
        } catch (NumberFormatException e) {
            // Convert malformed user input into an error handled by both console and GUI callers.
            throw new InvalidDescriptionException(
                    "Oops! Please enter a task number after " + command + ", e.g. " + command + " 1.");
        }
    }

    /**
     * Returns the keyword from a find command.
     *
     * @param arguments the text following the find command
     * @return the keyword to find in task descriptions
     * @throws InvalidDescriptionException if the keyword is missing
     */
    private static String parseFindKeyword(String arguments) throws InvalidDescriptionException {
        if (arguments.isBlank()) {
            throw new InvalidDescriptionException("Oops! You did not provide a keyword to find");
        }
        return arguments;
    }

    private static Command parseStatistics(String arguments) throws InvalidDescriptionException {
        if (arguments.isBlank() || arguments.equals("all")) {
            return new StatisticsCommand();
        }
        String[] parts = arguments.split("\\s+", 2);
        if (parts[0].equals("since") && parts.length == 2 && !parts[1].isBlank()) {
            return new StatisticsCommand(parseDate(parts[1]));
        }
        throw new InvalidDescriptionException(
                "Oops! Use stats, stats all, or stats since yyyy-MM-dd.");
    }

    /**
     * Creates a ToDo task from the user's input.
     *
     * @param arguments the text following the todo command
     * @return the ToDo task described by the arguments
     * @throws InvalidDescriptionException if the task description is missing
     */
    private static Task parseToDo(String arguments) throws InvalidDescriptionException {
        if (arguments.isBlank()) {
            throw new InvalidDescriptionException("Oops! You cannot have an empty todo description");
        }
        validateDescription(arguments);
        return new ToDo(false, arguments);
    }

    /**
     * Creates a deadline task from the user's input.
     *
     * @param arguments the text following the deadline command
     * @return the deadline task described by the arguments
     * @throws InvalidDescriptionException if the deadline description or date is missing
     * @throws DateTimeException if the date does not match the required format
     */
    private static Task parseDeadline(String arguments) throws InvalidDescriptionException, DateTimeException {
        validateParameterTokens(arguments, "deadline", DEADLINE_DATE_MARKER);
        int byIndex = findDateMarker(arguments, DEADLINE_DATE_MARKER);
        if (byIndex == -1
                || arguments.substring(byIndex + DEADLINE_DATE_MARKER.length()).trim().isEmpty()) {
            throw new InvalidDescriptionException("Oops! You did not provide a date for the deadline");
        }

        String description = arguments.substring(0, byIndex).trim();
        if (description.isBlank()) {
            throw new InvalidDescriptionException("Oops! You cannot have an empty deadline description");
        }
        validateDescription(description);

        String deadlineDate = arguments.substring(byIndex + DEADLINE_DATE_MARKER.length()).trim();
        return new Deadline(false, description, parseDate(deadlineDate));
    }

    /**
     * Creates an event task from the user's input.
     *
     * @param arguments the text following the event command
     * @return the event task described by the arguments
     * @throws InvalidDescriptionException if the event description, start date, or end date is missing
     */
    private static Task parseEvent(String arguments) throws InvalidDescriptionException {
        validateParameterTokens(arguments, "event", EVENT_START_MARKER, EVENT_END_MARKER);
        int fromIndex = findDateMarker(arguments, EVENT_START_MARKER);
        int toIndex = findDateMarker(arguments, EVENT_END_MARKER);
        if (fromIndex != -1 && toIndex != -1 && fromIndex > toIndex) {
            throw new InvalidDescriptionException("Oops! Put /from before /to in an event command.");
        }
        boolean hasEndMarker = toIndex != -1;
        String to = hasEndMarker ? arguments.substring(toIndex + EVENT_END_MARKER.length()).trim() : "";
        if (to.isEmpty()) {
            throw new InvalidDescriptionException("Oops! You did not provide an end date for the event");
        }

        boolean hasOrderedDateMarkers = fromIndex != -1 && fromIndex < toIndex;
        String from = hasOrderedDateMarkers
                ? arguments.substring(fromIndex + EVENT_START_MARKER.length(), toIndex).trim()
                : "";
        if (from.isEmpty()) {
            throw new InvalidDescriptionException("Oops! You did not provide a start date for the event");
        }

        String description = arguments.substring(0, fromIndex).trim();
        if (description.isBlank()) {
            throw new InvalidDescriptionException("Oops! You cannot have an empty event description");
        }
        validateDescription(description);

        LocalDate startDate = parseDate(from);
        LocalDate endDate = parseDate(to);
        if (startDate.isAfter(endDate)) {
            throw new InvalidDescriptionException("Oops! An event's end date must be on or after its start date.");
        }
        return new Event(false, description, startDate, endDate);
    }

    /**
     * Rejects text that cannot be safely stored as one task description.
     */
    private static void validateDescription(String description) throws InvalidDescriptionException {
        if (description.contains("|") || description.codePoints()
                .anyMatch(character -> Character.isISOControl(character) && character != '\t')) {
            throw new InvalidDescriptionException("Oops! Task descriptions cannot contain | or control characters.");
        }
    }

    /**
     * Finds one complete parameter token without matching parts of ordinary words.
     */
    private static int findDateMarker(String arguments, String marker) throws InvalidDescriptionException {
        Matcher matches = Pattern.compile("(?<!\\S)" + Pattern.quote(marker) + "(?=\\s|$)").matcher(arguments);
        if (!matches.find()) {
            return -1;
        }
        int markerIndex = matches.start();
        if (matches.find()) {
            throw new InvalidDescriptionException("Oops! Specify " + marker + " only once.");
        }
        return markerIndex;
    }

    /**
     * Rejects unsupported date parameters and parameters glued to their values.
     */
    private static void validateParameterTokens(String arguments, String command, String... allowedMarkers)
            throws InvalidDescriptionException {
        for (String token : arguments.split("\\s+")) {
            if (token.startsWith(DEADLINE_DATE_MARKER) || token.startsWith(EVENT_START_MARKER)
                    || token.startsWith(EVENT_END_MARKER)) {
                boolean isAllowed = Arrays.asList(allowedMarkers).contains(token);
                if (!isAllowed) {
                    throw new InvalidDescriptionException("Oops! Invalid " + command
                            + " parameter. Use separate /by, /from, or /to tokens as appropriate.");
                }
            }
        }
    }
}
