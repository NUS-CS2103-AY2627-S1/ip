package milo.task;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Locale;
import java.util.Optional;

/**
 * Reads the date formats Milo accepts from the user.
 * <p>
 * This lives apart from {@link Deadline} because {@link Event} needs to read
 * the same formats in order to tell whether an event ends before it starts,
 * and neither task type should have to reach into the other to find out how
 * a date is spelled.
 */
public final class TaskDate {
    // Input formats resolve STRICTly, so an impossible date such as
    // 31/2/2026 is rejected instead of being quietly moved to Feb 28, which
    // is what the default (lenient) resolver would do. Strict resolution
    // needs "uuuu" rather than "yyyy", because "yyyy" is year-of-era and
    // would additionally demand an era field.
    /** Accepted input formats that include a time of day. */
    private static final DateTimeFormatter[] DATE_TIME_FORMATS = {
        DateTimeFormatter.ofPattern("uuuu-MM-dd HHmm", Locale.ENGLISH)
                .withResolverStyle(ResolverStyle.STRICT),
        DateTimeFormatter.ofPattern("d/M/uuuu HHmm", Locale.ENGLISH)
                .withResolverStyle(ResolverStyle.STRICT),
    };

    /** Accepted input formats that give only a date. */
    private static final DateTimeFormatter[] DATE_FORMATS = {
        DateTimeFormatter.ofPattern("uuuu-MM-dd", Locale.ENGLISH)
                .withResolverStyle(ResolverStyle.STRICT),
        DateTimeFormatter.ofPattern("d/M/uuuu", Locale.ENGLISH)
                .withResolverStyle(ResolverStyle.STRICT),
    };

    /** Not instantiable: this class only holds the shared date formats. */
    private TaskDate() {
    }

    /**
     * A date read from text, together with whether that text carried a time
     * of day. A date given without a time is stored at midnight, so this flag
     * is what separates "no time was given" from "due at 00:00".
     *
     * @param value   the point in time the text described
     * @param hasTime whether the text included a time of day
     */
    public record Parsed(LocalDateTime value, boolean hasTime) {
    }

    /**
     * Reads a date, and possibly a time, from text.
     *
     * @param text the text to read, already trimmed
     * @return the date it describes, or empty if it is not in a format Milo
     *         accepts
     */
    public static Optional<Parsed> parse(String text) {
        // Formats carrying a time are tried first: "2026-10-15" would also
        // match the front of "2026-10-15 1800" and silently lose the time.
        for (DateTimeFormatter format : DATE_TIME_FORMATS) {
            try {
                return Optional.of(new Parsed(LocalDateTime.parse(text, format), true));
            } catch (DateTimeParseException e) {
                continue; // not this format; try the next
            }
        }

        for (DateTimeFormatter format : DATE_FORMATS) {
            try {
                return Optional.of(new Parsed(LocalDate.parse(text, format).atStartOfDay(), false));
            } catch (DateTimeParseException e) {
                continue;
            }
        }

        return Optional.empty();
    }
}
