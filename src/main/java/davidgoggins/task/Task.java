package davidgoggins.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Locale;
import java.util.regex.Pattern;

import davidgoggins.DavidGogginsException;

/**
 * Represents a single task in the user's list: a description plus whether it is done.
 *
 * <p>Renders itself two ways: {@link #toString()} for display, e.g.
 * {@code [T][X] read book}, and {@link #toSaveFormat()} for disk, e.g.
 * {@code T | 1 | read book}, whose separate fields make loading a split, not a hunt.
 */
public abstract class Task {
    /**
     * Separates the fields of a saved line.
     *
     * <p>Spaces are included so saved files stay readable. Every field is trimmed on
     * load, so an extra space either side of the bar does no harm.
     */
    public static final String SEPARATOR = " | ";

    /**
     * The character the separator is built from, kept apart from {@link #SEPARATOR} so
     * user input can be checked for it. A description containing this character would
     * produce an extra field on save and be unreadable on load, so it is rejected.
     */
    public static final String SEPARATOR_CHAR = "|";

    /**
     * The one date format accepted for dated tasks, e.g. {@code 2026-10-15}.
     *
     * <p>Strict, so a day that does not exist, such as {@code 2026-02-30}, is refused.
     * The default "smart" resolving would quietly turn it into February 28 instead.
     * Strict resolving needs {@code uuuu} (a plain year) rather than {@code yyyy}.
     */
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("uuuu-MM-dd")
            .withResolverStyle(ResolverStyle.STRICT);

    /** The earliest year accepted in a date; anything older is almost certainly a typo. */
    private static final int MIN_YEAR = 1900;

    /** The latest year accepted in a date; anything later is almost certainly a typo. */
    private static final int MAX_YEAR = 2100;

    /** Matches text shaped like a date, so a well-formed but impossible date gets its own advice. */
    private static final Pattern DATE_SHAPE = Pattern.compile("\\d{4}-\\d{2}-\\d{2}");

    /** The task's text, kept exactly as the user typed it. */
    protected String description;

    /** Whether the user has ticked this task off. */
    protected boolean isDone;

    /**
     * Creates a task that is not done yet.
     *
     * @param description the task's text, which must not contain {@link #SEPARATOR_CHAR}
     */
    public Task(String description) {
        // Both the parser and fromSaveFormat() reject such descriptions before a task is
        // built, so either one arriving here means a check was skipped somewhere.
        assert description != null && !description.isBlank() : "description should not be blank";
        assert !description.contains(SEPARATOR_CHAR) : "description would corrupt the save file";
        this.description = description;
        this.isDone = false;
    }

    /**
     * Returns this task as one line for display, e.g. {@code [T][X] read book}.
     *
     * <p>Abstract because only the subclass knows its type letter and its extra fields.
     *
     * @return the line to show the user, without a line separator
     */
    @Override
    public abstract String toString();

    /**
     * Returns this task as one line of the save file.
     *
     * <p>Each subclass writes its own type marker and its own extra fields, which is why
     * this is abstract rather than implemented once here: only the subclass knows how
     * many fields it has.
     *
     * @return the line to write, without a line separator
     */
    public abstract String toSaveFormat();

    /**
     * Returns a save line: the type marker, the done flag, the description, and then
     * whatever extra fields the subclass has.
     *
     * <p>Takes the extra fields as varargs because their number is exactly what differs
     * between the task types: a todo has none, a deadline has one, an event has two.
     * Each subclass passes only the fields it has, and the part of the line they all
     * share is written once here instead of three times.
     *
     * @param type the one-letter marker for the task type, e.g. {@code "T"}
     * @param extraFields the subclass's own fields, in the order they are saved
     * @return the line to write, without a line separator
     */
    protected String toSaveFormat(String type, String... extraFields) {
        StringBuilder line = new StringBuilder(type)
                .append(SEPARATOR).append(doneFlag())
                .append(SEPARATOR).append(description);
        for (String field : extraFields) {
            line.append(SEPARATOR).append(field);
        }
        return line.toString();
    }

    /**
     * Rebuilds a task from one line of the save file.
     *
     * <p>The reverse of {@link #toSaveFormat()}, but {@code static}: when a line is read
     * there is no object yet to call a method on, so this factory inspects the type
     * marker and decides which constructor to call.
     *
     * @param line one line from the save file
     * @return the task the line describes
     * @throws DavidGogginsException if the line is not in the expected format
     */
    public static Task fromSaveFormat(String line) throws DavidGogginsException {
        // -1 keeps trailing empty fields, so "D | 0 | return book | " is spotted as a
        // missing due date instead of silently losing the field.
        String[] fields = line.split("\\" + SEPARATOR_CHAR, -1);
        for (int i = 0; i < fields.length; i++) {
            fields[i] = fields[i].trim();
        }

        if (fields.length < 3) {
            throw new DavidGogginsException("expected at least 3 fields, found " + fields.length);
        }

        String type = fields[0];
        boolean isDone = parseDoneFlag(fields[1]);
        String description = requireNonEmpty(fields[2], "description");

        Task task = switch (type) {
            case "T" -> {
                requireFieldCount(fields, 3, "todo");
                yield new Todo(description);
            }
            case "D" -> {
                requireFieldCount(fields, 4, "deadline");
                yield new Deadline(description, requireNonEmpty(fields[3], "due date"));
            }
            case "E" -> {
                requireFieldCount(fields, 5, "event");
                yield new Event(description,
                        requireNonEmpty(fields[3], "start time"),
                        requireNonEmpty(fields[4], "end time"));
            }
            default -> throw new DavidGogginsException("unknown task type \"" + type + "\"");
        };

        // The constructors always start a task as not done, so the saved flag is
        // applied afterwards through the same methods the mark/unmark commands use.
        if (isDone) {
            task.markAsDone();
        }
        return task;
    }

