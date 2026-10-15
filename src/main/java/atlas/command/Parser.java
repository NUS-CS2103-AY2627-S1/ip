package atlas.command;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import atlas.AtlasException;
import atlas.client.Client;
import atlas.task.Deadline;
import atlas.task.Event;
import atlas.task.Priority;
import atlas.task.Task;
import atlas.task.Todo;

/**
 * Makes sense of user commands: recognises the command word, and
 * extracts task details, client details or indexes from the rest of the line.
 */
public class Parser {

    /** Syntax of the priority command, repeated in its error messages. */
    public static final String PRIORITY_SYNTAX = "priority <number> <high|medium|low|none>";
    /** Level word that removes a task's priority. */
    public static final String PRIORITY_NONE_WORD = "none";

    /** Separator that introduces the deadline date in a deadline command. */
    private static final String BY_MARKER = " /by ";
    /** Separator that introduces the start of an event in an event command. */
    private static final String FROM_MARKER = " /from ";
    /** Separator that introduces the end of an event in an event command. */
    private static final String TO_MARKER = " /to ";
    /** Separator that introduces a client's phone number. */
    private static final String PHONE_MARKER = " /phone ";
    /** Separator that introduces a client's email address. */
    private static final String EMAIL_MARKER = " /email ";

    /** Syntax of the deadline command, repeated in its error messages. */
    private static final String DEADLINE_SYNTAX = "deadline <desc> /by <when>";
    /** Syntax of the event command, repeated in its error messages. */
    private static final String EVENT_SYNTAX = "event <desc> /from <start> /to <end>";

    /** Clock time written with a colon, e.g. "14:00" or "2:30". */
    private static final Pattern COLON_TIME = Pattern.compile("(\\d{1,2}):(\\d{2})");
    /** Clock time written as three or four digits only, e.g. "1400" or "900". */
    private static final Pattern COMPACT_TIME = Pattern.compile("\\d{3,4}");

    /** Length of a date written as yyyy-mm-dd. */
    private static final int ISO_DATE_LENGTH = 10;

    /**
     * A date at the start of a value, written year first as yyyy-mm-dd or day
     * first as d/m/yyyy or d-m/yyyy. Only the date is matched, so a value such
     * as "18/9/2026 1000am" leaves its clock time to be read separately.
     */
    private static final Pattern LEADING_DATE = Pattern.compile(
            "^(\\d{4}-\\d{2}-\\d{2}|\\d{1,2}/\\d{1,2}/\\d{4}|\\d{1,2}-\\d{1,2}-\\d{4})");

    /** Message shown when a priority command carries no task number. */
    private static final String MISSING_PRIORITY_NUMBER_MESSAGE =
            "Which labour shall I rank? Use: " + PRIORITY_SYNTAX;
    /** Message shown when a priority command carries no level word. */
    private static final String MISSING_PRIORITY_LEVEL_MESSAGE =
            "Rank it high, medium or low, or none to clear it. Use: " + PRIORITY_SYNTAX;
    /** Message shown when a priority command names no known level. */
    private static final String UNKNOWN_PRIORITY_LEVEL_MESSAGE =
            "The Fates know only high, medium, low or none. Use: " + PRIORITY_SYNTAX;

    /** Syntax of the client add command, repeated in its error messages. */
    private static final String CLIENT_ADD_SYNTAX = "client add <name> [/phone <number>] [/email <address>]";
    /** Message shown when a client subcommand is missing or unrecognised. */
    private static final String CLIENT_USAGE_MESSAGE = "The Oracle is silent on that client command. Use: "
            + CLIENT_ADD_SYNTAX + ", client list, client find <keyword>, client delete <number>";
    /** Message shown when a client is added without a name. */
    private static final String CLIENT_NAME_MISSING_MESSAGE = "Name your client, mortal: " + CLIENT_ADD_SYNTAX;

