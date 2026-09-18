package myriad.parser;

import java.util.Locale;
import java.util.regex.Pattern;

import myriad.MyriadException;
import myriad.command.AddCommand;
import myriad.command.Command;
import myriad.command.DeleteCommand;
import myriad.command.ExitCommand;
import myriad.command.FindCommand;
import myriad.command.ListCommand;
import myriad.command.MarkCommand;
import myriad.command.ShowCommand;
import myriad.command.StatsCommand;
import myriad.command.UnmarkCommand;
import myriad.task.Deadline;
import myriad.task.Event;
import myriad.task.Task;
import myriad.task.TaskDateTime;
import myriad.task.ToDo;

/**
 * Makes sense of what the user typed, turning a line of input into the
 * {@link Command} that carries it out. This is the one place that knows the
 * command language — the keywords, the {@code /by}, {@code /from} and
 * {@code /to} markers, and which arguments each command requires — so
 * nothing else has to look at the raw line at all.
 *
 * The methods are static because parsing a line depends only on that
 * line: there's nothing to remember between calls, so a {@code Parser}
 * object would carry no state and only add ceremony at every call site.
 */
public class Parser {

    /** Separates a deadline's description from its due date. */
    private static final Pattern MARKER_BY = createMarkerPattern("/by");

    /** Separates an event's description from its start. */
    private static final Pattern MARKER_FROM = createMarkerPattern("/from");

    /** Separates an event's start from its end. */
    private static final Pattern MARKER_TO = createMarkerPattern("/to");

    /**
     * Separates the fields of a saved task. A description may not contain it,
     * because nothing escapes it in the data file.
     */
    private static final String SAVE_FIELD_SEPARATOR = "|";

    /** Not meant to be instantiated: every method here is static. */
    private Parser() {
    }

    /**
     * Builds the {@link Command} a stripped input line asks for, with that
     * line's arguments already interpreted. The first word is matched
     * case-insensitively against the known command keywords. {@code list},
     * {@code stats} and {@code bye} take no arguments, so they only match
     * when they are the whole line. Throws {@link MyriadException} if the
     * line names no known command, or if its arguments can't be made sense
     * of — so a command object only ever exists if it can actually be
     * attempted.
     *
     * @param strippedLine one line of user input, already whitespace-stripped
     *                     by whichever front end read it.
     * @return the {@code Command} that line asks for.
     * @throws MyriadException if the line names no known command, or its
     *                         arguments are missing or unparseable.
     */
    public static Command parse(String strippedLine) throws MyriadException {
        // Both front ends strip the line first; if one forgot, "bye " would have
        // non-empty args and fall through to "unrecognized command".
        assert strippedLine != null && strippedLine.equals(strippedLine.strip())
                : "Parser expects a line already stripped by Ui.readCommand or Myriad.getResponse";

        // Split off the keyword once; every parse* method below then works on
        // the argument text alone (empty if the line is just the keyword).
        String[] keywordAndArgs = strippedLine.split("\\s+", 2);
        // Lower-cased once, so that every case below is written in lower case.
        String keyword = keywordAndArgs[0].toLowerCase(Locale.ROOT);
        String args = keywordAndArgs.length == 2 ? keywordAndArgs[1] : "";

        return switch (keyword) {
            case "todo" -> new AddCommand(parseToDo(args));
            case "deadline" -> new AddCommand(parseDeadline(args));
            case "event" -> new AddCommand(parseEvent(args));
            case "list" -> requireNoArguments(args, new ListCommand());
            case "mark" -> new MarkCommand(parseTaskNumber(args));
            case "unmark" -> new UnmarkCommand(parseTaskNumber(args));
            case "delete" -> new DeleteCommand(parseTaskNumber(args));
            case "show" -> new ShowCommand(parseShowQuery(args));
            case "find" -> new FindCommand(parseFindKeyword(args));
            case "stats" -> requireNoArguments(args, new StatsCommand());
            case "bye" -> requireNoArguments(args, new ExitCommand());
            default -> throw createUnrecognizedCommandException();
        };
    }

