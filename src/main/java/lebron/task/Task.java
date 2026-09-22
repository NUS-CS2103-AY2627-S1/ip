package lebron.task;

/**
 * A task the user is tracking: a description plus a done/not-done status.
 *
 * <p>Concrete kinds of task ({@link Todo}, {@link Deadline}, {@link Event})
 * extend this class and decide how they are displayed and how they are
 * encoded for the data file.
 */
public abstract class Task {
    /** What the task is about. */
    protected String description;
    /** Whether the task has been completed. */
    protected boolean isDone;

    /**
     * Creates a task with the given description, initially not done.
     *
     * @param description what the task is about
     */
    protected Task(String description) {
        // Every caller (Parser, fromFileFormat) already rejects a blank
        // description before reaching here; this documents that contract
        // so a future caller that skips validation fails loudly under -ea
        // instead of silently creating a blank-looking task.
        assert description != null && !description.isBlank() : "description must not be blank";
        this.description = description;
        this.isDone = false;
    }

    /** Returns the task's description text. */
    public String getDescription() {
        return description;
    }

    /** Returns {@code "X"} if the task is done, or a single space otherwise. */
    public String getStatusIcon() {
        return isDone ? "X" : " ";
    }

    /** Marks this task as done. */
    public void markAsDone() {
        isDone = true;
    }

    /** Marks this task as not done. */
    public void markAsNotDone() {
        isDone = false;
    }

    /**
     * Returns whether this task recurs. Overridden by {@link Deadline}/
     * {@link Event} when they carry a {@link RecurrencePeriod}; a recurring
     * task's {@link #markAsDone()} advances its date instead of staying
     * marked done.
     *
     * @return true if this task recurs
     */
    public boolean isRecurring() {
        return false;
    }

    /** Shared helper for subclasses: returns the {@code 1}/{@code 0} done flag. */
    protected String getDoneBit() {
        return isDone ? "1" : "0";
    }

    /**
     * Returns this task encoded as one line for the data file. The first
     * field is a single-letter type tag ({@code T}, {@code D}, {@code E})
     * that {@link #fromFileFormat(String)} dispatches on.
     *
     * @return the data-file line for this task
     */
    public abstract String toFileFormat();

    /**
     * Reconstructs a task from one line of the data file produced by
     * {@link #toFileFormat()}, choosing the subclass from the leading type
     * field.
     *
     * @param line one line of the data file
     * @return the task the line describes
     * @throws IllegalArgumentException if the line is not in the expected
     *     format (wrong field count, unknown type, bad done flag, empty
     *     description, or an unparseable date). Callers use this to detect a
     *     corrupted data file.
     */
    public static Task fromFileFormat(String line) {
        String[] parts = line.split("\\|", -1);
        if (parts.length < 3) {
            throw new IllegalArgumentException("expected at least 3 fields but found " + parts.length);
        }
        String type = parts[0].trim();
        boolean done = parseDoneFlag(parts[1].trim());
        String description = parts[2].trim();
        if (description.isEmpty()) {
            throw new IllegalArgumentException("description is empty");
        }

        Task task;
        switch (type) {
            case "T":
                requireFieldCount(parts, 3, 3);
                task = new Todo(description);
                break;
            case "D": {
                // A 5th field is the optional recurrence period; older data
                // files without one (4 fields) are still valid -- non-recurring.
                requireFieldCount(parts, 4, 5);
                DateTime by = DateTime.parse(parts[3].trim());
                RecurrencePeriod recurrence = parts.length == 5 ? RecurrencePeriod.parse(parts[4].trim()) : null;
                task = new Deadline(description, by, recurrence);
                break;
            }
            case "E": {
                // Same optional trailing field, one position later.
                requireFieldCount(parts, 5, 6);
                DateTime from = DateTime.parse(parts[3].trim());
                DateTime to = DateTime.parse(parts[4].trim());
                RecurrencePeriod recurrence = parts.length == 6 ? RecurrencePeriod.parse(parts[5].trim()) : null;
                task = new Event(description, from, to, recurrence);
                break;
            }
            default:
                throw new IllegalArgumentException("unknown task type '" + type + "'");
        }
        // Every branch above either assigns task or throws -- the compiler
        // can't verify that for a String switch, so this documents it: if a
        // future case is added without doing one or the other, this fires
        // instead of task silently staying null.
        assert task != null : "every switch branch above must assign task or throw";
        if (done) {
            task.markAsDone();
        }
        return task;
    }

    private static boolean parseDoneFlag(String flag) {
        if (flag.equals("1")) {
            return true;
        }
        if (flag.equals("0")) {
            return false;
        }
        throw new IllegalArgumentException("done flag must be 0 or 1 but was '" + flag + "'");
    }

    /**
     * Requires the line to have between {@code min} and {@code max} fields
     * (inclusive) -- e.g. a {@code D} line has 4 fields, or 5 with an
     * optional trailing recurrence period.
     */
    private static void requireFieldCount(String[] parts, int min, int max) {
        if (parts.length < min || parts.length > max) {
            String expected = min == max ? String.valueOf(min) : min + "-" + max;
            throw new IllegalArgumentException(
                    "expected " + expected + " fields but found " + parts.length);
        }
    }

    /** Returns the display form, e.g. {@code [X] read book}. */
    @Override
    public String toString() {
        return "[" + getStatusIcon() + "] " + description;
    }
}
