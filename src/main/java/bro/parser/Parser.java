package bro.parser;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import bro.command.Command;
import bro.exception.BroException;
import bro.task.TaskDateTime;

/**
 * Handles parsing and interpreting raw user commands and arguments.
 */
public class Parser {
    private static final String ERROR_EMPTY_TODO =
            "Sorry bro, you can't have an empty todo.\nFormat: todo <description> (e.g. todo read book)";
    private static final String ERROR_EMPTY_DEADLINE =
            "Sorry bro, you can't have an empty deadline.\n"
                    + "Format: deadline <description> /by <deadline> "
                    + "(e.g. deadline return book /by 2026-10-15 1800)";
    private static final String ERROR_INVALID_DEADLINE_FORMAT =
            "I think you forgot to add the deadline bro.\n"
                    + "Format: deadline <description> /by <deadline> "
                    + "(e.g. deadline return book /by 2026-10-15 1800)";
    private static final String ERROR_EMPTY_EVENT =
            "Sorry bro, you can't have an empty event.\n"
                    + "Format: event <description> /from <start> /to <end> "
                    + "(e.g. event camp /from 2026-10-10 /to 2026-10-12)";
    private static final String ERROR_INVALID_EVENT_FORMAT =
            "I think you messed up the event format bro.\n"
                    + "Format: event <description> /from <start> /to <end> "
                    + "(e.g. event camp /from 2026-10-10 /to 2026-10-12)";
    private static final String ERROR_EMPTY_FIND_KEYWORDS =
            "Bro, what are you trying to find? Please provide some keywords.\n"
                    + "Format: find <keyword1>, <keyword2> (e.g. find book, project)";
    private static final String ERROR_EMPTY_EDIT_INDEX =
            "Bro, which task do you want to edit?\nFormat: edit <number> /desc <new description>";
    private static final String ERROR_MISSING_EDIT_FLAGS =
            "Bro, please specify what you want to edit using /desc, /by, /from, or /to.";
    private static final String ERROR_EMPTY_EDIT_VALUE =
            "Bro, you can't leave the edit value empty.";
    private static final String ERROR_UNKNOWN_EDIT_FLAG =
            "Bro, I didn't recognize that flag. Use /desc, /by, /from, or /to.";

    private static final Pattern DEADLINE_PATTERN = Pattern
            .compile("(?<description>.+?)\\s+/by\\s+(?<deadline>.+)");
    private static final Pattern EVENT_PATTERN = Pattern
            .compile("(?<description>.+?)\\s+/from\\s+(?<start>.+?)\\s+/to\\s+(?<end>.+)");
    private static final Pattern EDIT_FLAG_PATTERN = Pattern
            .compile("(?<=\\s)/(?<flag>desc|by|from|to)(?=\\s|$)");
    private static final Pattern BY_FLAG_PATTERN = Pattern
            .compile("(?<=\\s)/by(?=\\s|$)");
    private static final Pattern FROM_FLAG_PATTERN = Pattern
            .compile("(?<=\\s)/from(?=\\s|$)");
    private static final Pattern TO_FLAG_PATTERN = Pattern
            .compile("(?<=\\s)/to(?=\\s|$)");

    /**
     * Constructs a Parser instance.
     */
    public Parser() {
    }

    /**
     * Parses the raw input string into a {@link ParsedInput} containing both
     * the Command type and the argument string in a single operation.
     *
     * @param fullCommand The raw input line entered by the user.
     * @return A ParsedInput containing the Command and arguments.
     */
    public static ParsedInput parse(String fullCommand) {
        if (fullCommand == null || fullCommand.trim().isEmpty()) {
            return new ParsedInput(Command.UNKNOWN, "");
        }
        String[] parts = fullCommand.trim().split(" ", 2);
        Command command = Command.fromString(parts[0]);
        String arguments = parts.length > 1 ? parts[1].trim() : "";
        return new ParsedInput(command, arguments);
    }

    /**
     * Parses the raw input string to determine the Command type.
     *
     * @param fullCommand The raw input line entered by the user.
     * @return The corresponding Command enum constant.
     */
    public static Command parseCommand(String fullCommand) {
        Command command = parse(fullCommand).command();

        assert command != null : "Command.fromString should never return null";

        return command;
    }

    /**
     * Parses the raw input string to extract the arguments string.
     *
     * @param fullCommand The raw input line entered by the user.
     * @return The argument string, or an empty string if no arguments were provided.
     */
    public static String parseArguments(String fullCommand) {
        return parse(fullCommand).arguments();
    }

    /**
     * Parses a 1-based task index from the arguments into a 0-based array index.
     *
     * @param arguments    The argument string containing the task index.
     * @param emptyMessage The error message thrown if arguments is empty.
     * @return The 0-based integer index.
     * @throws BroException          If arguments is empty.
     * @throws NumberFormatException If arguments cannot be parsed as an integer.
     */
    public static int parseTaskIndex(String arguments, String emptyMessage) throws BroException {
        if (arguments == null || arguments.trim().isEmpty()) {
            throw new BroException(emptyMessage);
        }
        return Integer.parseInt(arguments.trim()) - 1;
    }

