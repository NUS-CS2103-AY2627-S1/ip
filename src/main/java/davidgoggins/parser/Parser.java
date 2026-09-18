package davidgoggins.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import davidgoggins.DavidGogginsException;
import davidgoggins.task.Deadline;
import davidgoggins.task.Event;
import davidgoggins.task.Task;
import davidgoggins.task.Todo;

/**
 * Deals with making sense of what the user typed.
 *
 * <p>Turns text into something the rest of the program can act on: a command word, a
 * task number, or a ready-built {@link Task}. Nothing here touches the task list or
 * prints anything, so every rule about what counts as valid input is in this one file.
 */
public class Parser {

    /** A correct todo command, suggested when a todo cannot be read, and shown by help. */
    public static final String TODO_EXAMPLE = "todo read book";

    /** A correct deadline command, suggested when a deadline cannot be read, and shown by help. */
    public static final String DEADLINE_EXAMPLE = "deadline return book /by 2026-09-10";

    /** A correct event command, suggested when an event cannot be read, and shown by help. */
    public static final String EVENT_EXAMPLE = "event project meeting /from 2026-09-10 /to 2026-09-11";

    /** The flag that starts a deadline's due date. */
    private static final String FLAG_BY = "/by";

    /** The flag that starts an event's start date. */
    private static final String FLAG_FROM = "/from";

    /** The flag that starts an event's end date. */
    private static final String FLAG_TO = "/to";

    /**
     * Matches a word that looks like a flag: a slash and letters standing on their own,
     * such as {@code /at}. A path such as {@code /etc/hosts} does not match, since the
     * first part is followed by another slash rather than a space.
     */
    private static final Pattern FLAG_WORD = Pattern.compile("(?<=^|\\s)/[A-Za-z]+(?=\\s|$)");

    /** Matches a whole number, possibly signed, however many digits it has. */
    private static final Pattern WHOLE_NUMBER = Pattern.compile("[+-]?\\d+");

    // Every method below is static because parsing needs no state: the same input
    // always gives the same result, so there is nothing for an instance to remember.

    /**
     * Returns the command word the user typed, lower-cased.
     *
     * <p>Lower-casing here means {@code LIST} and {@code list} are the same command
     * without every caller having to remember it. The description is left alone, so a
     * task keeps the capitalisation the user gave it.
     *
     * @param userInput the line the user typed, already trimmed
     * @return the command word, or an empty string if the line was empty
     */
    public static String parseCommand(String userInput) {
        // Locale.ROOT, because the machine's own locale can change the result: in Turkish,
        // "LIST".toLowerCase() is "lıst" with a dotless i, which is not a command.
        return split(userInput)[0].toLowerCase(Locale.ROOT);
    }

    /**
     * Returns everything the user typed after the command word, trimmed.
     *
     * @param userInput the line the user typed, already trimmed
     * @return the argument, or an empty string if there was none
     */
    public static String parseArgument(String userInput) {
        String[] parts = split(userInput);
        return parts.length > 1 ? parts[1].trim() : "";
    }

    /**
     * Splits the input into the command word and the rest of the line.
     *
     * <p>{@code mark 2} gives {@code "mark"} and {@code "2"}, while a description such
     * as {@code read book} stays intact as a single argument.
     */
    private static String[] split(String userInput) {
        // A leading space would make the command word "" and the real command the
        // argument, so both callers promise to trim the input first.
        assert userInput.equals(userInput.trim()) : "input should be trimmed before parsing";
        // Limit of 2 stops the split after the first space, keeping the rest whole.
        return userInput.split("\\s+", 2);
    }

    /**
     * Returns the argument unchanged, or refuses it if it holds the save file's
     * separator character.
     *
     * <p>A description such as {@code read book | now} would be written as an extra
     * field and could not be read back, so it is refused rather than mangled.
     *
     * @param argument everything the user typed after the command word
     * @return the same argument, unchanged
     * @throws DavidGogginsException if the argument contains the separator character
     */
    public static String rejectSeparator(String argument) throws DavidGogginsException {
        // Escaping the character would accept more input, but it makes both the writer
        // and the reader harder to follow; refusing one rarely used character is the
        // simpler trade for a task list.
        if (argument.contains(Task.SEPARATOR_CHAR)) {
            throw new DavidGogginsException("A task cannot contain the \""
                    + Task.SEPARATOR_CHAR + "\" character, since that is what I use to "
                    + "separate fields when saving. Drop it and try again.");
        }
        return argument;
    }

