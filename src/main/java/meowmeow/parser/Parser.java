package meowmeow.parser;

import meowmeow.MeowMeowException;
import meowmeow.task.Deadline;
import meowmeow.task.Event;
import meowmeow.task.TaskDateTime;
import meowmeow.task.Todo;

import java.util.regex.Pattern;

/**
 * Interprets raw user input and converts it into commands and task objects.
 */
public class Parser {

    /**
     * Returns the command type named by the first word of the input.
     *
     * @param input Full line of user input.
     * @return Matching command type, or {@code UNKNOWN} if unrecognised.
     */
    public static CommandType parseCommand(String input) {
        return CommandType.fromString(parseCommandWord(input));
    }

    /**
     * Returns the input split into the command word and the remaining text.
     *
     * @param input Full line of user input.
     * @return One or two elements: the command word, then any argument text.
     */
    private static String[] splitCommandAndArguments(String input) {
        return input.trim().split("\\s+", 2);
    }

    /**
     * Throws if the given delimiter appears more than once in the given text.
     *
     * @param text Text to inspect.
     * @param delimiter Delimiter that may appear at most once.
     * @param example Example of correct usage, appended to the error message.
     * @throws MeowMeowException If the delimiter appears more than once.
     */
    private static void requireAtMostOne(String text, String delimiter, String example)
            throws MeowMeowException {
        if (countOccurrences(text, delimiter) > 1) {
            throw new MeowMeowException("You used " + delimiter + " more than once, "
                    + "so I don't know which one you meant. " + example);
        }
    }

    /**
     * Returns how many times the target appears in the given text.
     *
     * @param text Text to search.
     * @param target Text to count.
     * @return Number of non-overlapping occurrences.
     */
    private static int countOccurrences(String text, String target) {
        return text.split(Pattern.quote(target), -1).length - 1;
    }

    /**
     * Returns the first word of the input exactly as the user typed it.
     *
     * @param input Full line of user input.
     * @return The command word.
     */
    public static String parseCommandWord(String input) {
        return splitCommandAndArguments(input)[0];
    }

    /**
     * Returns everything after the command word, or an empty string if the
     * input was a single word.
     *
     * @param input Full line of user input.
     * @return Trimmed argument text.
     */
    public static String parseArguments(String input) {
        String[] parts = splitCommandAndArguments(input);
        return (parts.length > 1) ? parts[1].trim() : "";
    }

    /**
     * Returns a todo built from the given argument text.
     *
     * @param arguments Text following the todo command word.
     * @return Todo with the given description.
     * @throws MeowMeowException If the description is empty.
     */
    public static Todo parseTodo(String arguments) throws MeowMeowException {
        if (arguments.isEmpty()) {
            throw new MeowMeowException("A todo needs a description. Try: todo borrow book");
        }
        return new Todo(arguments);
    }

    /**
     * Returns a deadline built from the given argument text.
     * Expects a description followed by a due date after the /by delimiter.
     *
     * @param arguments Text following the deadline command word.
     * @return Deadline with the given description and due date.
     * @throws MeowMeowException If the description is missing, the /by
     *         delimiter is absent, appears more than once, or the date cannot be parsed.
     */
    public static Deadline parseDeadline(String arguments) throws MeowMeowException {
        String example = "Try: deadline return book /by 2019-12-02 1800";
        requireAtMostOne(arguments, "/by", example);
        String[] parts = arguments.split("/by", 2);
        String description = parts[0].trim();

        if (description.isEmpty()) {
            throw new MeowMeowException("A deadline needs a description. " + example);
        }
        if (parts.length < 2 || parts[1].trim().isEmpty()) {
            throw new MeowMeowException("I need a due date after /by. " + example);
        }

        TaskDateTime by = TaskDateTime.parse(parts[1].trim());
        if (by.isInThePast()) {
            throw new MeowMeowException("That deadline has already passed (" + by + "). "
                    + "Give me a date in the future.");
        }
        return new Deadline(description, by);
    }

    /**
     * Returns an event built from the given argument text.
     * Expects a description followed by a start time after /from and an end
     * time after /to.
     *
     * @param arguments Text following the event command word.
     * @return Event with the given description, start and end times.
     * @throws MeowMeowException If the description is missing, either
     *         delimiter is absent or repeated, or a time cannot be parsed,
     *         or a time is invalid, or the event starts in the past.
     */
    public static Event parseEvent(String arguments) throws MeowMeowException {
        String example = "Try: event project meeting /from 2019-08-06 1400 /to 2019-08-06 1600";
        requireAtMostOne(arguments, "/from", example);
        String[] fromParts = arguments.split("/from", 2);
        String description = fromParts[0].trim();

        if (description.isEmpty()) {
            throw new MeowMeowException("An event needs a description. " + example);
        }
        if (fromParts.length < 2) {
            throw new MeowMeowException("I need a start time after /from. " + example);
        }

        requireAtMostOne(fromParts[1], "/to", example);
        String[] toParts = fromParts[1].split("/to", 2);
        String fromText = toParts[0].trim();

        if (fromText.isEmpty()) {
            throw new MeowMeowException("The start time after /from is empty. " + example);
        }
        if (toParts.length < 2 || toParts[1].trim().isEmpty()) {
            throw new MeowMeowException("I need an end time after /to. " + example);
        }

        TaskDateTime from = TaskDateTime.parse(fromText);
        TaskDateTime to = TaskDateTime.parse(toParts[1].trim());

        if (from.isInThePast()) {
            throw new MeowMeowException("That event starts in the past (" + from + "). "
                    + "Give me a start time in the future.");
        }

        return new Event(description, from, to);
    }

    /**
     * Returns the date named by the given argument text.
     *
     * @param arguments Text following the command word.
     * @return Parsed date.
     * @throws MeowMeowException If the argument is empty or cannot be parsed.
     */
    public static TaskDateTime parseDate(String arguments) throws MeowMeowException {
        if (arguments.isEmpty()) {
            throw new MeowMeowException("Which date? Try: on 2019-12-02");
        }
        return TaskDateTime.parse(arguments);
    }
}