package sel.parser;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
import java.util.List;

import sel.command.CommandType;
import sel.exception.SelException;

/**
 * Deals with making sense of the user command: identifying the command
 * type and extracting the arguments it needs.
 *
 * <p>Every method here works on a <em>normalised</em> command (see
 * {@link #normalise(String)}): leading and trailing spaces removed, and
 * runs of whitespace collapsed to one space. Normalising once up front
 * means the rest of the parsing can assume tidy input, so
 * {@code "  mark   3 "} and {@code "mark 3"} are treated identically.
 */
public class Parser {
    /**
     * Format the user types dates in.
     *
     * <p>The pattern uses {@code uuuu} (not {@code yyyy}) together with
     * {@link ResolverStyle#STRICT} so that dates which do not exist are
     * rejected instead of being quietly adjusted. With the default SMART
     * resolver, {@code 2019-02-30 1800} is silently turned into Feb 28 and
     * {@code 2019-12-02 2400} into the next midnight, which would store a
     * date the user never typed.
     */
    private static final DateTimeFormatter INPUT_FORMAT =
            DateTimeFormatter.ofPattern("uuuu-MM-dd HHmm")
                    .withResolverStyle(ResolverStyle.STRICT);

    /** Reminder of the date format, appended to date-related errors. */
    private static final String DATE_FORMAT_HINT = "yyyy-MM-dd HHmm (e.g. 2019-12-02 1800)";

    /**
     * Character used by {@link sel.storage.Storage} to separate fields in
     * the save file. A description containing it would make the saved line
     * ambiguous, so it is rejected on the way in.
     */
    private static final String RESERVED_CHARACTER = "|";

    private static final String MISSING_DEADLINE_DESCRIPTION =
            "Bro, you need to tell me what's the task :(";
    private static final String MISSING_DEADLINE_TIME =
            "Bro, you need to tell me when's the deadline, like: deadline DESCRIPTION /by "
            + DATE_FORMAT_HINT;
    private static final String MISSING_EVENT_DESCRIPTION =
            "Bro, you need to tell me what's the event :(";
    private static final String MISSING_EVENT_START =
            "Bro, you need to tell me when's the start date/time, like: event DESCRIPTION /from "
            + DATE_FORMAT_HINT + " /to ...";
    private static final String MISSING_EVENT_END =
            "Bro, you need to tell me when's the end date/time, like: event DESCRIPTION /from ... /to "
            + DATE_FORMAT_HINT;

    /**
     * Tidies up a raw command line: removes leading and trailing spaces
     * and collapses every run of whitespace (spaces, tabs) into a single
     * space.
     *
     * @param fullCommand the raw command line typed by the user, possibly
     *     {@code null}.
     * @return the normalised command, or an empty string if the input was
     *     {@code null}.
     */
    public static String normalise(String fullCommand) {
        if (fullCommand == null) {
            return "";
        }
        return fullCommand.trim().replaceAll("\\s+", " ");
    }

    /**
     * Identifies the type of command the user typed, based on its first word.
     *
     * @param fullCommand the raw command line typed by the user.
     * @return the matching {@link CommandType}, or {@code CommandType.UNKNOWN}
     *     if the command word is not recognised.
     */
    public static CommandType parseCommandType(String fullCommand) {
        String normalised = normalise(fullCommand);
        String commandWord = normalised.isEmpty() ? "" : normalised.split(" ", 2)[0];

        switch (commandWord) {
            case "bye":
                return CommandType.BYE;
            case "list":
                return CommandType.LIST;
            case "mark":
                return CommandType.MARK;
            case "unmark":
                return CommandType.UNMARK;
            case "delete":
                return CommandType.DELETE;
            case "todo":
                return CommandType.TODO;
            case "deadline":
                return CommandType.DEADLINE;
            case "event":
                return CommandType.EVENT;
            case "find":
                return CommandType.FIND;
            default:
                return CommandType.UNKNOWN;
        }
    }

