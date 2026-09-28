package nicola;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

/**
 * A task that has a deadline
 */
public class DeadlineTask extends Task {
    private static final DateTimeFormatter DISPLAY_DATE = DateTimeFormatter.ofPattern("MMM dd uuuu");
    private static final DateTimeFormatter DISPLAY_DATE_TIME = DateTimeFormatter.ofPattern("MMM dd uuuu HHmm");

    private final LocalDate byDate;
    private final LocalDateTime byDateTime;
    private final String rawInput;

    DeadlineTask(String description, LocalDate byDate, LocalDateTime byDateTime, String rawInput) {
        super(description);
        this.byDate = byDate;
        this.byDateTime = byDateTime;
        this.rawInput = rawInput;
    }

    @Override
    String getType() {
        return "D";
    }

    @Override
    String getDisplayText() {
        if (byDateTime != null) {
            return getDescription() + " (by: " + byDateTime.format(DISPLAY_DATE_TIME) + ")";
        }
        if (byDate != null) {
            return getDescription() + " (by: " + byDate.format(DISPLAY_DATE) + ")";
        }
        return getDescription() + " (by: " + rawInput + ")";
    }

    @Override
    String serialize() {
        if (byDateTime != null) {
            return "D | " + (isDone() ? "1" : "0") + " | " + getDescription()
                    + " |  | 1 | " + byDateTime;
        }
        return "D | " + (isDone() ? "1" : "0") + " | " + getDescription()
                + " | " + byDate + " | 0 | ";
    }

    /**
     * Returns whether this deadline is due within the given number of days.
     *
     * @param today the current date
     * @param numberOfDays the reminder period
     * @return true if this incomplete deadline is due within the period
     */
    boolean isDueWithin(LocalDate today, int numberOfDays) {
        LocalDate dueDate = byDateTime != null
                ? byDateTime.toLocalDate()
                : byDate;

        if (dueDate == null || isDone()) {
            return false;
        }

        long daysUntilDue = ChronoUnit.DAYS.between(today, dueDate);
        return daysUntilDue >= 0 && daysUntilDue <= numberOfDays;
    }
}
