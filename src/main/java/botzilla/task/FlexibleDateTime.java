package botzilla.task;

import java.time.LocalDateTime;
import java.util.Optional;

import botzilla.BotzillaException;

/**
 * Represents a single date/time value entered as free-form text by the
 * user, which may or may not have parsed successfully. A successfully
 * parsed value is usable as a LocalDateTime (e.g. for the "on" command's
 * date lookup) and can be reformatted for display or for saving to disk;
 * an unparseable value keeps the original text and shows it as-is in both
 * cases. Used by DeadlineTask and EventTask, which otherwise each need
 * this same fallback behavior for every date field they hold.
 */
final class FlexibleDateTime {
    private final LocalDateTime dateTime;
    private final boolean hasTime;
    private final String rawText;

    private FlexibleDateTime(LocalDateTime dateTime, boolean hasTime, String rawText) {
        this.dateTime = dateTime;
        this.hasTime = hasTime;
        this.rawText = rawText;
    }

    /**
     * Parses the given text as a date/time, e.g. "2/12/2019 1800". Falls
     * back to keeping the text as-is if it doesn't match a recognized
     * format.
     *
     * @param text the raw user-entered text.
     * @return a FlexibleDateTime wrapping either the parsed value or the raw text.
     * @throws BotzillaException if the text is date-shaped but names an
     *                           invalid calendar date or time (see {@link DateTimeUtil#parse}).
     */
    static FlexibleDateTime parse(String text) throws BotzillaException {
        Optional<LocalDateTime> parsed = DateTimeUtil.parse(text);
        return parsed.isPresent()
                ? new FlexibleDateTime(parsed.get(), DateTimeUtil.hasTimeComponent(text), null)
                : new FlexibleDateTime(null, false, text);
    }

    /**
     * Returns the parsed value, or null if the original text couldn't be
     * parsed as a date.
     *
     * @return the parsed date/time, or null.
     */
    LocalDateTime toDateTimeOrNull() {
        return dateTime;
    }

    /**
     * Returns this value formatted for saving to disk, or the original raw
     * text if it couldn't be parsed.
     *
     * @return the file-format representation.
     */
    String toFileString() {
        return dateTime != null ? DateTimeUtil.formatForFile(dateTime, hasTime) : rawText;
    }

    /**
     * Returns this value formatted for display to the user, or the
     * original raw text if it couldn't be parsed.
     *
     * @return the display representation.
     */
    String toDisplayString() {
        return dateTime != null ? DateTimeUtil.formatForDisplay(dateTime, hasTime) : rawText;
    }
}
