package goat;

import java.time.LocalDate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Converts raw user input into commands and task values.
 */
public final class Parser {
    private static final Pattern DEADLINE_ARGUMENTS =
            Pattern.compile("^(.*?)\\s*/by(?:\\s+(.*))?$");
    private static final Pattern EVENT_ARGUMENTS =
            Pattern.compile("^(.*?)\\s*/from(?:\\s+(.*?))?\\s*/to(?:\\s+(.*))?$");
    private static final Pattern BY_MARKER = Pattern.compile("(?<!\\S)/by(?!\\S)");
    private static final Pattern FROM_MARKER = Pattern.compile("(?<!\\S)/from(?!\\S)");
    private static final Pattern TO_MARKER = Pattern.compile("(?<!\\S)/to(?!\\S)");

    private Parser() {
    }

    /**
     * Separates a command word from its argument.
     *
     * @param input raw input entered by the user
     * @return command type, command word, and remaining argument
     * @throws GoatException if the input is empty
     */
    public static ParsedCommand parse(String input) throws GoatException {
        String command = input.trim();
        if (command.isEmpty()) {
            throw new GoatException("Please enter a command.");
        }

        String[] parts = command.split("\\s+", 2);
        String commandWord = parts[0];
        String argument = parts.length == 2 ? parts[1].trim() : "";
        return new ParsedCommand(CommandType.from(commandWord), commandWord, argument);
    }

    /**
     * Creates a todo from its command argument.
     *
     * @param argument todo description
     * @return parsed todo
     * @throws GoatException if the description is empty
     */
    public static Todo parseTodo(String argument) throws GoatException {
        if (argument.isEmpty()) {
            throw new GoatException("A todo needs a description. Try: todo <description>");
        }
        return new Todo(normalizeWhitespace(argument));
    }

    /**
     * Creates a deadline from its command argument.
     *
     * @param argument deadline description and due date
     * @return parsed deadline
     * @throws GoatException if required fields are missing or the date is invalid
     */
    public static Deadline parseDeadline(String argument) throws GoatException {
        if (countMatches(BY_MARKER, argument) != 1) {
            throw new GoatException("Use exactly one /by: "
                    + "deadline <description> /by <yyyy-MM-dd>");
        }
        Matcher matcher = DEADLINE_ARGUMENTS.matcher(argument);
        if (!matcher.matches()) {
            throw new GoatException("Use this format: deadline <description> /by <yyyy-MM-dd>");
        }

        String description = matcher.group(1).trim();
        String by = matcher.group(2) == null ? "" : matcher.group(2).trim();
        if (description.isEmpty()) {
            throw new GoatException("A deadline needs a description before /by.");
        }
        if (by.isEmpty()) {
            throw new GoatException("A deadline needs a date after /by.");
        }
        return new Deadline(normalizeWhitespace(description),
                TaskDate.parse(by, "The deadline date"));
    }

    /**
     * Creates an event from its command argument.
     *
     * @param argument event description and date range
     * @return parsed event
     * @throws GoatException if required fields are missing or either date is invalid
     */
    public static Event parseEvent(String argument) throws GoatException {
        if (countMatches(FROM_MARKER, argument) != 1
                || countMatches(TO_MARKER, argument) != 1) {
            throw new GoatException("Use exactly one /from and one /to: "
                    + "event <description> /from <start date> /to <end date>");
        }
        Matcher matcher = EVENT_ARGUMENTS.matcher(argument);
        if (!matcher.matches()) {
            throw new GoatException(
                    "Use this format: event <description> /from <start date> /to <end date>");
        }

        String description = matcher.group(1).trim();
        String from = matcher.group(2) == null ? "" : matcher.group(2).trim();
        String to = matcher.group(3) == null ? "" : matcher.group(3).trim();
        if (description.isEmpty()) {
            throw new GoatException("An event needs a description before /from.");
        }
        if (from.isEmpty()) {
            throw new GoatException("An event needs a start date after /from.");
        }
        if (to.isEmpty()) {
            throw new GoatException("An event needs an end date after /to.");
        }
        LocalDate startDate = TaskDate.parse(from, "The event start date");
        LocalDate endDate = TaskDate.parse(to, "The event end date");
        TaskDate.requireStartBeforeEnd(startDate, endDate, "An event");
        return new Event(normalizeWhitespace(description), startDate, endDate);
    }

    /**
     * Validates and returns a keyword used to search task descriptions.
     *
     * @param argument search keyword
     * @return validated keyword
     * @throws GoatException if the keyword is empty
     */
    public static String parseKeyword(String argument) throws GoatException {
        if (argument.isEmpty()) {
            throw new GoatException("Please specify a keyword. Try: find <keyword>");
        }
        return argument;
    }

    /**
     * Rejects trailing arguments for commands that do not accept them.
     *
     * @param command parsed command to validate
     * @throws GoatException if the command has an argument
     */
    public static void requireNoArgument(ParsedCommand command) throws GoatException {
        if (!command.argument().isEmpty()) {
            throw unknownCommandException();
        }
    }

    /**
     * Creates the guidance shown for an unsupported command.
     *
     * @return exception listing the supported commands
     */
    public static GoatException unknownCommandException() {
        return new GoatException("I don't recognise that command. Try todo, deadline, event, "
                + "list, find, sort, mark, unmark, delete, or bye.");
    }

    /**
     * Counts standalone occurrences of a command marker.
     */
    private static long countMatches(Pattern marker, String argument) {
        return marker.matcher(argument).results().count();
    }

    /**
     * Treats repeated whitespace in descriptions as an accidental formatting difference.
     */
    private static String normalizeWhitespace(String text) {
        return text.trim().replaceAll("\\s+", " ");
    }
}
