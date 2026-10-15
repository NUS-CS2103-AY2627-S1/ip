package jaylen;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

/** A task that must be completed by a stated date or time. */
public class Deadline extends Task {
    private final LocalDateTime by;
    private final boolean includesTime;

    /**
     * Creates a deadline.
     *
     * @param description what must be done
     * @param by the deadline date
     */
    public Deadline(String description, LocalDate by) {
        super(description, TaskType.DEADLINE);
        this.by = by.atStartOfDay();
        this.includesTime = false;
    }

    /**
     * Creates a deadline with a date and time.
     *
     * @param description what must be done
     * @param by the deadline date and time
     */
    public Deadline(String description, LocalDateTime by) {
        super(description, TaskType.DEADLINE);
        this.by = by;
        this.includesTime = true;
    }

    /**
     * Creates a deadline from user input, accepting either a date or date and time.
     *
     * @param description what must be done
     * @param by the user-provided date or date and time
     */
    public Deadline(String description, String by) {
        super(description, TaskType.DEADLINE);
        LocalDateTime parsedBy;
        boolean parsedIncludesTime;
        try {
            parsedBy = DateTimeParser.parseDateTime(by);
            parsedIncludesTime = true;
        } catch (DateTimeParseException dateTimeException) {
            try {
                parsedBy = DateTimeParser.parseDate(by).atStartOfDay();
                parsedIncludesTime = false;
            } catch (DateTimeParseException dateException) {
                throw new JaylenException("Use a date such as 2019-10-15 or "
                        + "a date and time such as 2/12/2019 1800.");
            }
        }
        this.by = parsedBy;
        this.includesTime = parsedIncludesTime;
    }

    /**
     * Returns the deadline as a typed date and time.
     *
     * @return the deadline date and time
     */
    public LocalDateTime getBy() {
        return by;
    }

    /**
     * Returns whether the user supplied a time as well as a date.
     *
     * @return {@code true} when the deadline includes a time
     */
    public boolean includesTime() {
        return includesTime;
    }

    @Override
    public String toString() {
        String formattedBy = includesTime
                ? DateTimeParser.formatDateTime(by)
                : DateTimeParser.formatDate(by.toLocalDate());
        return super.toString() + " (by: " + formattedBy + ")";
    }
}