    /**
     * Validates that the input string does not contain storage delimiters or newlines.
     *
     * @param input The string to validate.
     * @throws BroException If input contains '|', '\n', or '\r'.
     */
    private static void validateNoDelimiterOrNewline(String input) throws BroException {
        if (input == null) {
            return;
        }
        if (input.contains("|")) {
            throw new BroException("Bro, task descriptions and dates can't have the '|' character "
                    + "because that's how I save your tasks.");
        }
        if (input.contains("\n") || input.contains("\r")) {
            throw new BroException("Bro, task descriptions and dates can't have newline characters.");
        }
    }

    /**
     * Parses arguments for a todo task.
     *
     * @param arguments The description argument.
     * @return The trimmed description string.
     * @throws BroException If arguments is empty or contains invalid characters.
     */
    public static String parseTodoDescription(String arguments) throws BroException {
        if (arguments == null || arguments.trim().isEmpty()) {
            throw new BroException(ERROR_EMPTY_TODO);
        }
        String description = arguments.trim();
        validateNoDelimiterOrNewline(description);
        return description;
    }

    /**
     * Parses arguments for a deadline task into a {@link DeadlineDetails} record.
     *
     * @param arguments The raw arguments string.
     * @return A DeadlineDetails record containing description and deadline.
     * @throws BroException If arguments is empty, the format is invalid, or contains duplicate flags.
     */
    public static DeadlineDetails parseDeadlineDetails(String arguments) throws BroException {
        if (arguments == null || arguments.trim().isEmpty()) {
            throw new BroException(ERROR_EMPTY_DEADLINE);
        }
        String trimmed = arguments.trim();
        validateNoDelimiterOrNewline(trimmed);

        Matcher byFlagMatcher = BY_FLAG_PATTERN.matcher(" " + trimmed);
        int byCount = 0;
        while (byFlagMatcher.find()) {
            byCount++;
        }
        if (byCount > 1) {
            throw new BroException("Bro, you specified the '/by' flag more than once, you only need one.\n"
                    + "Format: deadline <description> /by <deadline>");
        }

        Matcher matcher = DEADLINE_PATTERN.matcher(trimmed);
        if (!matcher.find()) {
            throw new BroException(ERROR_INVALID_DEADLINE_FORMAT);
        }
        String description = matcher.group("description").trim();
        String deadline = matcher.group("deadline").trim();
        if (description.isEmpty() || deadline.isEmpty()) {
            throw new BroException(ERROR_INVALID_DEADLINE_FORMAT);
        }
        return new DeadlineDetails(description, deadline);
    }

    /**
     * Parses arguments for a deadline task into description and deadline date/time.
     *
     * @param arguments The raw arguments string.
     * @return A 2-element array containing [description, deadlineStr].
     * @throws BroException If arguments is empty or the format is invalid.
     */
    public static String[] parseDeadlineArguments(String arguments) throws BroException {
        DeadlineDetails details = parseDeadlineDetails(arguments);
        String[] detailsArray = new String[] { details.description(), details.deadline() };

        assert detailsArray.length == 2 && !detailsArray[0].isBlank() && !detailsArray[1].isBlank()
                : "Deadline details array must contain exactly 2 non-blank parts";

        return detailsArray;
    }

    /**
     * Parses arguments for an event task into an {@link EventDetails} record.
     *
     * @param arguments The raw arguments string.
     * @return An EventDetails record containing description, start, and end.
     * @throws BroException If arguments is empty, the format is invalid, or contains duplicate flags.
     */
    public static EventDetails parseEventDetails(String arguments) throws BroException {
        if (arguments == null || arguments.trim().isEmpty()) {
            throw new BroException(ERROR_EMPTY_EVENT);
        }
        String trimmed = arguments.trim();
        validateNoDelimiterOrNewline(trimmed);

        Matcher fromFlagMatcher = FROM_FLAG_PATTERN.matcher(" " + trimmed);
        int fromCount = 0;
        while (fromFlagMatcher.find()) {
            fromCount++;
        }
        if (fromCount > 1) {
            throw new BroException("Bro, you specified the '/from' flag more than once, you only need one.\n"
                    + "Format: event <description> /from <start> /to <end>");
        }

        Matcher toFlagMatcher = TO_FLAG_PATTERN.matcher(" " + trimmed);
        int toCount = 0;
        while (toFlagMatcher.find()) {
            toCount++;
        }
        if (toCount > 1) {
            throw new BroException("Bro, you specified the '/to' flag more than once, you only need one.\n"
                    + "Format: event <description> /from <start> /to <end>");
        }

        Matcher matcher = EVENT_PATTERN.matcher(trimmed);
        if (!matcher.find()) {
            throw new BroException(ERROR_INVALID_EVENT_FORMAT);
        }
        String description = matcher.group("description").trim();
        String start = matcher.group("start").trim();
        String end = matcher.group("end").trim();
        if (description.isEmpty() || start.isEmpty() || end.isEmpty()) {
            throw new BroException(ERROR_INVALID_EVENT_FORMAT);
        }
        return new EventDetails(description, start, end);
    }

