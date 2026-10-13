package baymax.task;

import java.time.LocalDate;

import baymax.exception.BaymaxException;

/** Validates the common-era date range supported by task display and storage. */
public final class TaskDate {
    private TaskDate() {
        // Utility class.
    }

    /** Returns the date if its year is between 0001 and 9999, otherwise rejects it. */
    public static LocalDate validate(LocalDate date) {
        assert date != null : "Task dates should be parsed before validation.";
        if (date.getYear() < 1 || date.getYear() > 9999) {
            throw new BaymaxException(" Sorry, date years must be between 0001 and 9999.");
        }
        return date;
    }
}
