package shannon;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.StringJoiner;

import shannon.exception.EmptyDescriptionException;
import shannon.exception.EmptyKeywordException;
import shannon.exception.InvalidDateException;
import shannon.exception.InvalidEventTimesException;
import shannon.exception.InvalidMarkerException;
import shannon.exception.InvalidTaskNumberException;
import shannon.exception.MissingDeadlineByException;
import shannon.exception.MissingEventTimeException;
import shannon.exception.ShannonException;
import shannon.exception.UnexpectedArgumentException;
import shannon.task.Deadline;
import shannon.task.Event;
import shannon.task.EventTime;
import shannon.task.Task;
import shannon.task.Todo;

/**
 * One line the user typed, broken into the parts the rest of the program needs.
 * <p>
 * A {@code Parser} is made from a single line of input and can then answer questions about it:
 * which command it is, and what the text after the command word means for that command. All the
 * string-splitting lives here, so the rest of the program works with task numbers and
 * {@link Task} objects instead. A line that cannot be made sense of is reported by throwing a
 * {@link ShannonException}, so the command loop handles every error in one {@code catch}.
 * <p>
 * The parser is forgiving about harmless differences and strict about real mistakes. The command
 * word may be in any case ({@code LIST} works), and runs of spaces are squeezed to one, so
 * {@code todo read   book} adds "read book". But a marker such as {@code /by} that is typed into
 * the wrong command, typed twice, or typed out of order is refused with a message saying so,
 * instead of being quietly swallowed into a description or a date.
 */
// An object rather than a class of static helpers: nearly every parse method needs both halves of
// the line, the argument to read and the command word so an error can show a matching example
// ("Try: delete 2"). Splitting once in the constructor keeps the two in step, and means a line
// cannot be split two different ways in two different places.
public class Parser {

    /** The marker before a deadline's due date. */
    private static final String MARKER_BY = "/by";

    /** The marker before an event's start. */
    private static final String MARKER_FROM = "/from";

    /** The marker before an event's end. */
    private static final String MARKER_TO = "/to";

    /**
     * Every marker any command takes. A word matching one of these is treated as a marker even
     * in a command that does not take it, so that it is refused rather than becoming part of a
     * description.
     */
    private static final List<String> ALL_MARKERS = List.of(MARKER_BY, MARKER_FROM, MARKER_TO);

    /** A complete, valid {@code todo} command, shown when one is typed wrongly. */
    private static final String EXAMPLE_TODO = "todo visit new theme park";

    /** A complete, valid {@code deadline} command, shown when one is typed wrongly. */
    private static final String EXAMPLE_DEADLINE = "deadline submit report /by 2026-08-09";

    /** A complete, valid {@code event} command, shown when one is typed wrongly. */
    private static final String EXAMPLE_EVENT =
            "event team meeting /from 2026-08-09 14:00 /to 2026-08-09 16:00";

    /** The first word in lower case, e.g. {@code deadline}. Empty if the user typed nothing. */
    private final String command;

    /** Everything after the command word, without the spaces around it. Never null. */
    private final String argument;

    /**
     * Splits a line into its command word and the rest.
     *
     * @param input one line exactly as the user typed it.
     */
    public Parser(String input) {
        // Limit of 2, so that a command typed on its own (e.g. "todo") still yields a command
        // word and an empty argument, and can therefore report the right error.
        String[] words = input.trim().split("\\s+", 2);
        // Lower case so that "List" or "TODO" works too: a capital letter is not a real mistake.
        // Locale.ROOT keeps the result the same whatever language the machine is set to.
        this.command = words[0].toLowerCase(Locale.ROOT);
        this.argument = words.length > 1 ? words[1] : "";
    }

    /**
     * Returns the command word, which the caller compares against the commands it knows.
     *
     * @return the first word of the line in lower case, or an empty string if the line was blank
     */
    public String getCommand() {
        return command;
    }

    /**
     * Returns whether anything was typed after the command word.
     *
     * @return {@code true} if the command word was followed by more text.
     */
    public boolean hasArgument() {
        return !argument.isEmpty();
    }

    /**
     * Checks that nothing was typed after the command word, for commands such as {@code list}
     * and {@code bye} that take nothing.
     * <p>
     * Extra words are refused rather than ignored, because they usually mean the user expected
     * them to do something: {@code list done} does not list only the done tasks, and saying so
     * is kinder than letting the user believe it did. For {@code bye}, it also keeps a sentence
     * that merely starts with "bye" from closing the chatbot.
     *
     * @throws UnexpectedArgumentException if there is text after the command word.
     */
    public void checkNoArgument() throws UnexpectedArgumentException {
        if (hasArgument()) {
            throw new UnexpectedArgumentException(command, argument);
        }
    }