    /** Length of the "client " prefix, derived from the command word. */
    private static final int CLIENT_PREFIX_LENGTH = Command.CLIENT.getWord().length() + 1;
    /** Length of the "client add " prefix, derived from the command words. */
    private static final int CLIENT_ADD_PREFIX_LENGTH =
            CLIENT_PREFIX_LENGTH + ClientCommand.ADD.getWord().length() + 1;
    /** Length of the "client find" prefix, derived from the command words. */
    private static final int CLIENT_FIND_PREFIX_LENGTH =
            CLIENT_PREFIX_LENGTH + ClientCommand.FIND.getWord().length();
    /** Length of the "client delete " prefix, derived from the command words. */
    private static final int CLIENT_DELETE_PREFIX_LENGTH =
            CLIENT_PREFIX_LENGTH + ClientCommand.DELETE.getWord().length() + 1;

    /**
     * Returns the command a line starts with, or null if the line is not a command.
     * A line matches a command when it is exactly the command word, or the
     * command word followed by a space and arguments.
     *
     * @param line input line to inspect.
     * @return command identified at the start of the line, or {@code null}.
     */
    public static Command parseCommand(String line) {
        // Leading whitespace is insignificant, so an indented command is still
        // recognised. Trailing whitespace keeps its meaning: a marker's closing
        // space is what proves a value was left empty.
        String text = line.stripLeading();
        for (Command cmd : Command.values()) {
            if (text.equals(cmd.getWord()) || text.startsWith(cmd.getWord() + " ")) {
                return cmd;
            }
        }
        return null;
    }

    /**
     * Parses the number after a mark/unmark/delete command.
     * Returns the 1-based task number, or -1 if it is not a number.
     *
     * @param line input line containing the command and task number.
     * @param cmd command whose argument should be parsed.
     * @return parsed 1-based task number, or {@code -1} when invalid.
     */
    public static int parseIndex(String line, Command cmd) {
        int prefixLength = cmd.getWord().length() + 1;
        if (line.length() < prefixLength) {
            return -1;
        }
        return parseNumber(line.substring(prefixLength));
    }

