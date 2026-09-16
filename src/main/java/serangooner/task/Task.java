package serangooner.task;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Function;

import serangooner.SerangoonerException;

/**
 * Represents a single task item tracked by Serangooner.
 * A task on its own is not a complete thing to store: it is a task of some
 * kind, and its kind decides how it is named, whether it falls on a date and
 * what code identifies it in the save file. Only those kinds can be built.
 */
public abstract class Task {
    /** Separator written between the fields of a saved task. */
    public static final String SAVE_DELIMITER = " | ";
    /** Field value marking a saved task as completed. */
    public static final String SAVE_DONE = "1";
    /** Field value marking a saved task as not yet completed. */
    public static final String SAVE_NOT_DONE = "0";
    /** Position of the type code within the fields of a saved task. */
    public static final int SAVE_INDEX_TYPE = 0;
    /** Position of the completion flag within the fields of a saved task. */
    public static final int SAVE_INDEX_DONE = 1;
    /** Position of the description within the fields of a saved task. */
    public static final int SAVE_INDEX_DESCRIPTION = 2;

    private final String description;
    private boolean isDone;

    /**
     * Constructs an incomplete task with the given description.
     * A description carrying the save delimiter is refused, since it would
     * split the saved line into the wrong fields and lose the task on the
     * next load.
     *
     * @param description Text describing what the task involves.
     * @throws SerangoonerException If the description carries the save delimiter.
     */
    protected Task(String description) {
        if (description.contains(SAVE_DELIMITER)) {
            throw new SerangoonerException("INVALID. pls leave '" + SAVE_DELIMITER
                    + "' out of ur task name, i need it to save ur list");
        }
        this.description = description;
        this.isDone = false;
    }

    /**
     * Marks this task as completed.
     */
    public void markDone() {
        this.isDone = true;
    }

    /**
     * Marks this task as not yet completed.
     */
    public void markNotDone() {
        this.isDone = false;
    }

    public boolean isDone() {
        return isDone;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Returns the name this kind of task goes by in messages to the user.
     */
    public abstract String getTypeName();

    /**
     * Returns whether this task falls on or between the given dates.
     * A task that carries no date of its own never does.
     *
     * @param start First date of the range, inclusive.
     * @param end Last date of the range, inclusive.
     * @return True if this task falls within the range.
     */
    public boolean isWithin(LocalDate start, LocalDate end) {
        return false;
    }

    /**
     * Returns whether the given task is this same task entered again.
     * Two tasks are the same when they are of the same kind and describe the
     * same thing, whatever case or spacing each was typed in. Whether either
     * is done does not matter, since finishing a task does not make it a
     * different one. A kind of task that carries dates compares those too.
     * This is deliberately not {@link #equals(Object)}: the list tells its
     * tasks apart by identity, so that undoing an add removes the very task
     * that was added.
     *
     * @param other Task to compare against.
     * @return True if the other task duplicates this one.
     */
    public boolean isDuplicateOf(Task other) {
        return getClass() == other.getClass()
                && normalizeDescription(description).equals(normalizeDescription(other.description));
    }

    /**
     * Returns the given description reduced to what makes it distinct: its
     * words, in lower case, one space apart.
     *
     * @param description Description as the user typed it.
     * @return Description with case and spacing evened out.
     */
    private static String normalizeDescription(String description) {
        return description.strip().replaceAll("\\s+", " ").toLowerCase(Locale.ROOT);
    }

    /**
     * Returns whether the span a task occupies overlaps the given range of dates.
     * A task that sits at a single point in time passes that point as both
     * ends of its span.
     *
     * @param startDateTime Start of the span the task occupies.
     * @param endDateTime End of the span the task occupies.
     * @param start First date of the range, inclusive.
     * @param end Last date of the range, inclusive.
     * @return True if the span and the range share at least one date.
     */
    protected static boolean isOverlapping(TaskDateTime startDateTime, TaskDateTime endDateTime,
            LocalDate start, LocalDate end) {
        assert !endDateTime.isBefore(startDateTime) : "a task cannot end before it starts";
        assert !end.isBefore(start) : "a DateRange has already put the two dates in order";
        return !startDateTime.toLocalDate().isAfter(end)
                && !endDateTime.toLocalDate().isBefore(start);
    }

    /**
     * Returns the task encoded by the given fields of a saved line.
     * A usable line has exactly the expected number of fields, none blank,
     * a recognized completion flag, and remaining fields that the task type
     * itself accepts.
     *
     * @param fields Fields that one line of the save file was split into.
     * @param fieldCount Number of fields a line of this task type must have.
     * @param factory Builds the task once the line is known to be usable.
     * @return Task the line describes, or nothing if it cannot be read.
     */
    protected static Optional<Task> buildFromSaveFields(String[] fields, int fieldCount,
            Function<String[], Task> factory) {
        assert fieldCount > SAVE_INDEX_DONE : "the completion flag must lie within a saved task's fields";
        if (fields.length != fieldCount) {
            return Optional.empty();
        }
        if (Arrays.stream(fields).anyMatch(String::isBlank)) {
            return Optional.empty();
        }
        boolean isDone = SAVE_DONE.equals(fields[SAVE_INDEX_DONE]);
        if (!isDone && !SAVE_NOT_DONE.equals(fields[SAVE_INDEX_DONE])) {
            return Optional.empty();
        }

        final Task task;
        try {
            task = factory.apply(fields);
        } catch (SerangoonerException exception) {
            return Optional.empty();
        }
        if (isDone) {
            task.markDone();
        }
        return Optional.of(task);
    }

    /**
     * Returns this task encoded as one line of the save file.
     * Subclasses prepend their type code and append their own date fields.
     *
     * @return Completion flag and description, separated by the save delimiter.
     */
    public String toSaveFormat() {
        return (isDone ? SAVE_DONE : SAVE_NOT_DONE) + SAVE_DELIMITER + description;
    }

    /**
     * Returns this task written the way it is shown to the user.
     * Subclasses prepend their type tag and append their own dates, the same
     * way they build on {@link #toSaveFormat()}.
     */
    @Override
    public String toString() {
        return (isDone ? "[✓] " : "[ ] ") + description;
    }
}
