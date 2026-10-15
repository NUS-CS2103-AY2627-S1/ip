package waddles;

import java.time.LocalDate;

/**
 * Represents how often a {@link Deadline} or {@link Event} repeats, e.g. a
 * weekly project meeting. {@link #NONE} means the task does not repeat.
 */
public enum Recurrence {
    NONE, DAILY, WEEKLY, MONTHLY;

    /**
     * Matches the word a user types after "/every" (e.g. "week") to a
     * Recurrence value.
     *
     * @param keyword The word the user typed, e.g. "daily" or "week".
     * @return The matching Recurrence, or null if the keyword isn't recognised.
     */
    public static Recurrence fromKeyword(String keyword) {
        switch (keyword.toLowerCase()) {
        case "day":
        case "daily":
            return DAILY;
        case "week":
        case "weekly":
            return WEEKLY;
        case "month":
        case "monthly":
            return MONTHLY;
        default:
            return null;
        }
    }

    /**
     * Recovers a Recurrence from the code previously written to the data
     * file by {@link #name()}. Falls back to NONE for a null/unrecognised
     * code so that data files saved before this feature existed still load.
     *
     * @param code The stored code, e.g. "WEEKLY".
     * @return The matching Recurrence, or NONE if not recognised.
     */
    public static Recurrence fromStorageCode(String code) {
        try {
            return Recurrence.valueOf(code);
        } catch (IllegalArgumentException | NullPointerException e) {
            return NONE;
        }
    }

    /**
     * Computes the next occurrence's date after the given one.
     *
     * @param date The date of the occurrence that was just completed.
     * @return The date of the next occurrence. Returns {@code date} unchanged if this is NONE.
     */
    public LocalDate advance(LocalDate date) {
        switch (this) {
        case DAILY:
            return date.plusDays(1);
        case WEEKLY:
            return date.plusWeeks(1);
        case MONTHLY:
            return date.plusMonths(1);
        default:
            return date;
        }
    }

    /**
     * Returns a short noun describing the recurrence period, for use in
     * messages like "(every week)".
     *
     * @return "day", "week", "month", or "" for NONE.
     */
    public String displayLabel() {
        switch (this) {
        case DAILY:
            return "day";
        case WEEKLY:
            return "week";
        case MONTHLY:
            return "month";
        default:
            return "";
        }
    }
}
