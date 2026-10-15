package chudgpt.task;

import java.util.Locale;

import chudgpt.exception.ChudException;

/** Defines task priorities in display order, from highest to lowest. */
public enum Priority {
    EXTREME,
    HIGH,
    MEDIUM,
    LOW,
    NONE;

    /**
     * Parses a case-insensitive priority keyword.
     *
     * @param value priority keyword without surrounding whitespace.
     * @return matching priority.
     * @throws ChudException if the keyword is unsupported.
     */
    public static Priority parse(String value) throws ChudException {
        try {
            return valueOf(value.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            throw new ChudException("Priority must be one of: EXTREME, HIGH, MEDIUM, LOW, NONE.");
        }
    }

    /**
     * Returns a bracketed label with a seven-character, right-padded priority name.
     *
     * @return fixed-width priority label.
     */
    public String getDisplayLabel() {
        return String.format("[P:%-7s]", name());
    }
}