    /**
     * Reads the one or more task numbers typed after the command word.
     * <p>
     * Several numbers may be given, separated by spaces, so {@code delete 2 5} works as well as
     * {@code delete 2}. The numbers are returned as an array rather than one at a time, which is
     * what lets {@link TaskList} take them as a varargs parameter.
     * <p>
     * Only the reading is done here. Whether those numbers refer to tasks that exist is
     * {@link TaskList}'s business, and is checked when they are used.
     *
     * @return the numbers the user typed, in the order typed, each counting from 1.
     * @throws InvalidTaskNumberException if no number was given, or one is not a whole number.
     */
    public int[] parseTaskNumbers() throws ShannonException {
        if (argument.isEmpty()) {
            throw new InvalidTaskNumberException(command, "");
        }
        String[] words = argument.split("\\s+");
        int[] taskNumbers = new int[words.length];
        for (int i = 0; i < words.length; i++) {
            try {
                taskNumbers[i] = Integer.parseInt(words[i]);
            } catch (NumberFormatException e) {
                // Translate Java's low-level parsing error into one of our own, so the command
                // loop only ever has to know about ShannonException. The offending word is
                // named, so "delete 1 two 3" points at "two" rather than at the whole line.
                throw new InvalidTaskNumberException(command, words[i]);
            }
        }
        return taskNumbers;
    }

    /**
     * Reads {@code find <keyword>}.
     * <p>
     * The whole of the rest of the line is the keyword, so {@code find team meeting} looks for
     * that phrase rather than for either word on its own. Splitting the argument into separate
     * words would be a larger feature: it would have to decide whether a task must contain all
     * of them or any of them, which is more than the command needs to be useful.
     *
     * @return the text to search for, with runs of spaces squeezed to one, as descriptions are
     * @throws EmptyKeywordException if no keyword was given
     */
    public String parseKeyword() throws ShannonException {
        // Squeezed like a description, so "find team   meeting" still finds "team meeting".
        String keyword = argument.replaceAll("\\s+", " ");
        if (keyword.isEmpty()) {
            throw new EmptyKeywordException();
        }
        return keyword;
    }

    /**
     * Reads {@code todo <description>}.
     *
     * @return the task it describes
     * @throws EmptyDescriptionException if no description was given
     * @throws InvalidMarkerException    if a marker such as {@code /by} was typed, since a todo has none
     */
    public Task parseTodo() throws ShannonException {
        // A todo takes no markers, so any /by, /from or /to is refused as belonging elsewhere.
        String description = splitAtMarkers(EXAMPLE_TODO)[0];
        if (description.isEmpty()) {
            throw new EmptyDescriptionException("todo", EXAMPLE_TODO);
        }
        return new Todo(description);
    }

    /**
     * Reads {@code deadline <description> /by <yyyy-mm-dd>}.
     *
     * @return the task it describes
     * @throws MissingDeadlineByException if the {@code /by} part is missing or blank
     * @throws EmptyDescriptionException  if no description was given before the {@code /by}
     * @throws InvalidMarkerException     if {@code /by} is repeated, unspaced, or joined by another marker
     * @throws InvalidDateException       if the {@code /by} part is not a real date in
     *                                    {@code yyyy-mm-dd} form
     */
    public Task parseDeadline() throws ShannonException {
        String[] parts = splitAtMarkers(EXAMPLE_DEADLINE, MARKER_BY);
        String description = parts[0];
        String by = parts[1];
        // The two checks are separate so the user is told exactly which half is missing.
        if (by.isEmpty()) {
            throw new MissingDeadlineByException(EXAMPLE_DEADLINE);
        }
        if (description.isEmpty()) {
            throw new EmptyDescriptionException("deadline", EXAMPLE_DEADLINE);
        }
        // The date is turned into a LocalDate here, at the edge of the program, so that a
        // Deadline object can never hold a date we failed to understand.
        return new Deadline(description, Deadline.parseBy(by));
    }

    /**
     * Reads {@code event <description> /from <start> /to <end>}, where the start and end are
     * each a date in {@code yyyy-mm-dd} form, optionally followed by a time in {@code HH:mm} form.
     *
     * @return the task it describes
     * @throws MissingEventTimeException  if the {@code /from} or {@code /to} part is missing or blank
     * @throws EmptyDescriptionException  if no description was given before the {@code /from}
     * @throws InvalidMarkerException     if a marker is repeated, unspaced, out of order, or a {@code /by}
     * @throws InvalidDateException       if the start or end is not a real date, with or without a time
     * @throws InvalidEventTimesException if the event does not end after it starts
     */
    public Task parseEvent() throws ShannonException {
        String[] parts = splitAtMarkers(EXAMPLE_EVENT, MARKER_FROM, MARKER_TO);
        String description = parts[0];
        String from = parts[1];
        String to = parts[2];
        if (from.isEmpty() || to.isEmpty()) {
            throw new MissingEventTimeException(EXAMPLE_EVENT);
        }
        if (description.isEmpty()) {
            throw new EmptyDescriptionException("event", EXAMPLE_EVENT);
        }
        return new Event(description, EventTime.parse(from), EventTime.parse(to));
    }

