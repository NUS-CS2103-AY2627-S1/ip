package downtowngurl.task;

import downtowngurl.exception.DowntownGurlException;

/**
 * Represents how often a dated task repeats.
 */
public enum RecurrenceFrequency {
    WEEKLY("weekly");

    private static final String UNSUPPORTED_FREQUENCY_MESSAGE = "Recurring tasks can only repeat weekly for now.";

    private final String displayText;

    /**
     * Creates a recurrence frequency with user-facing display text.
     *
     * @param displayText Text shown to users for this frequency.
     */
    RecurrenceFrequency(String displayText) {
        this.displayText = displayText;
    }

    /**
     * Parses a user-entered recurrence frequency.
     *
     * @param input Frequency text entered by the user.
     * @return Matching recurrence frequency.
     * @throws DowntownGurlException If the frequency is unsupported.
     */
    public static RecurrenceFrequency parse(String input) throws DowntownGurlException {
        if (WEEKLY.displayText.equalsIgnoreCase(input.trim())) {
            return WEEKLY;
        }
        throw new DowntownGurlException(UNSUPPORTED_FREQUENCY_MESSAGE);
    }

    /**
     * Parses a recurrence frequency saved in the storage file.
     *
     * @param input Frequency text from storage.
     * @return Matching recurrence frequency.
     * @throws DowntownGurlException If the frequency is unsupported.
     */
    public static RecurrenceFrequency parseFromStorage(String input) throws DowntownGurlException {
        try {
            return RecurrenceFrequency.valueOf(input.trim());
        } catch (IllegalArgumentException e) {
            throw new DowntownGurlException(UNSUPPORTED_FREQUENCY_MESSAGE);
        }
    }

    /**
     * Returns the user-facing text for this frequency.
     *
     * @return Display text for this frequency.
     */
    public String getDisplayText() {
        return this.displayText;
    }
}
