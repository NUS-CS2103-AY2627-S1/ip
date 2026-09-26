package amadeus.parser;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import amadeus.AmadeusException;
import amadeus.task.Deadline;
import amadeus.task.Event;
import amadeus.task.TaskDateTime;
import amadeus.task.Todo;

/**
 * Turns the lines the user types into the commands and objects the rest of the
 * app works with.
 * <p>
 * All the knowledge of the input format - where "/by" sits, that the user counts
 * tasks from 1, which date formats are allowed - lives here, so the main loop can
 * work with real objects instead of picking strings apart. Anything the user
 * types that cannot be understood is reported as an {@link AmadeusException} with
 * a message telling them the correct form, which means an invalid task can never
 * be built in the first place.
 * <p>
 * Every method is static because a Parser has nothing to remember between calls.
 */
public class Parser {

    /** Matches one "#word" tag, e.g. "#fun"; "#" on its own is not a tag. */
    private static final Pattern TAG_PATTERN = Pattern.compile("#\\w+");

    /**
     * Returns the first word of the input, which tells us which command the user wants.
     */
    public static String parseCommandWord(String input) throws AmadeusException {
        String trimmed = input.trim();
        if (trimmed.isEmpty()) {
            throw new AmadeusException("A thousand apologies, you haven't told me anything yet, sir.");
        }
        // "\\s+" splits on any run of whitespace, and the limit of 2 stops the
        // split after the first word so the rest of the line stays in one piece.
        return trimmed.split("\\s+", 2)[0];
    }

    /**
     * Returns everything after the command word, trimmed.
     * Returns an empty string when the user typed the command word on its own,
     * which is what lets the callers below detect missing arguments instead of
     * crashing with a StringIndexOutOfBoundsException.
     */
    private static String parseArguments(String input) {
        String[] parts = input.trim().split("\\s+", 2);
        return parts.length < 2 ? "" : parts[1].trim();
    }

    /**
     * Pulls every "#word" tag out of the given text.
     * <p>
     * A tag can appear anywhere on the line - before, after, or in between the
     * other parts of the command - so this runs once on the whole argument text
     * before that text is split up any further.
     *
     * @param text the text to search, e.g. "read book #fun".
     * @return the text with every tag removed, and the tags found, in the order
     *         they appeared.
     */
    private static ParsedTags extractTags(String text) {
        List<String> tags = new ArrayList<>();
        Matcher matcher = TAG_PATTERN.matcher(text);
        while (matcher.find()) {
            tags.add(matcher.group());
        }
        String remaining = matcher.replaceAll("").trim().replaceAll("\\s+", " ");
        return new ParsedTags(remaining, tags);
    }

    /** The result of {@link #extractTags(String)}: the text with tags removed, and the tags themselves. */
    private record ParsedTags(String remainingText, List<String> tags) {
    }

    /**
     * Returns the todo described by a line of the form
     * {@code todo <description> [#tag...]}.
     *
     * @param input the whole line the user typed, e.g. "todo read book #fun".
     * @return a new todo, not yet done, with any tags found attached.
     * @throws AmadeusException if the description is missing.
     */
    public static Todo parseTodo(String input) throws AmadeusException {
        ParsedTags parsed = extractTags(parseArguments(input));
        String description = parsed.remainingText();
        if (description.isEmpty()) {
            throw new AmadeusException("A hundred apologies, a todo needs a description, sir.",
                    "Please use: todo <description>");
        }
        Todo todo = new Todo(description);
        todo.setTags(parsed.tags());
        return todo;
    }

    /**
     * Parses a line of the form {@code deadline <description> /by <date> [#tag...]}.
     *
     * @throws AmadeusException if the description, the "/by" marker, or the date is
     *                          missing, or if the date cannot be understood
     */
    public static Deadline parseDeadline(String input) throws AmadeusException {
        ParsedTags parsed = extractTags(parseArguments(input));
        String arguments = parsed.remainingText();
        int byIdx = arguments.indexOf("/by");
        if (byIdx == -1) {
            throw new AmadeusException("Ten thousand apologies, a deadline needs a '/by', sir.",
                    "Please use: deadline <description> /by <date>");
        }

        // The description sits before "/by"; the due time is everything after it.
        String description = arguments.substring(0, byIdx).trim();
        String by = arguments.substring(byIdx + "/by".length()).trim();

        if (description.isEmpty()) {
            throw new AmadeusException("A hundred apologies, a deadline needs a description, sir.",
                    "Please use: deadline <description> /by <date>");
        }
        if (by.isEmpty()) {
            throw new AmadeusException("A hundred apologies, you didn't say when it's due, sir.",
                    "Please use: deadline <description> /by <date>");
        }
        // TaskDateTime.parse throws if the text is not a date it recognises, so a
        // Deadline can never be built with a due date the app cannot understand.
        Deadline deadline = new Deadline(description, TaskDateTime.parse(by));
        deadline.setTags(parsed.tags());
        return deadline;
    }