    /**
     * Checks that a command which takes no arguments was typed on its own,
     * so that a typo such as {@code "list all"} is reported instead of
     * being silently ignored.
     *
     * @param fullCommand the raw command line typed by the user.
     * @param commandWord the command word that should stand alone.
     * @throws SelException if anything was typed after the command word.
     */
    public static void requireNoArguments(String fullCommand, String commandWord)
            throws SelException {
        String argument = stripCommandWord(fullCommand, commandWord);
        if (!argument.isEmpty()) {
            throw new SelException("Bro, '" + commandWord + "' doesn't take anything after it, "
                    + "but you added '" + argument + "'. Just type '" + commandWord + "'.");
        }
    }

    /**
     * Extracts the (0-based) task index from a "mark"/"unmark"/"delete"
     * style command, e.g. {@code "mark 3"} becomes {@code 2}.
     *
     * @param fullCommand the raw command line typed by the user.
     * @param commandWord the command word to strip off (e.g. {@code "mark"}).
     * @param missingArgMessage message to use if no index was given at all.
     * @param invalidNumberMessage message to use if the given index is not
     *     a valid number.
     * @return the zero-based task index.
     * @throws SelException if the index is missing, not a whole number,
     *     given more than once, or too large to be a task number.
     */
    public static int parseIndex(String fullCommand, String commandWord,
            String missingArgMessage, String invalidNumberMessage) throws SelException {
        String argument = stripCommandWord(fullCommand, commandWord);

        if (argument.isEmpty()) {
            throw new SelException(missingArgMessage);
        }
        if (argument.contains(" ")) {
            throw new SelException("Bro, one task number at a time - I can't do '" + argument + "'.");
        }
        if (!argument.matches("\\d+")) {
            throw new SelException(invalidNumberMessage);
        }

        try {
            return Integer.parseInt(argument) - 1;
        } catch (NumberFormatException e) {
            // All digits, but too many of them to fit in an int.
            throw new SelException("Bro, you definitely don't have " + argument + " tasks :(");
        }
    }

    /**
     * Extracts a single free-text argument from a command, e.g.
     * {@code "todo read book"} becomes {@code "read book"}.
     *
     * @param fullCommand the raw command line typed by the user.
     * @param commandWord the command word to strip off (e.g. {@code "todo"}).
     * @param errorMessage message to use if no argument was given.
     * @return the trimmed argument text.
     * @throws SelException if the argument is missing or blank.
     */
    public static String parseSimpleArgument(String fullCommand, String commandWord,
            String errorMessage) throws SelException {
        String argument = stripCommandWord(fullCommand, commandWord);
        if (argument.isEmpty()) {
            throw new SelException(errorMessage);
        }
        return argument;
    }

    /**
     * Extracts a task description from a command, rejecting text that
     * cannot be stored safely.
     *
     * @param fullCommand the raw command line typed by the user.
     * @param commandWord the command word to strip off (e.g. {@code "todo"}).
     * @param errorMessage message to use if no description was given.
     * @return the description to store.
     * @throws SelException if the description is missing, blank, or
     *     contains a reserved character.
     */
    public static String parseDescription(String fullCommand, String commandWord,
            String errorMessage) throws SelException {
        return requireStorableDescription(
                parseSimpleArgument(fullCommand, commandWord, errorMessage));
    }

    /**
     * Extracts the description and deadline from a
     * {@code "deadline ... /by ..."} command.
     *
     * @param fullCommand the raw command line typed by the user.
     * @return a two-element array of {@code {description, deadline}}.
     * @throws SelException if the {@code /by} marker is missing, given more
     *     than once, or if the description or deadline text is missing.
     */
    public static String[] parseDeadlineArgs(String fullCommand) throws SelException {
        String arguments = stripCommandWord(fullCommand, "deadline");
        if (arguments.isEmpty()) {
            throw new SelException(MISSING_DEADLINE_DESCRIPTION);
        }

        List<Integer> byPositions = findMarkerPositions(arguments, "/by");
        if (byPositions.isEmpty()) {
            throw new SelException(missingByMessage(arguments));
        }
        if (byPositions.size() > 1) {
            throw new SelException("Bro, you gave me /by " + byPositions.size()
                    + " times. A deadline has exactly one.");
        }

        int byIndex = byPositions.get(0);
        String description = arguments.substring(0, byIndex).trim();
        String ddl = arguments.substring(byIndex + "/by".length()).trim();

        if (description.isEmpty()) {
            throw new SelException(MISSING_DEADLINE_DESCRIPTION);
        }
        if (ddl.isEmpty()) {
            throw new SelException(MISSING_DEADLINE_TIME);
        }

        return new String[] {requireStorableDescription(description), ddl};
    }