    /**
     * Parses arguments for an event task into description, start date/time, and end date/time.
     *
     * @param arguments The raw arguments string.
     * @return A 3-element array containing [description, startStr, endStr].
     * @throws BroException If arguments is empty or the format does not match the event pattern.
     */
    public static String[] parseEventArguments(String arguments) throws BroException {
        EventDetails details = parseEventDetails(arguments);
        return new String[] { details.description(), details.start(), details.end() };
    }

    /**
     * Parses a query date string for searching tasks.
     *
     * @param arguments The argument string containing the target date.
     * @return The parsed LocalDate.
     * @throws BroException If arguments is empty or invalid.
     */
    public static LocalDate parseQueryDate(String arguments) throws BroException {
        return TaskDateTime.parseQueryDate(arguments);
    }

    /**
     * Parses the arguments for the find command into an array of keywords delimited by commas.
     *
     * @param arguments The raw arguments string containing comma-separated keywords.
     * @return An array of trimmed, non-empty keyword strings.
     * @throws BroException If arguments is null, empty, or contains only whitespace/commas.
     */
    public static String[] parseFindKeywords(String arguments) throws BroException {
        if (arguments == null || arguments.trim().isEmpty()) {
            throw new BroException(ERROR_EMPTY_FIND_KEYWORDS);
        }
        String[] rawKeywords = arguments.split(",");
        ArrayList<String> validKeywords = new ArrayList<>();
        for (String rawKeyword : rawKeywords) {
            String trimmed = rawKeyword.trim();
            if (!trimmed.isEmpty()) {
                validKeywords.add(trimmed);
            }
        }
        if (validKeywords.isEmpty()) {
            throw new BroException(ERROR_EMPTY_FIND_KEYWORDS);
        }

        // validKeywords should not be empty if no exception was thrown
        assert validKeywords.size() > 0 : "Parsed keywords list should have at least 1 keyword";

        return validKeywords.toArray(new String[0]);
    }

    /**
     * Parses the arguments for the edit command into an {@link EditDetails} record.
     *
     * @param arguments The raw arguments string containing task index and edit flags.
     * @return The parsed EditDetails record.
     * @throws BroException          If arguments are missing, no flags are provided, or flag values are empty.
     * @throws NumberFormatException If the task index cannot be parsed as an integer.
     */
    public static EditDetails parseEditDetails(String arguments) throws BroException {
        if (arguments == null || arguments.trim().isEmpty()) {
            throw new BroException(ERROR_EMPTY_EDIT_INDEX);
        }

        String trimmed = arguments.trim();
        validateNoDelimiterOrNewline(trimmed);

        String[] parts = trimmed.split("\\s+", 2);
        int taskIndex = parseTaskIndex(parts[0], ERROR_EMPTY_EDIT_INDEX);

        if (parts.length < 2 || parts[1].trim().isEmpty()) {
            throw new BroException(ERROR_MISSING_EDIT_FLAGS);
        }

        String remaining = " " + parts[1].trim();
        Matcher matcher = EDIT_FLAG_PATTERN.matcher(remaining);

        ArrayList<Integer> matchStarts = new ArrayList<>();
        ArrayList<Integer> matchEnds = new ArrayList<>();
        ArrayList<String> flags = new ArrayList<>();

        while (matcher.find()) {
            matchStarts.add(matcher.start());
            matchEnds.add(matcher.end());
            flags.add(matcher.group("flag"));
        }

        if (flags.isEmpty()) {
            throw new BroException(ERROR_MISSING_EDIT_FLAGS);
        }

        if (!remaining.substring(0, matchStarts.get(0)).trim().isEmpty()) {
            throw new BroException(ERROR_UNKNOWN_EDIT_FLAG);
        }

        String newDescription = null;
        String newBy = null;
        String newFrom = null;
        String newTo = null;

        HashSet<String> seenFlags = new HashSet<>();
        for (int i = 0; i < flags.size(); i++) {
            String flag = flags.get(i);
            if (!seenFlags.add(flag)) {
                throw new BroException(String.format(
                        "Bro, you specified the '/%s' flag more than once, you only need one.", flag));
            }
            int valStart = matchEnds.get(i);
            int valEnd = (i + 1 < matchStarts.size()) ? matchStarts.get(i + 1) : remaining.length();
            String value = remaining.substring(valStart, valEnd).trim();

            if (value.isEmpty()) {
                throw new BroException(ERROR_EMPTY_EDIT_VALUE);
            }

            switch (flag) {
                case "desc" -> newDescription = value;
                case "by" -> newBy = value;
                case "from" -> newFrom = value;
                case "to" -> newTo = value;
                default -> { }
            }
        }

        return new EditDetails(taskIndex, newDescription, newBy, newFrom, newTo);
    }
}
