package ev;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Locale;

import ev.task.Deadline;
import ev.task.Event;
import ev.task.Task;
import ev.task.Todo;

/**
 * Parses raw user input into the commands and tasks E.V. works with.
 */
public class Parser {
    private static final DateTimeFormatter INPUT_FORMAT =
            DateTimeFormatter.ofPattern("uuuu-MM-dd HHmm", Locale.ENGLISH)
                    .withResolverStyle(ResolverStyle.STRICT);

    private static final String STORAGE_SEPARATOR = "|";

    /** Hides the implicit public constructor; Parser is never instantiated. */
    private Parser() {
    }

    /**
     * Returns the command named by the first word of the given input.
     *
     * @param input the full line entered by the user.
     * @return the command matching the first word.
     * @throws EvException if the input is empty or names no known command.
     */
    public static Command parseCommand(String input) throws EvException {
        String trimmed = input.trim();

        if (trimmed.isEmpty()) {
            throw new EvException("No command entered.");
        }

        return Command.parseKeyword(trimmed.split("\\s+", 2)[0]);
    }

    /**
     * Returns the text following the command word, or an empty string if there is none.
     *
     * @param input the full line entered by the user.
     * @return the trimmed argument text.
     */
    public static String parseArguments(String input) {
        String[] words = input.trim().split("\\s+", 2);
        return words.length > 1 ? words[1].trim() : "";
    }

    /**
     * Checks that a command which takes no arguments was given none. Silently ignoring
     * the extra text would leave the user believing the words they typed had an effect.
     *
     * @param commandWord the keyword the user typed, used in the error message.
     * @param arguments the text following the command word.
     * @throws EvException if any argument text was given.
     */
    public static void requireNoArguments(String commandWord, String arguments) throws EvException {
        if (!arguments.isEmpty()) {
            throw new EvException("The " + commandWord + " command takes no extra words.");
        }
    }

    /**
     * Converts the argument of a mark, unmark or delete command into a task index.
     *
     * @param arguments the text following the command word.
     * @param taskCount the number of tasks currently in the registry.
     * @return zero-based index of the task.
     * @throws EvException if the argument is not a number or is not an existing task number.
     */
    public static int parseTaskIndex(String arguments, int taskCount) throws EvException {
        int taskNumber;

        try {
            taskNumber = Integer.parseInt(arguments);
        } catch (NumberFormatException e) {
            throw new EvException("Task number must be a number.");
        }

        if (taskNumber < 1 || taskNumber > taskCount) {
            throw new EvException("No task with that number.");
        }

        int index = taskNumber - 1;
        assert index >= 0 && index < taskCount : "A validated task index must be within the registry.";

        return index;
    }

    /**
     * Returns a todo built from the given argument text.
     *
     * @param arguments the text following the todo keyword.
     * @return the todo described by the arguments.
     * @throws EvException if no description is given.
     */
    public static Task parseTodo(String arguments) throws EvException {
        if (arguments.isEmpty()) {
            throw new EvException("A todo needs a description.");
        }

        validateDescription(arguments);

        return new Todo(arguments);
    }

    /**
     * Returns a deadline built from the given argument text.
     *
     * @param arguments the text following the deadline keyword.
     * @return the deadline described by the arguments.
     * @throws EvException if the description or the /by time is missing.
     */
    public static Task parseDeadline(String arguments) throws EvException {
        String[] parts = arguments.split(" /by ", 2);

        if (parts.length < 2 || parts[0].isEmpty() || parts[1].isEmpty()) {
            throw new EvException("A deadline needs a description and a /by time.");
        }

        validateDescription(parts[0]);

        return new Deadline(parts[0], parseDateTime(parts[1]));
    }

    /**
     * Returns an event built from the given argument text.
     *
     * @param arguments the text following the event keyword.
     * @return the event described by the arguments.
     * @throws EvException if the description, the /from time or the /to time is missing.
     */
    public static Task parseEvent(String arguments) throws EvException {
        String[] fromParts = arguments.split(" /from ", 2);

        if (fromParts.length < 2 || fromParts[0].isEmpty()) {
            throw new EvException("An event needs a description and a /from time.");
        }

        String[] timeParts = fromParts[1].split(" /to ", 2);

        if (timeParts.length < 2 || timeParts[0].isEmpty() || timeParts[1].isEmpty()) {
            throw new EvException("An event needs a /to time.");
        }

        validateDescription(fromParts[0]);

        LocalDateTime from = parseDateTime(timeParts[0]);
        LocalDateTime to = parseDateTime(timeParts[1]);

        if (to.isBefore(from)) {
            throw new EvException("An event cannot end before it starts.");
        }

        return new Event(fromParts[0], from, to);
    }

    /**
     * Returns the date and time described by text in yyyy-MM-dd HHmm form.
     *
     * @param text the date and time as the user typed it.
     * @return the parsed date and time.
     * @throws EvException if the text is not in the expected format.
     */
    public static LocalDateTime parseDateTime(String text) throws EvException {
        try {
            return LocalDateTime.parse(text, INPUT_FORMAT);
        } catch (DateTimeParseException e) {
            throw new EvException("Dates must look like 2026-09-18 1800.");
        }
    }

    /**
     * Checks that a description can be written to and read back from the data file.
     * The separator character would split the description into extra fields on reload,
     * silently truncating it.
     *
     * @param description the description as the user typed it.
     * @throws EvException if the description contains the storage separator.
     */
    private static void validateDescription(String description) throws EvException {
        if (description.contains(STORAGE_SEPARATOR)) {
            throw new EvException("Descriptions cannot contain '|'. It separates fields in my memory banks.");
        }
    }

    /**
     * Returns the text a find command should search for.
     *
     * @param arguments the text following the find keyword.
     * @return the search text.
     * @throws EvException if no search text is given.
     */
    public static String parseSearchKeyword(String arguments) throws EvException {
        if (arguments.isEmpty()) {
            throw new EvException("Tell me what to search for.");
        }

        return arguments;
    }
}
