package dude.task;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Objects;

import dude.exception.UsageDetails;
import dude.exception.UsageException;

/**
 * A task that must be completed by a specified date or time.
 */
public class Deadline extends Task {
    private static final String USAGE_MESSAGE = "Usage: deadline <description> /by"
            + " <yyyy-MM-dd [HHmm]>";

    /**
     * Date or time by which this task should be completed.
     */
    private final TaskDate by;

    /**
     * Creates an incomplete deadline task with its completion date or time.
     *
     * @param description Text describing the task.
     * @param by          Date or time by which the task should be completed.
     */
    public Deadline(String description, TaskDate by) {
        super(description);
        this.by = Objects.requireNonNull(by, "by");
    }

    /**
     * Creates a deadline task from command input.
     *
     * @param input Raw text following the deadline command.
     * @return Parsed deadline task.
     * @throws UsageException If the input does not contain a valid description and deadline.
     */
    public static Deadline fromInput(String input) throws UsageException {
        if (input == null || input.isBlank() || input.trim().startsWith("/by")) {
            throw usageError(new UsageDetails("deadline", "task details", "<missing>",
                    "non-empty text", USAGE_MESSAGE, "<task details>"));
        }

        String trimmed = input.trim();
        if (countStandaloneTokens(trimmed, "/by") != 1) {
            throw usageError(new UsageDetails("deadline", "by", trimmed,
                    "exactly one /by delimiter", USAGE_MESSAGE, "/by"));
        }
        String[] deadlineParts = splitAt(trimmed, "/by");
        if (deadlineParts == null) {
            String token = containsStandaloneToken(trimmed, "/by") ? "<date or time>" : "/by";
            throw usageError(new UsageDetails("deadline", "by", trimmed,
                    "a date/time after /by", USAGE_MESSAGE, token));
        }

        try {
            return new Deadline(deadlineParts[0], TaskDate.parse(deadlineParts[1]));
        } catch (DateTimeParseException exception) {
            throw usageError(new UsageDetails("deadline", "by", deadlineParts[1],
                    "yyyy-MM-dd or yyyy-MM-dd HHmm", USAGE_MESSAGE,
                    "<yyyy-MM-dd [HHmm]>"), exception);
        }
    }

    /**
     * Creates a usage exception without an underlying parsing cause.
     *
     * @param details Structured usage details.
     * @return Usage exception.
     */
    private static UsageException usageError(UsageDetails details) {
        return new UsageException(details);
    }

    /**
     * Returns a usage exception for an invalid date with its parsing cause.
     *
     * @param details Structured usage details.
     * @param cause   Underlying parsing cause.
     * @return Usage exception.
     */
    private static UsageException usageError(UsageDetails details, Throwable cause) {
        return new UsageException(details, cause);
    }

    /**
     * Returns the date or time by which this task should be completed.
     *
     * @return Completion date or time.
     */
    public TaskDate getBy() {
        return by;
    }

    /**
     * Returns whether this deadline occurs on its deadline date.
     *
     * @param targetDate Date to compare with.
     * @return True when the deadline is on the supplied date.
     */
    @Override
    public boolean occursOn(LocalDate targetDate) {
        return by.occursOn(targetDate);
    }

    /**
     * Returns the formatted deadline representation.
     *
     * @return Deadline type marker followed by the task and deadline.
     */
    @Override
    public String toString() {
        return String.format("[D]%s (by: %s)", super.toString(), by);
    }
}