    /**
     * Returns {@code command} if its keyword was the whole line, for the
     * commands that take no arguments. Throws the same
     * {@link MyriadException} as an unknown keyword otherwise, so that
     * {@code bye now} is rejected outright rather than quietly ending the
     * session with the {@code now} ignored.
     *
     * @param args    the argument text after the keyword.
     * @param command the command the keyword names.
     * @return {@code command}, if {@code args} is empty.
     * @throws MyriadException if {@code args} is not empty.
     */
    private static Command requireNoArguments(String args, Command command) throws MyriadException {
        if (!args.isEmpty()) {
            throw createUnrecognizedCommandException();
        }
        return command;
    }

    /**
     * Returns the exception for a line that names no known command, listing
     * every keyword the user can try instead.
     *
     * @return the exception to throw.
     */
    private static MyriadException createUnrecognizedCommandException() {
        return new MyriadException(
                "I don't recognize that command. Try: todo, deadline, "
                        + "event, list, mark, unmark, delete, "
                        + "show, find, stats, or bye.");
    }

    /**
     * Parses the task number argument of a {@code mark}, {@code unmark} or
     * {@code delete} command (e.g. the {@code 2} in {@code mark 2}) and
     * returns it as typed, 1-based. Throws {@link MyriadException} if it is
     * missing or isn't a whole number.
     *
     * Deliberately stops there: whether that number actually exists
     * depends on how many tasks there are right now, which is the task
     * list's business, not the command language's — so the range check
     * happens later, when the command runs (see
     * {@code TaskNumberCommand.resolveIndex}).
     *
     * @param args the argument text after the keyword.
     * @return the number as typed, 1-based.
     * @throws MyriadException if {@code args} is empty or isn't a whole number.
     */
    private static int parseTaskNumber(String args) throws MyriadException {
        if (args.isEmpty()) {
            throw new MyriadException(
                    "Please tell me which task number, e.g. \"mark 2\".");
        }

        String numberText = args.strip();
        try {
            return Integer.parseInt(numberText);
        } catch (NumberFormatException e) {
            throw new MyriadException(
                    "\"" + numberText + "\" is not a valid task number — it needs to be a whole "
                            + "number, e.g. \"mark 2\".");
        }
    }

    /**
     * Checks that {@code description} can be saved and read back unchanged.
     * The data file separates fields with {@code |}, so a description holding
     * one would be split apart on the next launch, losing text or the whole
     * task. Rejecting it here is simpler than escaping it in the save format,
     * and keeps existing data files readable.
     *
     * @param description the task description the user typed.
     * @throws MyriadException if {@code description} contains {@code |}.
     */
    private static void checkDescriptionCanBeSaved(String description) throws MyriadException {
        if (description.contains(SAVE_FIELD_SEPARATOR)) {
            throw new MyriadException(
                    "A task description can't contain \"" + SAVE_FIELD_SEPARATOR
                            + "\", because that character separates fields in the save file.");
        }
    }

    /**
     * Builds the {@link ToDo} described by a {@code todo} command's
     * arguments. Throws {@link MyriadException} if the description is
     * missing or cannot be saved.
     *
     * @param args the argument text after {@code todo}.
     * @return the new {@code ToDo}.
     * @throws MyriadException if the description is missing or contains {@code |}.
     */
    private static Task parseToDo(String args) throws MyriadException {
        if (args.isEmpty()) {
            throw new MyriadException(
                    "Please include a task description, e.g. \"todo read book\".");
        }
        checkDescriptionCanBeSaved(args);
        return new ToDo(args);
    }

    /**
     * Returns a pattern matching {@code marker} (e.g. {@code /by})
     * case-insensitively, together with any whitespace around it, so that
     * splitting on it also trims the text either side. This mirrors the
     * case-insensitive matching used for command keywords (e.g.
     * {@code deadline} itself).
     *
     * The marker is quoted, so that its characters are matched literally
     * rather than read as regular expression syntax, and the pattern is
     * compiled once, into a constant, rather than again on every line parsed.
     *
     * @param marker the marker text, e.g. {@code /by}.
     * @return the compiled pattern.
     */
    private static Pattern createMarkerPattern(String marker) {
        return Pattern.compile("\\s*" + Pattern.quote(marker) + "\\s*", Pattern.CASE_INSENSITIVE);
    }