    /** Turns the saved {@code 1}/{@code 0} flag back into a boolean. */
    private static boolean parseDoneFlag(String field) throws DavidGogginsException {
        return switch (field) {
            case "1" -> true;
            case "0" -> false;
            default -> throw new DavidGogginsException(
                    "done flag should be 0 or 1, found \"" + field + "\"");
        };
    }

    /** Rejects a field that is present but blank, which would give a task with no text. */
    private static String requireNonEmpty(String field, String name) throws DavidGogginsException {
        if (field.isEmpty()) {
            throw new DavidGogginsException("the " + name + " is empty");
        }
        return field;
    }

    /** Rejects a line with the wrong number of fields for its type. */
    private static void requireFieldCount(String[] fields, int expected, String type)
            throws DavidGogginsException {
        if (fields.length != expected) {
            throw new DavidGogginsException("a " + type + " needs " + expected
                    + " fields, found " + fields.length);
        }
    }

    /**
     * Turns a date the user typed into a {@link LocalDate}.
     *
     * <p>Static so a subclass constructor can call it while its {@code final} date
     * fields are still being assigned.
     *
     * @param date    the text the user typed, expected as {@code yyyy-mm-dd}
     * @param example a correct command to suggest if the date is malformed
     * @return the date the text describes
     * @throws DavidGogginsException if the text is not a date in that format, or its year
     *         is outside {@link #MIN_YEAR} to {@link #MAX_YEAR}
     */
    protected static LocalDate parseDate(String date, String example) throws DavidGogginsException {
        LocalDate parsedDate;
        try {
            parsedDate = LocalDate.parse(date, DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            if (DATE_SHAPE.matcher(date).matches()) {
                throw new DavidGogginsException("\"" + date + "\" is not a real date. "
                        + "Check the month and the day: that day does not exist.");
            }
            throw new DavidGogginsException("I need the date as yyyy-mm-dd, not \"" + date
                    + "\". Try: " + example);
        }

        if (parsedDate.getYear() < MIN_YEAR || parsedDate.getYear() > MAX_YEAR) {
            throw new DavidGogginsException("\"" + date + "\" is not a realistic date. "
                    + "Pick a year from " + MIN_YEAR + " to " + MAX_YEAR + ".");
        }
        return parsedDate;
    }

    /**
     * Returns the done marker shown in the task list.
     *
     * @return {@code "[X] "} when the task is done, {@code "[ ] "} when it is not
     */
    protected String getStatusIcon() {
        return isDone ? "[X] " : "[ ] ";
    }

    /**
     * Returns the saved done flag.
     *
     * @return {@code "1"} when the task is done, {@code "0"} when it is not
     */
    protected String doneFlag() {
        return isDone ? "1" : "0";
    }

    /**
     * Returns true if this task records the same work as {@code other}.
     *
     * <p>Two tasks are duplicates when they are the same type with the same description,
     * ignoring case and runs of spaces; subclasses add their dates to the comparison.
     * Whether either is done does not matter: logging a finished task again is still a repeat.
     *
     * @param other the task to compare with
     * @return true if the two tasks are duplicates
     */
    public boolean isDuplicateOf(Task other) {
        return other != null
                && getClass() == other.getClass()
                && normalize(description).equals(normalize(other.description));
    }

    /** Returns the description lower-cased with runs of spaces squeezed to one. */
    private static String normalize(String text) {
        return text.trim().replaceAll("\\s+", " ").toLowerCase(Locale.ROOT);
    }

    /**
     * Returns true if this task's description contains the given keyword.
     *
     * <p>Matching ignores case, so {@code find book} also finds a task the user
     * wrote as {@code Read Book}. Asking the task itself, rather than handing out
     * the description through a getter, keeps the field private to this class.
     *
     * @param keyword the text to look for
     * @return true if the description contains the keyword
     */
    public boolean matches(String keyword) {
        // Locale.ROOT, so matching does not depend on the machine's language setting.
        return description.toLowerCase(Locale.ROOT).contains(keyword.toLowerCase(Locale.ROOT));
    }

    /**
     * Returns true if the user has ticked this task off.
     *
     * @return true when the task is done
     */
    public boolean isDone() {
        return isDone;
    }

    /** Marks this task as done, used by the {@code mark} command. */
    public void markAsDone() {
        isDone = true;
    }

    /** Marks this task as not done yet, used by the {@code unmark} command. */
    public void markAsNotDone() {
        isDone = false;
    }
}
