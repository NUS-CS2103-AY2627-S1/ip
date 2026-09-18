package tally.parser;

import java.time.LocalDate;
import java.util.regex.Pattern;

import tally.TallyException;
import tally.task.Deadline;
import tally.task.Event;
import tally.task.Moment;
import tally.task.Task;
import tally.task.Todo;
import tally.task.Window;

/**
 * Turns what the user typed into the things Tally acts on.
 *
 * <p>Every method here reports a line it cannot make sense of as a
 * TallyException carrying a message written for the user, so the caller can
 * show it and carry on rather than deciding what went wrong.
 */
public class Parser {
    /**
     * How the data file separates one part of a task from the next.
     *
     * <p>The file has no way to escape it, so text holding it would be written out and
     * read back as a different number of parts. The whole character is refused rather
     * than the separator as written, because the file joins parts with a space either
     * side: a description merely ending in a bar builds the separator on being saved.
     */
    private static final String SEPARATOR_CHARACTER = "|";

    /**
     * What separates the command word from the rest of the line.
     *
     * <p>Any run of spaces or tabs, not a single space. A task's own text has its
     * spacing tidied wherever it comes from, so a line typed with a tab in it would
     * otherwise be read as a task everywhere except at the command word.
     */
    private static final Pattern AFTER_THE_COMMAND = Pattern.compile("\\s+");

    /** The markers the free command takes, each matched as a whole word. */
    private static final String MARKER_FOR = "/for";
    private static final String MARKER_FROM = "/from";

    /** Prevents anyone making one: every method here is static. */
    private Parser() {
    }

    /**
     * Returns the command named by the first word of a line.
     *
     * @param line the line the user typed, with surrounding spaces removed.
     * @return the command it names.
     * @throws TallyException if the first word names no command.
     */
    public static Command parseCommand(String line) throws TallyException {
        return Command.parse(AFTER_THE_COMMAND.split(line, 2)[0]);
    }

    /**
     * Refuses arguments given to a command that takes none.
     *
     * <p>They were being dropped, so "bye now" ended the conversation and "list all"
     * listed everything: the user is told their line did nothing they asked for, rather
     * than being given the nearest command Tally could find in it.
     *
     * @param command the command the line names.
     * @param arguments whatever followed the command word.
     * @throws TallyException if the command takes nothing and something was given.
     */
    public static void rejectUnwantedArguments(Command command, String arguments)
            throws TallyException {
        if (command.takesArguments() || arguments.isEmpty()) {
            return;
        }
        throw new TallyException(String.format(
                "%s takes nothing after it. \"%s\" is yours to explain.",
                command.getKeyword(), arguments));
    }

    /**
     * Refuses a line that gives the same marker more than once.
     *
     * <p>The parts are split on the first marker found, so a second one ends up inside
     * the value of the first: "/by Monday /by Tuesday" was read as a date of
     * "Monday /by Tuesday" and refused for the wrong reason, and an event's free-text
     * end swallowed it whole without complaint.
     *
     * @param arguments everything the user typed after the command word.
     * @param marker the marker to count, written as it is split on, such as " /by ".
     * @throws TallyException if the marker appears more than once.
     */
    private static void rejectRepeatedMarker(String arguments, String marker)
            throws TallyException {
        String padded = " " + arguments + " ";
        int first = padded.indexOf(marker);
        if (first >= 0 && padded.indexOf(marker, first + 1) >= 0) {
            throw new TallyException(String.format(
                    "%s is given more than once. Choose.",
                    marker.trim()));
        }
    }

    /**
     * Returns whatever follows the command word on a line.
     *
     * @param line the line the user typed, with surrounding spaces removed.
     * @return the arguments, or an empty string if the line is only a command word.
     */
    public static String parseArguments(String line) {
        String[] words = AFTER_THE_COMMAND.split(line, 2);
        return words.length > 1 ? words[1].trim() : "";
    }

    /**
     * Returns the todo described by a "todo" command's arguments.
     *
     * @param arguments what the user typed after the command word.
     * @return the todo described.
     * @throws TallyException if no description was given.
     */
    public static Todo parseTodo(String arguments) throws TallyException {
        if (arguments.isEmpty()) {
            throw new TallyException("A todo needs a description. Example: todo read book");
        }
        rejectSeparator(arguments);
        return new Todo(arguments);
    }

    /**
     * Returns the deadline described by a "deadline" command's arguments.
     *
     * @param arguments what the user typed after the command word.
     * @return the deadline described.
     * @throws TallyException if the description or the date is missing or unreadable.
     */
    public static Deadline parseDeadline(String arguments) throws TallyException {
        rejectRepeatedMarker(arguments, " /by ");
        String[] fields = arguments.split(" /by ", 2);
        if (fields.length < 2 || fields[0].isBlank() || fields[1].isBlank()) {
            throw new TallyException(
                    "A deadline needs a description and a /by date."
                            + " Example: deadline return book /by 2019-10-15");
        }
        rejectSeparator(fields[0]);
        return new Deadline(fields[0], parseDate(fields[1].trim()));
    }