    /**
     * Parses a number out of argument text.
     *
     * @param text text that should hold a number.
     * @return the parsed number, or {@code -1} when the text is not a number.
     */
    public static int parseNumber(String text) {
        try {
            return Integer.parseInt(text.trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    /**
     * Parses the keywords after a find command, one per whitespace-separated
     * word, e.g. "find book paper" yields two keywords.
     *
     * @param line input line containing the find command and keywords.
     * @param cmd find command whose argument should be parsed.
     * @return the keywords to search for.
     * @throws AtlasException if no keyword was supplied.
     */
    public static String[] parseKeywords(String line, Command cmd) throws AtlasException {
        String remainder = line.substring(cmd.getWord().length()).trim();
        if (remainder.isEmpty()) {
            throw new AtlasException("What shall I seek, mortal? Use: find <keyword>");
        }
        return remainder.split("\\s+");
    }

    /**
     * Parses the task number at the start of a priority command, e.g. the 2 in
     * "priority 2 high". Text after the number is ignored here, because
     * {@link #parsePriorityLevel} is what validates it.
     *
     * @param line input line containing a priority command.
     * @return parsed 1-based task number, or {@code -1} when the leading
     *     argument is not a number.
     * @throws AtlasException if the command carries no argument at all.
     */
    public static int parsePriorityNumber(String line) throws AtlasException {
        String argument = textAfterCommand(line, Command.PRIORITY);
        if (argument.isEmpty()) {
            throw new AtlasException(MISSING_PRIORITY_NUMBER_MESSAGE);
        }
        return parseNumber(firstToken(argument));
    }

    /**
     * Parses the level word after a priority command, e.g. {@code high} in
     * "priority 2 high". The level word is the only text allowed after the
     * task number.
     *
     * @param line input line containing a priority command.
     * @return the level word, which is either a priority level or
     *     {@link #PRIORITY_NONE_WORD} to remove the priority.
     * @throws AtlasException if no level word was supplied or the word names
     *     no level Atlas knows.
     */
    public static String parsePriorityLevel(String line) throws AtlasException {
        String level = afterFirstToken(textAfterCommand(line, Command.PRIORITY));
        if (level.isEmpty()) {
            throw new AtlasException(MISSING_PRIORITY_LEVEL_MESSAGE);
        }
        if (!level.equals(PRIORITY_NONE_WORD) && Priority.fromWord(level) == null) {
            throw new AtlasException(UNKNOWN_PRIORITY_LEVEL_MESSAGE);
        }
        return level;
    }

    /**
     * Returns the trimmed text that follows a command word, or an empty string
     * when the line holds nothing but the command.
     *
     * @param line input line.
     * @param cmd command whose word starts the line.
     * @return the remaining text.
     */
    private static String textAfterCommand(String line, Command cmd) {
        int prefixLength = cmd.getWord().length();
        return line.length() <= prefixLength ? "" : line.substring(prefixLength).trim();
    }

    /**
     * Returns the first token of some text, where any whitespace character
     * separates tokens. Treating a tab as a separator matches how the rest of
     * the parser, and {@link #parseNumber}, handle whitespace.
     *
     * @param text text to split.
     * @return the first token, or the whole text when it holds no whitespace.
     */
    private static String firstToken(String text) {
        int separator = indexOfWhitespace(text);
        return separator == -1 ? text : text.substring(0, separator);
    }

    /**
     * Returns the text that follows the first token.
     *
     * @param text text to split.
     * @return the remaining text, trimmed, or an empty string when there is none.
     */
    private static String afterFirstToken(String text) {
        int separator = indexOfWhitespace(text);
        return separator == -1 ? "" : text.substring(separator + 1).trim();
    }

    /**
     * Returns the position of the first whitespace character in some text.
     *
     * @param text text to scan.
     * @return the zero-based position, or {@code -1} when there is no whitespace.
     */
    private static int indexOfWhitespace(String text) {
        for (int i = 0; i < text.length(); i++) {
            if (Character.isWhitespace(text.charAt(i))) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Counts how many times a marker appears in some text, scanning without
     * overlap. Used to reject a command that names the same parameter twice.
     *
     * @param text text to scan.
     * @param marker marker to look for.
     * @return the number of occurrences.
     */
    private static int countMarkers(String text, String marker) {
        int count = 0;
        int position = text.indexOf(marker);
        while (position != -1) {
            count++;
            position = text.indexOf(marker, position + marker.length());
        }
        return count;
    }

    /**
     * Returns the minute of the day a plain clock time stands for, or null when
     * the text is not a clock time. Accepted forms are "2pm", "2 pm", "10am",
     * "2:30pm", "1000am", "14:00" and "1400".
     *
     * <p>Free text such as "7pm at marina" is not a clock time, so callers read a
     * null result as "these two values cannot be compared" rather than as an error.
     *
     * @param text value written after a /from or /to marker.
     * @return minutes since midnight, or {@code null} when the text is not a
     *     clock time.
     */
    private static Integer minuteOfDayOrNull(String text) {
        String value = text.trim().toLowerCase(Locale.ROOT);
        String suffix = meridianSuffix(value);
        String clock = suffix.isEmpty() ? value : value.substring(0, value.length() - 2).trim();
        return minuteOfDay(clock, suffix);
    }

    /**
     * Returns the am/pm marker that ends some text, if the text carries one.
     *
     * @param value text already trimmed and lower-cased.
     * @return "am", "pm", or an empty string when the text carries no marker.
     */
    private static String meridianSuffix(String value) {
        return value.endsWith("am") || value.endsWith("pm") ? value.substring(value.length() - 2) : "";
    }

    /**
     * Reads a clock time written without its am/pm marker, e.g. "14:00", "1400"
     * or "2" alongside a "pm" suffix.
     *
     * @param clock text of the clock time, without an am/pm marker.
     * @param suffix the marker the text carried, or an empty string.
     * @return minutes since midnight, or {@code null} when the text is not a
     *     clock time.
     */
    private static Integer minuteOfDay(String clock, String suffix) {
        int hours;
        int minutes;
        Matcher colon = COLON_TIME.matcher(clock);
        if (colon.matches()) {
            hours = Integer.parseInt(colon.group(1));
            minutes = Integer.parseInt(colon.group(2));
            if (minutes > 59) {
                return null;
            }
        } else if (suffix.isEmpty() && COMPACT_TIME.matcher(clock).matches()) {
            // "1400" reads as 14:00, but a bare "2" or "230" is too ambiguous to read.
            String digits = clock.length() == 3 ? "0" + clock : clock;
            hours = Integer.parseInt(digits.substring(0, 2));
            minutes = Integer.parseInt(digits.substring(2));
            if (minutes > 59) {
                return null;
            }
        } else if (suffix.isEmpty() || !isAllDigits(clock)) {
            return null;
        } else if (clock.length() <= 2) {
            hours = Integer.parseInt(clock);
            minutes = 0;
        } else {
            // A 12-hour clock written compactly, e.g. "1000am" or "1030pm".
            String digits = clock.length() == 3 ? "0" + clock : clock;
            hours = Integer.parseInt(digits.substring(0, 2));
            minutes = Integer.parseInt(digits.substring(2));
            if (minutes > 59) {
                return null;
            }
        }
        return applyMeridian(hours, minutes, suffix);
    }

    /**
     * Converts an hour read from either clock into minutes since midnight: an
     * hour that carries an am/pm marker is a 12-hour hour, and one that does not
     * is a 24-hour hour.
     *
     * @param hours hour read from the text.
     * @param minutes minutes read from the text.
     * @param suffix the am/pm marker the text carried, or an empty string.
     * @return minutes since midnight, or {@code null} when the hour is out of
     *     range for the clock it was written on.
     */
    private static Integer applyMeridian(int hours, int minutes, String suffix) {
        if (!suffix.isEmpty()) {
            if (hours < 1 || hours > 12) {
                return null;
            }
            int converted = hours % 12 + (suffix.equals("pm") ? 12 : 0);
            return converted * 60 + minutes;
        }
        if (hours > 23) {
            return null;
        }
        return hours * 60 + minutes;
    }

    /**
     * Returns whether some text holds only decimal digits.
     *
     * @param text text to inspect.
     * @return {@code true} when the text is non-empty and every character is a digit.
     */
    private static boolean isAllDigits(String text) {
        if (text.isEmpty()) {
            return false;
        }
        for (int i = 0; i < text.length(); i++) {
            if (!Character.isDigit(text.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns the client subcommand a line asks for, e.g. ADD for
     * "client add Bob".
     *
     * @param line input line containing a client command.
     * @return the client subcommand named in the line.
     * @throws AtlasException if the subcommand is missing or unrecognised.
     */
    public static ClientCommand parseClientSubcommand(String line) throws AtlasException {
        if (line.length() <= CLIENT_PREFIX_LENGTH) {
            throw new AtlasException(CLIENT_USAGE_MESSAGE);
        }
        String remainder = line.substring(CLIENT_PREFIX_LENGTH);
        for (ClientCommand sub : ClientCommand.values()) {
            if (remainder.equals(sub.getWord()) || remainder.startsWith(sub.getWord() + " ")) {
                return sub;
            }
        }
        throw new AtlasException(CLIENT_USAGE_MESSAGE);
    }

    /**
     * Parses a client add command into a Client. The name is required, while
     * " /phone " and " /email " are optional and may appear in either order.
     *
     * @param line input line containing a client add command.
     * @return client represented by the line.
     * @throws AtlasException if the name is missing or a marker has no value.
     */
    public static Client parseClient(String line) throws AtlasException {
        String remainder = line.length() <= CLIENT_ADD_PREFIX_LENGTH
                ? "" : line.substring(CLIENT_ADD_PREFIX_LENGTH);
        // A space on each side lets a marker at either end of the line be
        // recognised, so "client add Bob /phone" reports a missing value
        // instead of treating "/phone" as part of the name, and
        // "client add /phone 1" reports a missing name.
        String text = " " + remainder + " ";
        int phonePos = text.indexOf(PHONE_MARKER);
        int emailPos = text.indexOf(EMAIL_MARKER);
        int firstMarker = earlierMarker(phonePos, emailPos);
        String name = (firstMarker == -1 ? text : text.substring(0, firstMarker)).trim();
        if (name.isEmpty()) {
            throw new AtlasException(CLIENT_NAME_MISSING_MESSAGE);
        }
        if (countMarkers(text, PHONE_MARKER) > 1) {
            throw new AtlasException("One number is enough, mortal. Use: " + CLIENT_ADD_SYNTAX);
        }
        if (countMarkers(text, EMAIL_MARKER) > 1) {
            throw new AtlasException("One address is enough, mortal. Use: " + CLIENT_ADD_SYNTAX);
        }
        String phone = valueAfterMarker(text, phonePos, emailPos, PHONE_MARKER);
        if (phonePos != -1 && phone.isEmpty()) {
            throw new AtlasException("A number must follow /phone. Use: " + CLIENT_ADD_SYNTAX);
        }
        String email = valueAfterMarker(text, emailPos, phonePos, EMAIL_MARKER);
        if (emailPos != -1 && email.isEmpty()) {
            throw new AtlasException("An address must follow /email. Use: " + CLIENT_ADD_SYNTAX);
        }
        return new Client(name, phone, email);
    }

    /**
     * Parses the keywords after a client find command, one per
     * whitespace-separated word.
     *
     * @param line input line containing a client find command.
     * @return the keywords to search for.
     * @throws AtlasException if no keyword was supplied.
     */
    public static String[] parseClientKeywords(String line) throws AtlasException {
        String remainder = line.length() <= CLIENT_FIND_PREFIX_LENGTH
                ? "" : line.substring(CLIENT_FIND_PREFIX_LENGTH).trim();
        if (remainder.isEmpty()) {
            throw new AtlasException("Whom shall I seek, mortal? Use: client find <keyword>");
        }
        return remainder.split("\\s+");
    }

    /**
     * Returns the argument text after a client delete command.
     *
     * @param line input line containing a client delete command.
     * @return the trimmed argument, or an empty string when none was supplied.
     */
    public static String parseClientDeleteArgument(String line) {
        return line.length() <= CLIENT_DELETE_PREFIX_LENGTH
                ? "" : line.substring(CLIENT_DELETE_PREFIX_LENGTH).trim();
    }

    /**
     * Returns whichever marker appears earlier in the text.
     *
     * @param firstPos position of one marker, or -1 when it is absent.
     * @param secondPos position of the other marker, or -1 when it is absent.
     * @return the earlier position, or -1 when neither marker is present.
     */
    private static int earlierMarker(int firstPos, int secondPos) {
        if (firstPos == -1) {
            return secondPos;
        }
        if (secondPos == -1) {
            return firstPos;
        }
        return Math.min(firstPos, secondPos);
    }

    /**
     * Returns the value that follows one marker, running either to the other
     * marker or to the end of the text.
     *
     * @param text text being parsed.
     * @param markerPos position of the marker whose value is wanted, or -1.
     * @param otherMarkerPos position of the other marker, or -1.
     * @param marker the marker whose value is wanted.
     * @return the trimmed value, or an empty string when the marker is absent.
     */
    private static String valueAfterMarker(String text, int markerPos, int otherMarkerPos, String marker) {
        if (markerPos == -1) {
            return "";
        }
        int valueEnd = text.length();
        if (otherMarkerPos > markerPos) {
            // The two markers can share the single space between them, as in
            // "client add B /phone /email x@y.com", where the space that ends
            // " /phone " also starts " /email ". Ending the value at that
            // shared space would place the end before the value began, so the
            // end is never allowed to fall inside the marker itself.
            valueEnd = Math.max(otherMarkerPos, markerPos + marker.length());
        }
        return text.substring(markerPos + marker.length(), valueEnd).trim();
    }

    /**
     * Parses a todo/deadline/event command line into a Task.
     * Throws AtlasException (with an explanation) if the input is malformed.
     *
     * @param line input line containing a task command.
     * @param cmd task command to parse.
     * @return task represented by the line.
     * @throws AtlasException if the input is malformed.
     */
    public static Task parseTask(String line, Command cmd) throws AtlasException {
        int prefixLen = cmd.getWord().length() + 1; // word plus the separating space, e.g. "todo "
        return switch (cmd) {
            case TODO -> parseTodoCommand(line, prefixLen);
            case DEADLINE -> parseDeadlineCommand(line, prefixLen);
            case EVENT -> parseEventCommand(line, prefixLen);
            default -> throw new AssertionError("Not a task command: " + cmd);
        };
    }

    /**
     * Parses a todo command line, whose description is the whole argument.
     *
     * @param line input line containing a todo command.
     * @param prefixLen length of the command word plus its separating space.
     * @return the todo represented by the line.
     * @throws AtlasException if the description is missing.
     */
    private static Task parseTodoCommand(String line, int prefixLen) throws AtlasException {
        // A line that holds nothing but the command word is one character
        // shorter than the prefix, and so carries no description.
        String desc = line.length() == prefixLen - 1 ? "" : line.substring(prefixLen);
        if (desc.trim().isEmpty()) {
            throw new AtlasException("Name your labour, mortal: todo <desc>");
        }
        return new Todo(desc);
    }

    /**
     * Parses a deadline command line, which needs a description and a /by date.
     *
     * @param line input line containing a deadline command.
     * @param prefixLen length of the command word plus its separating space.
     * @return the deadline represented by the line.
     * @throws AtlasException if a field is missing, repeated or unreadable.
     */
    private static Task parseDeadlineCommand(String line, int prefixLen) throws AtlasException {
        if (countMarkers(line, BY_MARKER) > 1) {
            throw new AtlasException("One reckoning is enough, mortal. Use: " + DEADLINE_SYNTAX);
        }
        int byPos = line.indexOf(BY_MARKER);
        if (byPos == -1) {
            throw new AtlasException("The Fates weave on schedule. Use: " + DEADLINE_SYNTAX);
        }
        String desc = descriptionBefore(line, prefixLen, byPos, DEADLINE_SYNTAX);
        assert byPos > prefixLen : "byPos must point past the command prefix";
        LocalDate by = parseByDate(line, byPos);
        return new Deadline(desc, by);
    }

    /**
     * Reads the date that follows a /by marker.
     *
     * @param line input line containing a deadline command.
     * @param byPos position of the /by marker.
     * @return the date written after the marker.
     * @throws AtlasException if the date is missing or is not a date.
     */
    private static LocalDate parseByDate(String line, int byPos) throws AtlasException {
        String byText = line.substring(byPos + BY_MARKER.length());
        if (byText.trim().isEmpty()) {
            throw new AtlasException("The Fates weave on schedule. Use: " + DEADLINE_SYNTAX);
        }
        try {
            return LocalDate.parse(byText.trim());
        } catch (DateTimeParseException e) {
            throw new AtlasException("The Fates cannot read that date, mortal. "
                    + "Use: deadline <desc> /by yyyy-mm-dd");
        }
    }

    /**
     * Parses an event command line, which needs a description and a /from and
     * /to value.
     *
     * @param line input line containing an event command.
     * @param prefixLen length of the command word plus its separating space.
     * @return the event represented by the line.
     * @throws AtlasException if a field is missing, repeated or carries the
     *     same time of day at both ends.
     */
    private static Task parseEventCommand(String line, int prefixLen) throws AtlasException {
        if (countMarkers(line, FROM_MARKER) > 1) {
            throw new AtlasException("One departure is enough, mortal. Use: " + EVENT_SYNTAX);
        }
        if (countMarkers(line, TO_MARKER) > 1) {
            throw new AtlasException("One landing is enough, mortal. Use: " + EVENT_SYNTAX);
        }
        int fromPos = line.indexOf(FROM_MARKER);
        if (fromPos == -1) {
            throw new AtlasException("Even Icarus launched from somewhere. "
                    + "Use: " + EVENT_SYNTAX);
        }
        int toPos = line.indexOf(TO_MARKER, fromPos);
        if (toPos == -1) {
            throw new AtlasException("Icarus never planned a landing either. "
                    + "Use: " + EVENT_SYNTAX);
        }
        String desc = descriptionBefore(line, prefixLen, fromPos, EVENT_SYNTAX);
        assert fromPos > prefixLen : "fromPos must point past the command prefix";
        assert toPos > fromPos : "/to must come after /from";
        String from = eventStart(line, fromPos, toPos);
        String to = eventEnd(line, toPos);
        requireOrderedRange(from, to);
        return new Event(desc, from, to);
    }

    /**
     * Reads the value that starts an event, which runs from its /from marker to
     * the /to marker.
     *
     * @param line input line containing an event command.
     * @param fromPos position of the /from marker.
     * @param toPos position of the /to marker.
     * @return the start value written after the marker.
     * @throws AtlasException if the start value is missing.
     */
    private static String eventStart(String line, int fromPos, int toPos) throws AtlasException {
        int fromStart = fromPos + FROM_MARKER.length();
        // The space that ends " /from " can also begin " /to ", as in
        // "event x /from /to y". Ending the value at that shared space
        // would place the end before the value began, so the end is
        // never allowed to fall inside the marker itself.
        String from = line.substring(fromStart, Math.max(toPos, fromStart));
        if (from.trim().isEmpty()) {
            throw new AtlasException("Even Icarus launched from somewhere. "
                    + "Use: " + EVENT_SYNTAX);
        }
        return from;
    }

    /**
     * Reads the value that ends an event.
     *
     * @param line input line containing an event command.
     * @param toPos position of the /to marker.
     * @return the end value written after the marker.
     * @throws AtlasException if the end value is missing.
     */
    private static String eventEnd(String line, int toPos) throws AtlasException {
        String to = line.substring(toPos + TO_MARKER.length());
        if (to.trim().isEmpty()) {
            throw new AtlasException("Icarus never planned a landing either. "
                    + "Use: " + EVENT_SYNTAX);
        }
        return to;
    }

    /**
     * Rejects an event whose start and end fall in an order Atlas can rule out.
     * The two values are compared only when Atlas can read the same kind of time
     * from both: a date from each. Free text, and a clock time with no date, are
     * left to the same-time check below, so a value Atlas cannot read never
     * fails a command.
     *
     * @param from start value written after the /from marker.
     * @param to end value written after the /to marker.
     * @throws AtlasException if the end falls before the start, or if both
     *     values name the same time.
     */
    private static void requireOrderedRange(String from, String to) throws AtlasException {
        LocalDate startDate = leadingDateOrNull(from);
        LocalDate endDate = leadingDateOrNull(to);
        if (startDate == null || endDate == null) {
            requireDistinctTimes(from, to);
            return;
        }
        requireDatedOrder(from, to, startDate, endDate);
    }

    /**
     * Rejects a dated event that ends before it starts. Two values are ordered
     * by date, and two values on the same date by the hour each one carries, so
     * a same-day event runs forwards and an event that runs past midnight uses
     * the next day's date.
     *
     * @param from start value written after the /from marker.
     * @param to end value written after the /to marker.
     * @param startDate date read from the start value.
     * @param endDate date read from the end value.
     * @throws AtlasException if the end falls before the start, or if both
     *     values name the same time.
     */
    private static void requireDatedOrder(String from, String to, LocalDate startDate, LocalDate endDate)
            throws AtlasException {
        if (endDate.isBefore(startDate)) {
            throw new AtlasException("Time flows one way, mortal: an event that "
                    + "ends before it begins is no event. Use: " + EVENT_SYNTAX);
        }
        if (endDate.isAfter(startDate)) {
            return;
        }
        Integer startMinutes = clockPartOrNull(from);
        Integer endMinutes = clockPartOrNull(to);
        if (startMinutes == null || endMinutes == null) {
            return;
        }
        if (startMinutes.equals(endMinutes)) {
            throw new AtlasException("Time flows one way, mortal: an event that "
                    + "begins and ends together is no event. Use: " + EVENT_SYNTAX);
        }
        if (startMinutes > endMinutes) {
            throw new AtlasException("Time flows one way, mortal: an event that "
                    + "ends before it begins is no event. Use: " + EVENT_SYNTAX);
        }
    }

    /**
     * Returns the date that a value starts with, when that date is written as
     * yyyy-mm-dd, d/m/yyyy or d-m-yyyy.
     *
     * @param value text written after a marker.
     * @return the date, or {@code null} when the text starts with no date.
     * @throws AtlasException if the text starts with a date-shaped value that
     *     names no real day, such as 31/2/2026.
     */
    private static LocalDate leadingDateOrNull(String value) throws AtlasException {
        int length = leadingDateLength(value);
        if (length == 0) {
            return null;
        }
        String text = value.trim().substring(0, length);
        try {
            return parseLeadingDate(text);
        } catch (DateTimeException e) {
            throw new AtlasException("The Fates know no such date as " + text
                    + ". Use a real date, as yyyy-mm-dd, d/m/yyyy or d-m-yyyy.");
        }
    }

    /**
     * Returns how many characters of a value its leading date takes up.
     *
     * @param value text written after a marker.
     * @return the length of the leading date, or 0 when the text starts with no
     *     date.
     */
    private static int leadingDateLength(String value) {
        Matcher matcher = LEADING_DATE.matcher(value.trim());
        return matcher.find() ? matcher.group(1).length() : 0;
    }

    /**
     * Reads a date written either year first, as yyyy-mm-dd, or day first, as
     * d/m/yyyy or d-m-yyyy.
     *
     * @param text text of the date alone.
     * @return the date the text names.
     * @throws DateTimeException if the text names no real date.
     */
    private static LocalDate parseLeadingDate(String text) {
        if (text.length() == ISO_DATE_LENGTH && text.charAt(4) == '-') {
            return LocalDate.parse(text);
        }
        String[] parts = text.split("[/-]");
        return LocalDate.of(Integer.parseInt(parts[2]),
                Integer.parseInt(parts[1]), Integer.parseInt(parts[0]));
    }

    /**
     * Returns the clock time that a value carries after its date, e.g. the
     * "1400" of "2026-12-01 1400" or the "1000am" of "18/9/2026 1000am".
     *
     * @param value text written after a marker.
     * @return minutes since midnight, or {@code null} when the text carries no
     *     clock time Atlas can read.
     */
    private static Integer clockPartOrNull(String value) {
        int length = leadingDateLength(value);
        String text = value.trim();
        if (length == 0 || text.length() <= length) {
            return null;
        }
        return minuteOfDayOrNull(text.substring(length).trim());
    }

    /**
     * Rejects an event that begins and ends at the same time of day. A value
     * that is not a clock time cannot be compared with another, so it is
     * accepted here.
     *
     * @param from start value written after the /from marker.
     * @param to end value written after the /to marker.
     * @throws AtlasException if both values name the same time of day.
     */
    private static void requireDistinctTimes(String from, String to) throws AtlasException {
        Integer fromMinutes = minuteOfDayOrNull(from);
        if (fromMinutes != null && fromMinutes.equals(minuteOfDayOrNull(to))) {
            throw new AtlasException("Time flows one way, mortal: an event that "
                    + "begins and ends together is no event. Use: " + EVENT_SYNTAX);
        }
    }

    /**
     * Returns the description that precedes a marker.
     *
     * @param line input line containing a task command.
     * @param prefixLen length of the command word plus its separating space.
     * @param markerPos position of the marker that ends the description.
     * @param syntax syntax text reported when the description is missing.
     * @return the description written before the marker.
     * @throws AtlasException if the description is missing.
     */
    private static String descriptionBefore(String line, int prefixLen, int markerPos, String syntax)
            throws AtlasException {
        String desc = markerPos <= prefixLen ? "" : line.substring(prefixLen, markerPos);
        if (desc.trim().isEmpty()) {
            throw new AtlasException("Name your labour, mortal: " + syntax);
        }
        return desc;
    }
}