    /**
     * Splits {@code text} at the first match of {@code marker}. Returns a
     * 1-element array holding all of the text if the marker isn't found, or
     * a 2-element array of the text before/after the marker if it is.
     *
     * @param text   the text to split.
     * @param marker the marker pattern to split on, e.g. {@link #MARKER_BY}.
     * @return a 1- or 2-element array, as described above.
     */
    private static String[] splitOnMarker(String text, Pattern marker) {
        return marker.split(text, 2);
    }

    /**
     * Builds the {@link Deadline} described by a
     * {@code deadline <description> /by <date>} command's arguments. Throws
     * {@link MyriadException} if the description or the date is missing, if
     * the description cannot be saved, or if the date doesn't parse.
     *
     * @param args the argument text after {@code deadline}.
     * @return the new {@code Deadline}.
     * @throws MyriadException if the description or date is missing or
     *                         unparseable, or the description contains {@code |}.
     */
    private static Task parseDeadline(String args) throws MyriadException {
        String[] descAndDate = splitOnMarker(args, MARKER_BY);
        String description = descAndDate[0];
        String date = descAndDate.length == 2 ? descAndDate[1] : null;

        String example = "deadline return book /by 2019-12-02";
        if (description.isBlank()) {
            throw new MyriadException(
                    "Please include a task description, e.g. \"" + example + "\".");
        } else if (date == null || date.isBlank()) {
            throw new MyriadException(
                    "Please include a date after /by, e.g. \"" + example + "\".");
        }
        checkDescriptionCanBeSaved(description);
        return new Deadline(description, TaskDateTime.parse(date));
    }

    /**
     * Builds the {@link Event} described by an
     * {@code event <description> /from <start> /to <end>} command's
     * arguments. Throws {@link MyriadException} if the description, start or
     * end is missing, if the description cannot be saved, if either date
     * doesn't parse, or if the event would end before or when it starts.
     *
     * @param args the argument text after {@code event}.
     * @return the new {@code Event}.
     * @throws MyriadException if the description, start or end is missing or
     *                         unparseable, the description contains {@code |},
     *                         or the end is not after the start.
     */
    private static Task parseEvent(String args) throws MyriadException {
        String[] descAndRest = splitOnMarker(args, MARKER_FROM);
        String description = descAndRest[0];
        String rest = descAndRest.length == 2 ? descAndRest[1] : "";

        String[] startAndEnd = splitOnMarker(rest, MARKER_TO);
        String start = startAndEnd[0];
        String end = startAndEnd.length == 2 ? startAndEnd[1] : null;

        String example = "event project meeting /from 2019-12-02 1400 /to 2019-12-02 1600";
        if (description.isBlank()) {
            throw new MyriadException(
                    "Please include a task description, e.g. \"" + example + "\".");
        } else if (start.isBlank()) {
            throw new MyriadException(
                    "Please include a start time after /from, e.g. \"" + example + "\".");
        } else if (end == null || end.isBlank()) {
            throw new MyriadException(
                    "Please include an end time after /to, e.g. \"" + example + "\".");
        }
        checkDescriptionCanBeSaved(description);
        TaskDateTime startTime = TaskDateTime.parse(start);
        TaskDateTime endTime = TaskDateTime.parse(end);
        Event.checkTimesInOrder(startTime, endTime);
        return new Event(description, startTime, endTime);
    }

    /**
     * Parses the date/time a {@code show} command asks about. Throws
     * {@link MyriadException} if it is missing or doesn't parse.
     *
     * @param args the argument text after {@code show}.
     * @return the date/time to search for.
     * @throws MyriadException if it is missing or unparseable.
     */
    private static TaskDateTime parseShowQuery(String args) throws MyriadException {
        String example = "show 2019-12-02 1800";

        if (args.isBlank()) {
            throw new MyriadException(
                    "Please include a date/time to show, e.g. \"" + example + "\".");
        }
        return TaskDateTime.parse(args.strip());
    }

    /**
     * Parses the keyword a {@code find} command searches for. Throws
     * {@link MyriadException} if it is missing.
     *
     * @param args the argument text after {@code find}.
     * @return the keyword to search for, stripped of surrounding whitespace.
     * @throws MyriadException if the keyword is missing or blank.
     */
    private static String parseFindKeyword(String args) throws MyriadException {
        if (args.isBlank()) {
            throw new MyriadException(
                    "Please include a keyword to find, e.g. \"find book\".");
        }
        return args.strip();
    }
}