    /**
     * Returns the three parts of a command written with two markers, in marker order.
     *
     * <p>Splitting on the first marker before looking for the second is what makes the
     * order matter: a line writing them the other way round leaves no second marker
     * behind to find, and is refused rather than quietly recorded back to front.
     *
     * @param arguments everything the user typed after the command word.
     * @param firstMarker the marker introducing the second part, such as " /from ".
     * @param secondMarker the marker introducing the third part, such as " /to ".
     * @param usage what to tell the user when a part or a marker is missing.
     * @return the description and the two values the markers introduce, trimmed.
     * @throws TallyException if either marker is missing or any of the three is blank.
     */
    private static String[] splitOnTwoMarkers(String arguments, String firstMarker,
            String secondMarker, String usage) throws TallyException {
        rejectRepeatedMarker(arguments, firstMarker);
        rejectRepeatedMarker(arguments, secondMarker);
        String[] descriptionAndRest = arguments.split(firstMarker, 2);
        if (descriptionAndRest.length < 2) {
            throw new TallyException(usage);
        }
        String[] secondAndThird = descriptionAndRest[1].split(secondMarker, 2);
        if (secondAndThird.length < 2 || descriptionAndRest[0].isBlank()
                || secondAndThird[0].isBlank() || secondAndThird[1].isBlank()) {
            throw new TallyException(usage);
        }
        return new String[] {descriptionAndRest[0].trim(), secondAndThird[0].trim(),
                secondAndThird[1].trim()};
    }

    /**
     * Returns the event described by an "event" command's arguments.
     *
     * <p>The arguments are split on /from first, and on /to within what follows.
     * Splitting in that order is what makes the order of the two markers matter:
     * an event written /to first leaves no /to in the remainder, so it is refused
     * rather than recorded with its start and end the wrong way round.
     *
     * @param arguments what the user typed after the command word.
     * @return the event described.
     * @throws TallyException if the description, the start or the end is missing, if
     *     /to is written before /from, if either end cannot be read, or if the event
     *     ends before it starts.
     */
    public static Event parseEvent(String arguments) throws TallyException {
        // AI found the bug, manually fixed.
        String usage = "An event needs a description, a /from date and a /to date,"
                + " in that order, each with an optional time after it."
                + " Example: event lecture /from 2026-08-06 16:00 /to 2026-08-06 18:00";
        String[] parts = splitOnTwoMarkers(arguments, " /from ", " /to ", usage);
        for (String part : parts) {
            rejectSeparator(part);
        }
        Moment start = parseMoment(parts[1]);
        Moment end = parseMoment(parts[2]);
        if (Event.hasBackwardsEnds(start, end)) {
            throw new TallyException(String.format(
                    "An event cannot end before it starts. You have it backwards:"
                            + " ends %s, starts %s.", end.toSaveFormat(), start.toSaveFormat()));
        }
        return new Event(parts[0], start, end);
    }

    /**
     * Returns the moment an end of an event names.
     *
     * <p>A time is optional. Most events are remembered by the day they fall on, and one
     * that is not was written before an event could carry an hour at all, so demanding
     * one would refuse what the user has every reason to expect to work.
     *
     * @param text what the user typed as one end of the event.
     * @return the day, and the hour if one was given.
     * @throws TallyException if it is not a date, optionally followed by a time.
     */
    private static Moment parseMoment(String text) throws TallyException {
        return Moment.read(text).orElseThrow(() -> new TallyException(String.format(
                "Unreadable date: \"%s\". The form is yyyy-mm-dd, and a time after it"
                        + " is written HH:mm. Example: 2019-10-15 16:00.", text)));
    }

    /**
     * Returns the text a "find" command is searching for.
     *
     * @param arguments what the user typed after the command word.
     * @return the text to look for.
     * @throws TallyException if nothing was given to search for.
     */
    public static String parseSearchText(String arguments) throws TallyException {
        if (arguments.isEmpty()) {
            throw new TallyException("find needs something to look for. Example: find book");
        }
        return arguments;
    }

    /**
     * Returns what a free command asked for: a run of days, and where to start looking.
     *
     * <p>Both parts are optional. "free" asks for the next single free day from today,
     * "/for 3" asks for three in a row, and "/from 2026-09-08" moves the search off
     * today, which is what lets the answer be checked against a fixed expectation.
     *
     * @param arguments everything the user typed after the command word.
     * @param today the day to search from when no /from date is given.
     * @return the run wanted and the day to start from.
     * @throws TallyException if a marker is malformed, the count is not a positive
     *     number, or the date cannot be read.
     */
    public static FreeQuery parseFreeQuery(String arguments, LocalDate today) throws TallyException {
        String usage = "free takes an optional /for count and an optional /from date."
                + " Example: free /for 3 /from 2026-09-08";
        String trimmed = arguments.trim();
        String[] tokens = trimmed.isEmpty() ? new String[0] : trimmed.split("\\s+");

        int days = 1;
        LocalDate earliestDate = today;
        boolean hasCount = false;
        boolean hasDate = false;
        for (int i = 0; i < tokens.length; i += 2) {
            boolean hasValue = i + 1 < tokens.length;
            if (tokens[i].equals(MARKER_FOR) && hasValue && !hasCount) {
                days = parseDayCount(tokens[i + 1], usage);
                hasCount = true;
            } else if (tokens[i].equals(MARKER_FROM) && hasValue && !hasDate) {
                earliestDate = parseDate(tokens[i + 1]);
                hasDate = true;
            } else {
                throw new TallyException(usage);
            }
        }
        return new FreeQuery(days, earliestDate);
    }

