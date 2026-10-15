package nob.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.util.Locale;
import java.util.Optional;

/**
 * Utilities for parsing and formatting date and time strings used by tasks.
 */
public final class DateTimeUtil {
    private static final DateTimeFormatter DATE_ONLY_FORMATTER = DateTimeFormatter.ofPattern("MMM dd yyyy",
            Locale.ENGLISH);
    private static final DateTimeFormatter DISPLAY_FORMATTER = new DateTimeFormatterBuilder()
            .appendPattern("MMM dd yyyy")
            .appendLiteral(", ")
            .append(DateTimeFormatter.ofPattern("h:mma", Locale.ENGLISH))
            .toFormatter(Locale.ENGLISH);

    private static final DateTimeFormatter[] INPUT_FORMATTERS = new DateTimeFormatter[] {
            DateTimeFormatter.ofPattern("d/M/uuuu HHmm", Locale.ENGLISH),
            DateTimeFormatter.ofPattern("d/M/uuuu HH:mm", Locale.ENGLISH),
            DateTimeFormatter.ofPattern("d/M/uuuu h:mma", Locale.ENGLISH),
            DateTimeFormatter.ofPattern("d/M/uuuu hha", Locale.ENGLISH),
            DateTimeFormatter.ofPattern("d/M/uuuu", Locale.ENGLISH),
            DateTimeFormatter.ofPattern("M/d/uuuu HHmm", Locale.ENGLISH),
            DateTimeFormatter.ofPattern("M/d/uuuu", Locale.ENGLISH),
            DateTimeFormatter.ofPattern("MM/dd/uuuu", Locale.ENGLISH),
            DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ENGLISH),
            DateTimeFormatter.ofPattern("yyyy/MM/dd", Locale.ENGLISH),
            DateTimeFormatter.ofPattern("yyyy-MM-dd HHmm", Locale.ENGLISH),
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm", Locale.ENGLISH),
            DateTimeFormatter.ofPattern("yyyy-MM-dd h:mma", Locale.ENGLISH),
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm", Locale.ENGLISH),
            DateTimeFormatter.ofPattern("MMM d yyyy, h:mma", Locale.ENGLISH),
            DateTimeFormatter.ofPattern("MMM dd yyyy, h:mma", Locale.ENGLISH),
            DateTimeFormatter.ofPattern("MMM d yyyy", Locale.ENGLISH),
            DateTimeFormatter.ofPattern("MMM dd yyyy", Locale.ENGLISH),
            DateTimeFormatter.ofPattern("d MMM yyyy, h:mma", Locale.ENGLISH),
            DateTimeFormatter.ofPattern("dd MMM yyyy, h:mma", Locale.ENGLISH),
            DateTimeFormatter.ofPattern("d MMM yyyy", Locale.ENGLISH),
            DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.ENGLISH),
            DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.ENGLISH),
            DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.ENGLISH),
            DateTimeFormatter.ofPattern("MMM d yyyy h:mma", Locale.ENGLISH)
    };

    private DateTimeUtil() {
        // utility class
    }

    /**
     * Parses a supported date or date-time string.
     *
     * @param value Raw user input or stored value.
     * @return Parsed date-time if the input format is supported.
     */
    public static Optional<LocalDateTime> parseDateTime(String value) {
        if (value == null) {
            return Optional.empty();
        }

        String trimmed = value.trim();
        if (trimmed.isEmpty()) {
            return Optional.empty();
        }

        for (DateTimeFormatter formatter : INPUT_FORMATTERS) {
            try {
                return Optional.of(LocalDateTime.parse(trimmed, formatter));
            } catch (DateTimeParseException exception) {
                // Try the next supported format.
            }
        }

        String[] pieces = trimmed.split(" ");
        if (pieces.length == 2 && pieces[1].matches("\\d{3,4}")) {
            try {
                LocalDate parsedDate = LocalDate.parse(pieces[0], DateTimeFormatter.ISO_LOCAL_DATE);
                return Optional.of(parsedDate.atTime(Integer.parseInt(pieces[1]) / 100,
                        Integer.parseInt(pieces[1]) % 100));
            } catch (DateTimeParseException | NumberFormatException exception) {
                // Not a ISO date + time combination.
            }
        }

        try {
            LocalDate parsedDate = LocalDate.parse(trimmed, DateTimeFormatter.ISO_LOCAL_DATE);
            return Optional.of(parsedDate.atStartOfDay());
        } catch (DateTimeParseException exception) {
            return Optional.empty();
        }
    }

    /**
     * Formats a value for display in the task list.
     *
     * @param value Value to format.
     * @return A readable string for display.
     */
    public static String formatDisplay(LocalDateTime value) {
        if (value == null) {
            return "";
        }
        if (value.toLocalTime().equals(LocalTime.MIDNIGHT)) {
            return value.format(DATE_ONLY_FORMATTER);
        }
        return value.format(DISPLAY_FORMATTER);
    }

}