    /**
     * Splits the argument into the description and the text after each of the given markers.
     * <p>
     * A marker counts only as a word of its own, matched ignoring case, so the {@code /by} in
     * {@code and/by} is left alone while {@code /BY} is still recognized. Along the way, the
     * mistakes that would otherwise put text in the wrong place are refused: a marker that
     * belongs to a different command, a marker typed twice, markers in the wrong order, and a
     * marker with no space around it. Runs of spaces are squeezed to one in every part.
     *
     * @param example a full, valid example of the command, shown in error messages.
     * @param markers the markers this command takes, in the order they must appear.
     * @return the description, then the text after each marker in the order given. Any part is
     *         empty if it was left blank or its marker is missing, for the caller to report,
     *         since only the caller knows what to say about it.
     * @throws InvalidMarkerException if a marker is misused in one of the ways above.
     */
    private String[] splitAtMarkers(String example, String... markers) throws InvalidMarkerException {
        List<String> expectedMarkers = List.of(markers);
        List<String> seenMarkers = new ArrayList<>();
        String[] parts = new String[markers.length + 1];
        Arrays.fill(parts, "");

        // Words are collected into parts[currentPart]: 0 is the description, and i is the text
        // after markers[i - 1]. Joining the words with single spaces is what squeezes the runs.
        int currentPart = 0;
        StringJoiner currentText = new StringJoiner(" ");
        for (String word : argument.split("\\s+")) {
            String marker = word.toLowerCase(Locale.ROOT);
            if (!ALL_MARKERS.contains(marker)) {
                currentText.add(word);
                continue;
            }
            checkMarker(marker, expectedMarkers, seenMarkers, example);
            parts[currentPart] = currentText.toString();
            currentText = new StringJoiner(" ");
            currentPart = expectedMarkers.indexOf(marker) + 1;
            seenMarkers.add(marker);
        }
        parts[currentPart] = currentText.toString();

        checkMarkerSpacing(expectedMarkers, seenMarkers, example);
        return parts;
    }

    /**
     * Checks that a marker just found in the argument is allowed at that point.
     *
     * @param marker          the marker found, in lower case.
     * @param expectedMarkers the markers this command takes, in the order they must appear.
     * @param seenMarkers     the markers found before this one, in the order found.
     * @param example         a full, valid example of the command, shown in error messages.
     * @throws InvalidMarkerException if the command does not take this marker, it has already
     *                                appeared, or it should have come before one already found.
     */
    private void checkMarker(String marker, List<String> expectedMarkers, List<String> seenMarkers,
            String example) throws InvalidMarkerException {
        if (!expectedMarkers.contains(marker)) {
            // Typing a marker the command does not take usually means the wrong command was
            // picked, so the example shows the command the marker belongs to.
            String suggestion = marker.equals(MARKER_BY) ? EXAMPLE_DEADLINE : EXAMPLE_EVENT;
            throw InvalidMarkerException.ofUnexpected(command, marker, suggestion);
        }
        if (seenMarkers.contains(marker)) {
            throw InvalidMarkerException.ofRepeated(marker, example);
        }
        if (!seenMarkers.isEmpty()) {
            String lastMarker = seenMarkers.getLast();
            if (expectedMarkers.indexOf(marker) < expectedMarkers.indexOf(lastMarker)) {
                throw InvalidMarkerException.ofWrongOrder(marker, lastMarker, example);
            }
        }
    }

    /**
     * Checks that every marker which seems to be missing was not simply typed without spaces,
     * as in {@code report/by 2026-08-09}.
     * <p>
     * Without this, that line would be told it needs a {@code /by}, which is confusing to
     * someone who can see the {@code /by} they typed.
     *
     * @param expectedMarkers the markers this command takes.
     * @param seenMarkers     the markers found as words of their own.
     * @param example         a full, valid example of the command, shown in error messages.
     * @throws InvalidMarkerException if a missing marker appears joined to other text.
     */
    private void checkMarkerSpacing(List<String> expectedMarkers, List<String> seenMarkers,
            String example) throws InvalidMarkerException {
        String lowerArgument = argument.toLowerCase(Locale.ROOT);
        for (String marker : expectedMarkers) {
            if (!seenMarkers.contains(marker) && lowerArgument.contains(marker)) {
                throw InvalidMarkerException.ofMissingSpace(marker, example);
            }
        }
    }
}
