package yapatron.task;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;

import yapatron.YapException;

public class Deadline extends Task {
    protected LocalDateTime deadline;
    private DeadlineType deadlineType;

    private enum DeadlineType {
        DATE,
        TIME,
        DATE_TIME
    }

    private static final DateTimeFormatter INPUT_DATE_TIME_FORMAT =
        DateTimeFormatter.ofPattern("d/M/uuuu HHmm")
        .withResolverStyle(ResolverStyle.STRICT);
    private static final DateTimeFormatter INPUT_DATE_FORMAT =
        DateTimeFormatter.ofPattern("d/M/uuuu")
        .withResolverStyle(ResolverStyle.STRICT);
    private static final DateTimeFormatter INPUT_TIME_FORMAT =
        DateTimeFormatter.ofPattern("HHmm")
        .withResolverStyle(ResolverStyle.STRICT);
    private static final DateTimeFormatter STORED_DATE_TIME_FORMAT =
        DateTimeFormatter.ofPattern("uuuu-MM-dd HHmm")
        .withResolverStyle(ResolverStyle.STRICT);
    private static final DateTimeFormatter DATE_OUTPUT_FORMAT =
        DateTimeFormatter.ofPattern("MMM dd uuuu");
    private static final DateTimeFormatter DATE_TIME_OUTPUT_FORMAT =
        DateTimeFormatter.ofPattern("MMM dd uuuu, h:mma");
    private static final DateTimeFormatter TIME_OUTPUT_FORMAT =
        DateTimeFormatter.ofPattern("h:mma");

    public Deadline(String desc, String deadline) throws YapException {
        super(desc);
        parseDeadline(deadline.trim());

    }
    /**
     * Parses and checks if the supplied deadline values are valid
     *
     * @param value deadline value to parse
     * @throws YapException if the value is not a valid date, time, or date-time
     */
    private void parseDeadline(String value) throws YapException {
        try {
            deadline = LocalDateTime.parse(value, INPUT_DATE_TIME_FORMAT);
            deadlineType = DeadlineType.DATE_TIME;
            return;
        } catch (DateTimeParseException ignored) {
            // Try the remaining supported formats.
        }

        try {
            deadline = LocalDateTime.parse(value, STORED_DATE_TIME_FORMAT);
            deadlineType = DeadlineType.DATE_TIME;
            return;
        } catch (DateTimeParseException ignored) {
            // Try the remaining supported formats.
        }

        try {
            deadline = LocalDate.parse(value, INPUT_DATE_FORMAT).atStartOfDay();
            deadlineType = DeadlineType.DATE;
            return;
        } catch (DateTimeParseException ignored) {
            // Try the time-only format.
        }

        try {
            deadline = LocalDate.of(1970, 1, 1)
                .atTime(LocalTime.parse(value, INPUT_TIME_FORMAT));
            deadlineType = DeadlineType.TIME;
        } catch (DateTimeParseException e) {
            throw new YapException(
                    "Is that date real?? I can't read that deadline :("
                    + "Use d/M/yyyy, HHmm, "
                    + "or d/M/yyyy HHmm.");
        }
    }

    /**
     * Returns the deadline as a formatted String.
     *
     * @return formatted deadline description
     */
    @Override
    public String toString() {
        String formattedDeadline;
        switch (deadlineType) {
            case DATE:
                formattedDeadline = deadline.format(DATE_OUTPUT_FORMAT);
                break;
            case TIME:
                formattedDeadline = deadline.format(TIME_OUTPUT_FORMAT);
                break;
            default:
                formattedDeadline = deadline.format(DATE_TIME_OUTPUT_FORMAT);
                break;
        }
        return "[D]" + super.toString() + " (by: " + formattedDeadline + ")";
    }

    /**
     * Formats the deadline for file storage.
     *
     * @return pipe-delimited deadline data
     */
    @Override
    public String toFileFormat() {
        String storedDeadline;
        switch (deadlineType) {
            case DATE:
                storedDeadline = deadline.format(INPUT_DATE_FORMAT);
                break;
            case TIME:
                storedDeadline = deadline.format(INPUT_TIME_FORMAT);
                break;
            default:
                storedDeadline = deadline.format(STORED_DATE_TIME_FORMAT);
                break;
        }
        return "[DEADLINE] | " + super.toFileFormat()
            + " | " + storedDeadline;
    }
}