    /**
     * Turns the number the user typed into an {@code int}.
     *
     * <p>Whether that number actually refers to an existing task is not checked here:
     * that depends on the list, which the parser deliberately knows nothing about.
     *
     * @param argument    the task number the user typed, as text
     * @param commandName the command it was typed for, used in the error message so the
     *                    advice names the command the user actually used
     * @return the number the user typed, which may not refer to an existing task
     * @throws DavidGogginsException if the argument is not a single whole number that fits in an int
     */
    public static int parseTaskNumber(String argument, String commandName) throws DavidGogginsException {
        if (argument.split("\\s+").length > 1) {
            throw new DavidGogginsException("One task at a time. Give me a single number, e.g. "
                    + commandName + " 2.");
        }
        try {
            return Integer.parseInt(argument);
        } catch (NumberFormatException e) {
            if (WHOLE_NUMBER.matcher(argument).matches()) {
                // A whole number that does not fit in an int cannot be a task number either,
                // but "not a number" would be the wrong advice for it.
                throw new DavidGogginsException("There's no task " + argument
                        + " in your list. That number is way past the end of it.");
            }
            // The user typed something like "mark two".
            throw new DavidGogginsException(
                    "\"" + argument + "\" is not a task number. Use a whole number, e.g. "
                            + commandName + " 2.");
        }
    }

    /**
     * Returns the keyword the user wants to search for.
     *
     * <p>Left exactly as typed apart from the trimming every argument gets: the
     * search itself is what ignores case, so the keyword is not lower-cased here.
     *
     * @param argument everything the user typed after the word "find"
     * @return the keyword to search for, never empty
     * @throws DavidGogginsException if no keyword was given
     */
    public static String parseKeyword(String argument) throws DavidGogginsException {
        if (argument.isEmpty()) {
            throw new DavidGogginsException(
                    "Tell me what to search for. You can't chase what you can't name. Try: find book");
        }
        return argument;
    }

    /**
     * Creates a todo from its description.
     *
     * @param argument everything the user typed after the word "todo"
     * @return a new todo, not yet done
     * @throws DavidGogginsException if the description is empty or contains a date flag
     */
    public static Todo parseTodo(String argument) throws DavidGogginsException {
        if (argument.isEmpty()) {
            throw new DavidGogginsException(
                    "The description of a todo cannot be empty. Name the work. Try: " + TODO_EXAMPLE);
        }
        // Only the date flags are refused: a todo's description is free text, so some
        // other slash-word in it is more likely meant than mistyped.
        for (String flag : findFlags(argument)) {
            if (flag.equals(FLAG_BY) || flag.equals(FLAG_FROM) || flag.equals(FLAG_TO)) {
                throw new DavidGogginsException("A todo has no dates, so it takes no " + flag
                        + " part. Use deadline or event for a dated task. Try: " + TODO_EXAMPLE);
            }
        }
        return new Todo(argument);
    }

    /**
     * Creates a deadline from {@code <description> /by <when>}.
     *
     * @param argument everything the user typed after the word "deadline"
     * @return a new deadline, not yet done
     * @throws DavidGogginsException if the description or the due time is missing, or a
     *                               flag is repeated or does not belong to a deadline
     */
    public static Deadline parseDeadline(String argument) throws DavidGogginsException {
        rejectRepeatedFlag(argument, FLAG_BY, "A deadline has one due date", DEADLINE_EXAMPLE);
        for (String flag : findFlags(argument)) {
            if (!flag.equals(FLAG_BY)) {
                throw new DavidGogginsException("A deadline does not take a " + flag
                        + " part, only /by. Try: " + DEADLINE_EXAMPLE);
            }
        }

        // Splitting on the bare keyword (rather than " /by ") lets us spot a
        // "/by" with nothing after it instead of silently failing to split.
        String[] parts = argument.split("/by", 2);
        if (parts.length < 2) {
            throw new DavidGogginsException(
                    "A deadline needs a /by part. Without a date it's just a wish. Try: " + DEADLINE_EXAMPLE);
        }

        String description = parts[0].trim();
        String by = parts[1].trim();
        if (description.isEmpty()) {
            throw new DavidGogginsException(
                    "The description of a deadline cannot be empty. Name the work. Try: " + DEADLINE_EXAMPLE);
        }
        if (by.isEmpty()) {
            throw new DavidGogginsException(
                    "Tell me when it is due after /by. No date, no deadline. Try: " + DEADLINE_EXAMPLE);
        }
        return new Deadline(description, by);
    }