    /**
     * Extracts the description, start time, and end time from an
     * {@code "event ... /from ... /to ..."} command.
     *
     * @param fullCommand the raw command line typed by the user.
     * @return a three-element array of {@code {description, from, to}}.
     * @throws SelException if a marker is missing, given more than once, or
     *     out of order, or if the description, start time, or end time is
     *     missing.
     */
    public static String[] parseEventArgs(String fullCommand) throws SelException {
        String arguments = stripCommandWord(fullCommand, "event");
        if (arguments.isEmpty()) {
            throw new SelException(MISSING_EVENT_DESCRIPTION);
        }

        List<Integer> fromPositions = findMarkerPositions(arguments, "/from");
        List<Integer> toPositions = findMarkerPositions(arguments, "/to");

        if (fromPositions.isEmpty()) {
            throw new SelException(missingFromMessage(arguments));
        }
        if (toPositions.isEmpty()) {
            throw new SelException(MISSING_EVENT_END);
        }
        if (fromPositions.size() > 1) {
            throw new SelException("Bro, you gave me /from " + fromPositions.size()
                    + " times. An event has exactly one start.");
        }
        if (toPositions.size() > 1) {
            throw new SelException("Bro, you gave me /to " + toPositions.size()
                    + " times. An event has exactly one end.");
        }

        int fromIndex = fromPositions.get(0);
        int toIndex = toPositions.get(0);
        if (toIndex < fromIndex) {
            throw new SelException("Bro, /from comes before /to: "
                    + "event DESCRIPTION /from START /to END.");
        }

        String description = arguments.substring(0, fromIndex).trim();
        String from = arguments.substring(fromIndex + "/from".length(), toIndex).trim();
        String to = arguments.substring(toIndex + "/to".length()).trim();

        if (description.isEmpty()) {
            throw new SelException(MISSING_EVENT_DESCRIPTION);
        }
        if (from.isEmpty()) {
            throw new SelException(MISSING_EVENT_START);
        }
        if (to.isEmpty()) {
            throw new SelException(MISSING_EVENT_END);
        }

        return new String[] {requireStorableDescription(description), from, to};
    }

    /**
     * Parses a user-typed date/time string (e.g. {@code "2019-12-02 1800"})
     * into a {@link LocalDateTime}.
     *
     * @param input the raw date/time text typed by the user.
     * @return the parsed date/time.
     * @throws SelException if the text is blank, does not match the
     *     expected {@code yyyy-MM-dd HHmm} format, or names a date that
     *     does not exist (e.g. {@code 2019-02-30}).
     */
    public static LocalDateTime parseDateTime(String input) throws SelException {
        String normalised = normalise(input);
        if (normalised.isEmpty()) {
            throw new SelException("Bro, I need a date/time in the format " + DATE_FORMAT_HINT + ".");
        }

        try {
            return LocalDateTime.parse(normalised, INPUT_FORMAT);
        } catch (DateTimeParseException e) {
            // Covers both causes at once: the text may be in the wrong
            // shape, or it may be well-formed but name a day that does not
            // exist, such as 2019-02-30.
            throw new SelException("Bro, I can't read '" + normalised + "' as a date/time - "
                    + "either the format is off or that date doesn't exist. Use "
                    + DATE_FORMAT_HINT + ".");
        }
    }