    /**
     * Parses a line of the form
     * {@code event <description> /from <start> /to <end> [#tag...]}.
     *
     * @throws AmadeusException if any of the three parts is missing, if "/to"
     *                          appears before "/from", or if a date cannot be understood
     */
    public static Event parseEvent(String input) throws AmadeusException {
        ParsedTags parsed = extractTags(parseArguments(input));
        String arguments = parsed.remainingText();
        int fromIdx = arguments.indexOf("/from");
        int toIdx = arguments.indexOf("/to");

        if (fromIdx == -1 || toIdx == -1) {
            throw new AmadeusException("A million apologies, an event needs both '/from' and '/to', sir.",
                    "Please use: event <description> /from <start> /to <end>");
        }
        // The three parts are read by position, so they have to be in this order.
        if (toIdx < fromIdx) {
            throw new AmadeusException("A million apologies, '/from' has to come before '/to', sir.",
                    "Please use: event <description> /from <start> /to <end>");
        }

        String description = arguments.substring(0, fromIdx).trim();
        String start = arguments.substring(fromIdx + "/from".length(), toIdx).trim();
        String end = arguments.substring(toIdx + "/to".length()).trim();

        if (description.isEmpty()) {
            throw new AmadeusException("A hundred apologies, an event needs a description, sir.",
                    "Please use: event <description> /from <start> /to <end>");
        }
        if (start.isEmpty() || end.isEmpty()) {
            throw new AmadeusException("A hundred apologies, an event needs both a start and an end time, sir.",
                    "Please use: event <description> /from <start> /to <end>");
        }
        Event event = new Event(description, TaskDateTime.parse(start), TaskDateTime.parse(end));
        event.setTags(parsed.tags());
        return event;
    }

    /**
     * Reads the date given to the "on" command, e.g. "on 2019-12-02".
     * <p>
     * Any time of day the user adds is dropped, because "on" asks about a whole
     * day rather than a moment within it.
     *
     * @param input the whole line the user typed, e.g. "on 2019-12-02"
     * @return the day being asked about
     * @throws AmadeusException if the date is missing or not in a known format
     */
    public static LocalDate parseDate(String input) throws AmadeusException {
        String argument = parseArguments(input);
        if (argument.isEmpty()) {
            throw new AmadeusException("A hundred apologies, please tell me which date, sir.",
                    "Please use: on <date>, for example: on 2019-12-02");
        }
        return TaskDateTime.parse(argument).getDate();
    }

    /**
     * Returns the keyword given to the "find" command, e.g. "book" from "find book".
     * <p>
     * Everything after the command word is taken as one keyword rather than
     * being split into several, so "find read book" searches for the whole
     * phrase "read book".
     *
     * @param input the whole line the user typed, e.g. "find book".
     * @return the text to search descriptions for.
     * @throws AmadeusException if no keyword was given.
     */
    public static String parseKeyword(String input) throws AmadeusException {
        String keyword = parseArguments(input);
        if (keyword.isEmpty()) {
            throw new AmadeusException("A hundred apologies, please tell me what to search for, sir.",
                    "Please use: find <keyword>");
        }
        return keyword;
    }

    /**
     * Converts the number typed after "mark" or "unmark" into an array index.
     * The number shown to the user starts at 1, but arrays start at 0, hence the
     * subtraction at the end.
     *
     * @param input     the whole line the user typed, e.g. "mark 2"
     * @param taskCount how many tasks are currently stored
     * @return the 0-based index of the requested task
     * @throws AmadeusException if the number is missing, not a number, or out of range
     */
    public static int parseTaskIndex(String input, int taskCount) throws AmadeusException {
        String argument = parseArguments(input);
        if (argument.isEmpty()) {
            throw new AmadeusException("A hundred apologies, please tell me which task number, sir.");
        }

        int taskNumber;
        try {
            taskNumber = Integer.parseInt(argument);
        } catch (NumberFormatException e) {
            // parseInt reports bad input by throwing rather than by returning a
            // value, so this has to be caught rather than tested with an if.
            throw new AmadeusException("Hundreds of apologies, '" + argument + "' is not a task number, sir.");
        }

        if (taskCount == 0) {
            throw new AmadeusException("Thousands of apologies, your list is empty, sir.");
        }
        if (taskNumber < 1 || taskNumber > taskCount) {
            throw new AmadeusException("Thousands of apologies, I only have " + taskCount + " task(s), sir.");
        }
        return taskNumber - 1;
    }
}