    /**
     * Creates an event from {@code <description> /from <start> /to <end>}.
     *
     * @param argument everything the user typed after the word "event"
     * @return a new event, not yet done
     * @throws DavidGogginsException if the description, the start or the end is missing,
     *                               or a flag is repeated, out of order or does not belong
     */
    public static Event parseEvent(String argument) throws DavidGogginsException {
        rejectRepeatedFlag(argument, FLAG_FROM, "An event starts once", EVENT_EXAMPLE);
        rejectRepeatedFlag(argument, FLAG_TO, "An event ends once", EVENT_EXAMPLE);
        for (String flag : findFlags(argument)) {
            if (!flag.equals(FLAG_FROM) && !flag.equals(FLAG_TO)) {
                throw new DavidGogginsException("An event does not take a " + flag
                        + " part, only /from and /to. Try: " + EVENT_EXAMPLE);
            }
        }
        int fromIndex = argument.indexOf(FLAG_FROM);
        int toIndex = argument.indexOf(FLAG_TO);
        if (fromIndex >= 0 && toIndex >= 0 && toIndex < fromIndex) {
            throw new DavidGogginsException("Put /from before /to: start first, then finish. Try: "
                    + EVENT_EXAMPLE);
        }

        String[] fromParts = argument.split("/from", 2);
        if (fromParts.length < 2) {
            throw new DavidGogginsException(
                    "An event needs a /from part. When does the work start? Try: " + EVENT_EXAMPLE);
        }

        String[] toParts = fromParts[1].split("/to", 2);
        if (toParts.length < 2) {
            throw new DavidGogginsException(
                    "An event needs a /to part after /from. When does it end? Try: " + EVENT_EXAMPLE);
        }

        String description = fromParts[0].trim();
        String from = toParts[0].trim();
        String to = toParts[1].trim();
        if (description.isEmpty()) {
            throw new DavidGogginsException(
                    "The description of an event cannot be empty. Name the work. Try: " + EVENT_EXAMPLE);
        }
        if (from.isEmpty()) {
            throw new DavidGogginsException(
                    "Tell me when the event starts after /from. Try: " + EVENT_EXAMPLE);
        }
        if (to.isEmpty()) {
            throw new DavidGogginsException(
                    "Tell me when the event ends after /to. Try: " + EVENT_EXAMPLE);
        }
        return new Event(description, from, to);
    }

    /**
     * Returns every word in the argument that looks like a flag, in the order typed.
     *
     * @param argument everything the user typed after the command word
     * @return the flag-like words, such as {@code /by} or {@code /at}, which may be empty
     */
    private static List<String> findFlags(String argument) {
        List<String> flags = new ArrayList<>();
        Matcher matcher = FLAG_WORD.matcher(argument);
        while (matcher.find()) {
            flags.add(matcher.group());
        }
        return flags;
    }

    /**
     * Refuses an argument that contains the given flag more than once.
     *
     * <p>Without this, the second copy would be swallowed into a date and reported as a
     * badly written date, which sends the user looking in the wrong place.
     *
     * @param argument the text to check
     * @param flag     the flag that may appear at most once, e.g. {@code /by}
     * @param reason   why only one is allowed, e.g. "A deadline has one due date"
     * @param example  a correct command to suggest
     * @throws DavidGogginsException if the flag appears more than once
     */
    private static void rejectRepeatedFlag(String argument, String flag, String reason, String example)
            throws DavidGogginsException {
        // Counted the same way the split finds it, as plain text, so the two always agree.
        int firstIndex = argument.indexOf(flag);
        if (firstIndex >= 0 && argument.indexOf(flag, firstIndex + flag.length()) >= 0) {
            throw new DavidGogginsException("You gave " + flag + " more than once. " + reason
                    + ". Try: " + example);
        }
    }
}
