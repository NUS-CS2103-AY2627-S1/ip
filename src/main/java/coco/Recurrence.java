package coco;

import java.time.LocalDate;

/**
 * How often a recurring Deadline repeats.
 */
public enum Recurrence {
    DAILY,
    WEEKLY,
    MONTHLY;

    /**
     * Parses user-typed recurrence text (e.g. "weekly") into a Recurrence.
     *
     * @param text Raw text, case-insensitive.
     * @return The matching Recurrence.
     * @throws CocoException If the text doesn't match daily/weekly/monthly.
     */
    public static Recurrence fromText(String text) throws CocoException {
        try {
            return valueOf(text.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new CocoException("'" + text
                    + "' isn't a recurrence I know. Try daily, weekly, or monthly.");
        }
    }

    /**
     * Returns the next occurrence of a date recurring at this frequency.
     *
     * @param from The date this recurrence is measured from.
     * @return The next date it falls on.
     */
    public LocalDate nextOccurrence(LocalDate from) {
        switch (this) {
        case DAILY -> {
            return from.plusDays(1);
            }
        case WEEKLY -> {
            return from.plusWeeks(1);
            }
        case MONTHLY -> {
            return from.plusMonths(1);
            }
        default -> throw new AssertionError("Unhandled Recurrence: " + this);
        }
    }
}