    /**
     * Returns the number of days a /for marker named.
     *
     * @param text what followed the marker.
     * @param usage what to tell the user when it makes no sense.
     * @return the count, always at least one.
     * @throws TallyException if the text is not a number, or names fewer than one day.
     */
    private static int parseDayCount(String text, String usage) throws TallyException {
        int days;
        try {
            days = Integer.parseInt(text);
        } catch (NumberFormatException exception) {
            throw new TallyException(usage);
        }
        if (days < 1) {
            throw new TallyException("A free stretch must be at least one day. Fewer is not a stretch.");
        }
        return days;
    }

    /**
     * Returns the window task described by "submit form /between 2026-09-08 /and 2026-09-12".
     *
     * <p>Both ends are read as dates rather than kept as text, so that a period running
     * backwards can be refused here instead of reaching the tally.
     *
     * @param arguments everything the user typed after the command word.
     * @return the window task described.
     * @throws TallyException if either marker or either part is missing, if a date cannot
     *     be read, or if the period ends before it starts.
     */
    public static Window parseWindow(String arguments) throws TallyException {
        String usage = "A window needs a description, a /between date and an /and date,"
                + " in that order."
                + " Example: window submit form /between 2026-09-08 /and 2026-09-12";
        String[] parts = splitOnTwoMarkers(arguments, " /between ", " /and ", usage);
        rejectSeparator(parts[0]);

        LocalDate startDate = parseDate(parts[1]);
        LocalDate endDate = parseDate(parts[2]);
        // Turned away at the keyboard rather than stored, since nothing later can make
        // sense of it: the free-day search would be asked about a stretch with no days.
        if (endDate.isBefore(startDate)) {
            throw new TallyException(String.format(
                    "A window cannot end before it starts. You have it backwards:"
                            + " ends %s, starts %s.", endDate, startDate));
        }
        return new Window(parts[0], startDate, endDate);
    }

    /**
     * Refuses text that the data file could not carry back unchanged.
     *
     * @param text a part of a task as the user typed it.
     * @throws TallyException if it holds the separator the data file uses.
     */
    private static void rejectSeparator(String text) throws TallyException {
        if (text.contains(SEPARATOR_CHARACTER)) {
            throw new TallyException(String.format(
                    "A task cannot contain \"%s\": the record file separates one part"
                            + " from the next with it. Choose another character.",
                    SEPARATOR_CHARACTER));
        }
    }

    /**
     * Returns the date some text names, wherever in a command it was written.
     *
     * <p>Dates are read in the yyyy-mm-dd form alone, and shown back in a different
     * form, as Level-8 requires. What that form is belongs to Task, which every reader
     * asks; this adds the complaint to make when a user wrote something else, which the
     * data file has no use for.
     *
     * @param text what the user typed as the date.
     * @return the date it names.
     * @throws TallyException if the text is not a date written as yyyy-mm-dd.
     */
    public static LocalDate parseDate(String text) throws TallyException {
        return Task.readDate(text).orElseThrow(() -> new TallyException(String.format(
                "Unreadable date: \"%s\". The form is yyyy-mm-dd, and has not changed."
                        + " Example: 2019-10-15.", text)));
    }

    /**
     * Returns the position a command such as "mark 2" names, counting from 0.
     *
     * <p>The number the user types counts from 1, while the list is indexed from 0.
     * Doing the conversion here keeps it out of every command that names a task.
     *
     * @param arguments what the user typed after the command word.
     * @param size how many tasks the tally holds.
     * @param command the command that named the task, used to word the error.
     * @return the index of the task the command refers to.
     * @throws TallyException if no number was given, it is not a number, or no task
     *     has that position.
     */
    public static int parseTaskIndex(String arguments, int size, Command command)
            throws TallyException {
        int position;
        try {
            position = Integer.parseInt(arguments);
        } catch (NumberFormatException exception) {
            throw new TallyException(String.format(
                    "%s needs the number of a task. Example: %s 2",
                    command.getKeyword(), command.getKeyword()));
        }
        if (position < 1 || position > size) {
            throw new TallyException(String.format(
                    "There is no task %d on record. Type list before guessing.",
                    position));
        }
        return position - 1;
    }
}