    /**
     * Removes the command word from the front of a command and returns
     * whatever is left.
     *
     * @param fullCommand the raw command line typed by the user.
     * @param commandWord the command word to remove.
     * @return the arguments after the command word, possibly empty.
     */
    private static String stripCommandWord(String fullCommand, String commandWord) {
        String normalised = normalise(fullCommand);

        if (normalised.equals(commandWord)) {
            return "";
        }
        if (normalised.startsWith(commandWord + " ")) {
            return normalised.substring(commandWord.length() + 1).trim();
        }

        // The command word should always be there, because parseCommandType
        // matched it first. Returning the whole line rather than blindly
        // calling substring keeps an unexpected caller from triggering a
        // StringIndexOutOfBoundsException.
        return normalised;
    }

    /**
     * Finds every position at which a marker such as {@code /by} appears as
     * a word of its own.
     *
     * <p>Requiring a whole word means a description like
     * {@code "event flight /from SIN /to KUL"} is split on the real markers
     * only: {@code "/tomorrow"} is not mistaken for {@code "/to"}, and
     * {@code "and/or"} is left alone.
     *
     * @param arguments the normalised argument text to search.
     * @param marker the marker to look for, e.g. {@code "/by"}.
     * @return the starting positions of the marker, in order.
     */
    private static List<Integer> findMarkerPositions(String arguments, String marker) {
        List<Integer> positions = new ArrayList<>();
        int searchFrom = 0;

        // indexOf returns -1 once there is nothing left to find, which is
        // what ends the loop; searchFrom can never run past the end.
        while (true) {
            int index = arguments.indexOf(marker, searchFrom);
            if (index < 0) {
                break;
            }
            if (isWholeWord(arguments, index, marker.length())) {
                positions.add(index);
            }
            searchFrom = index + marker.length();
        }

        return positions;
    }

    /**
     * Checks whether the text at the given position stands alone as a word.
     * The arguments are normalised, so neighbouring whitespace is always a
     * single space.
     *
     * @param arguments the normalised argument text.
     * @param index where the candidate word starts.
     * @param length how long the candidate word is.
     * @return {@code true} if the candidate is bounded by spaces or by the
     *     ends of the text.
     */
    private static boolean isWholeWord(String arguments, int index, int length) {
        boolean spaceBefore = index == 0 || arguments.charAt(index - 1) == ' ';
        int afterIndex = index + length;
        boolean spaceAfter = afterIndex == arguments.length()
                || arguments.charAt(afterIndex) == ' ';
        return spaceBefore && spaceAfter;
    }

    /**
     * Rejects descriptions that cannot be written to the save file without
     * making the saved line ambiguous.
     *
     * @param description the description the user typed.
     * @return the same description, if it is safe to store.
     * @throws SelException if the description contains a reserved character.
     */
    private static String requireStorableDescription(String description) throws SelException {
        if (description.contains(RESERVED_CHARACTER)) {
            throw new SelException("Bro, I can't put '" + RESERVED_CHARACTER
                    + "' in a description - that's what separates the fields in my save file. "
                    + "Pick another character.");
        }
        return description;
    }

    /**
     * Builds the error message for a {@code deadline} with no {@code /by},
     * pointing out the mix-up if event markers were used instead.
     *
     * @param arguments the normalised argument text.
     * @return the message to show the user.
     */
    private static String missingByMessage(String arguments) {
        boolean usedEventMarkers = !findMarkerPositions(arguments, "/from").isEmpty()
                || !findMarkerPositions(arguments, "/to").isEmpty();

        if (usedEventMarkers) {
            return "Bro, a deadline is marked with /by, not /from or /to. "
                    + "Did you mean to add an event?";
        }
        return MISSING_DEADLINE_TIME;
    }

    /**
     * Builds the error message for an {@code event} with no {@code /from},
     * pointing out the mix-up if the deadline marker was used instead.
     *
     * @param arguments the normalised argument text.
     * @return the message to show the user.
     */
    private static String missingFromMessage(String arguments) {
        if (!findMarkerPositions(arguments, "/by").isEmpty()) {
            return "Bro, an event is marked with /from and /to, not /by. "
                    + "Did you mean to add a deadline?";
        }
        return MISSING_EVENT_START;
    }
}
