package tally.storage;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Optional;
import java.util.regex.Pattern;

import tally.task.Deadline;
import tally.task.Event;
import tally.task.Moment;
import tally.task.Task;
import tally.task.Todo;
import tally.task.Window;

/**
 * Reads one line of the data file back into the task it stands for.
 *
 * <p>Where the fields sit on a line, and how many a line of each kind has, is known
 * here and nowhere else. Writing a line is the task's own job, since only the task
 * knows what it carries; reading one cannot be, because which kind of task to build
 * is only known once the type letter has been read.
 *
 * <p>A line that does not describe a task is answered with an empty Optional rather
 * than by throwing, since the reader skips it and carries on with the rest of the
 * file. The helpers below still answer with null among themselves, which read keeps
 * to itself, so that no caller has to remember to ask.
 */
final class TaskLine {
    /** Where each part of a task sits on its line in the data file. */
    private static final int INDEX_TYPE = 0;
    private static final int INDEX_DONE = 1;
    private static final int INDEX_DESCRIPTION = 2;

    /**
     * Where a task's own two extra parts sit. They are dates for a deadline and a
     * window, and whatever the user typed for an event.
     */
    private static final int INDEX_FIRST_DETAIL = 3;
    private static final int INDEX_SECOND_DETAIL = 4;

    /** How many parts a line of each kind of task has. */
    private static final int FIELD_COUNT_TODO = 3;
    private static final int FIELD_COUNT_DEADLINE = 4;
    private static final int FIELD_COUNT_EVENT = 5;
    private static final int FIELD_COUNT_WINDOW = 5;

    /** Prevents anyone making one: every method here is static. */
    private TaskLine() {
    }

    /**
     * Returns the task a line of the data file stands for.
     *
     * <p>Reading is a factory rather than a method on Task, because which subclass
     * to build is only known once the type letter has been read.
     *
     * @param line one line of the data file, with surrounding spaces removed.
     * @return the task described, or empty if the line is not in the expected format.
     */
    static Optional<Task> read(String line) {
        String[] fields = line.split(Pattern.quote(Task.FIELD_SEPARATOR));
        boolean hasValidCommonFields = fields.length > INDEX_DESCRIPTION
                && (fields[INDEX_DONE].equals(Task.FLAG_NOT_DONE) || fields[INDEX_DONE].equals(Task.FLAG_DONE))
                && Arrays.stream(fields).noneMatch(String::isBlank);
        if (!hasValidCommonFields) {
            return Optional.empty();
        }

        String description = fields[INDEX_DESCRIPTION];
        Task task = switch (fields[INDEX_TYPE]) {
            case Todo.TYPE -> fields.length == FIELD_COUNT_TODO ? new Todo(description) : null;
            case Deadline.TYPE -> fields.length == FIELD_COUNT_DEADLINE
                    ? readDeadline(description, fields[INDEX_FIRST_DETAIL]) : null;
            case Event.TYPE -> fields.length == FIELD_COUNT_EVENT
                    ? readEvent(description, fields[INDEX_FIRST_DETAIL],
                            fields[INDEX_SECOND_DETAIL]) : null;
            case Window.TYPE -> fields.length == FIELD_COUNT_WINDOW
                    ? readWindow(description, fields[INDEX_FIRST_DETAIL], fields[INDEX_SECOND_DETAIL]) : null;
            default -> null;
        };

        if (task != null && fields[INDEX_DONE].equals(Task.FLAG_DONE)) {
            task.markAsDone();
        }
        return Optional.ofNullable(task);
    }

    /**
     * Returns the deadline a data-file line describes.
     *
     * <p>A date the file cannot offer as yyyy-mm-dd is damage rather than something
     * to ask the user about, so this reports it the same way as any other malformed
     * line: by returning null.
     *
     * @param description what has to be done.
     * @param dueDateText the date field as it appears in the file.
     * @return the deadline, or null if the date cannot be read.
     */
    private static Task readDeadline(String description, String dueDateText) {
        return Task.readDate(dueDateText)
                .<Task>map(dueDate -> new Deadline(description, dueDate))
                .orElse(null);
    }

    /**
     * Returns the event a data-file line describes, or null if it cannot be read.
     *
     * @param description what is happening.
     * @param startText the first date field as it appears in the file.
     * @param endText the second date field as it appears in the file.
     * @return the event, or null if the line cannot be read.
     */
    private static Task readEvent(String description, String startText, String endText) {
        Optional<Moment> start = Moment.read(startText);
        Optional<Moment> end = Moment.read(endText);
        if (start.isEmpty() || end.isEmpty()
                || Event.hasBackwardsEnds(start.get(), end.get())) {
            return null;
        }
        return new Event(description, start.get(), end.get());
    }

    /**
     * Returns the window task a data-file line describes, or null if it cannot be read.
     *
     * @param description what has to be done.
     * @param startDateText the first date field as it appears in the file.
     * @param endDateText the second date field as it appears in the file.
     * @return the window task, or null if the line cannot be read.
     */
    private static Task readWindow(String description, String startDateText, String endDateText) {
        Optional<LocalDate> start = Task.readDate(startDateText);
        Optional<LocalDate> end = Task.readDate(endDateText);
        if (start.isEmpty() || end.isEmpty() || end.get().isBefore(start.get())) {
            return null;
        }
        return new Window(description, start.get(), end.get());
    }
}
