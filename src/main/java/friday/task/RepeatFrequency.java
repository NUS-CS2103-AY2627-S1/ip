package friday.task;

import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.util.Locale;

import friday.FridayException;

/**
 * Represents how often a task should repeat.
 */
public enum RepeatFrequency {
    DAILY("daily"),
    WEEKLY("weekly"),
    BIWEEKLY("biweekly"),
    MONTHLY("monthly"),
    YEARLY("yearly");

    private final String text;

    RepeatFrequency(String text) {
        this.text = text;
    }

    /**
     * Parses a repeat frequency from user input or storage text.
     *
     * @param text text to parse
     * @return matching repeat frequency
     * @throws FridayException if the frequency is not supported
     */
    public static RepeatFrequency parse(String text) throws FridayException {
        String normalizedText = text.trim().toLowerCase(Locale.ROOT);
        for (RepeatFrequency frequency : values()) {
            if (frequency.text.equals(normalizedText)) {
                return frequency;
            }
        }
        throw new FridayException("Apologies, please use daily, weekly, biweekly, monthly, or yearly sir");
    }

    /**
     * Returns the text used to save and display this frequency.
     *
     * @return frequency text
     */
    public String getText() {
        return text;
    }

    /** Advances a date by one recurrence interval, clamping invalid month-end days. */
    public LocalDateTime advance(LocalDateTime date) throws FridayException {
        try {
            return switch (this) {
                case DAILY -> date.plusDays(1);
                case WEEKLY -> date.plusWeeks(1);
                case BIWEEKLY -> date.plusWeeks(2);
                case MONTHLY -> date.plusMonths(1);
                case YEARLY -> date.plusYears(1);
            };
        } catch (DateTimeException e) {
            throw new FridayException("The next recurring date is outside the supported date range.");
        }
    }
}
