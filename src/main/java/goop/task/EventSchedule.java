package goop.task;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Locale;

/**
 * Validates explicit event dates and times while retaining legacy free-form schedules.
 */
final class EventSchedule {
    private static final String ORDER_ERROR = "An event must end after it starts. "
            + "Use full dates and times for an overnight event.";

    private EventSchedule() {
    }

    /**
     * Checks numeric schedules and equal text without guessing the meaning of natural language.
     */
    static void validate(String from, String to) {
        if (from == null || to == null || from.isBlank() || to.isBlank()) {
            throw new IllegalArgumentException("An event needs a non-empty start and end.");
        }
        String start = from.replaceAll("(?U)\\s+", " ").strip();
        String end = to.replaceAll("(?U)\\s+", " ").strip();
        LocalDateTime startTime = parse(start);
        LocalDateTime endTime = parse(end);
        if (hasDate(start) != hasDate(end)) {
            throw new IllegalArgumentException("Use full dates for both event endpoints.");
        }
        if (start.equalsIgnoreCase(end)
                || startTime != null && endTime != null && !endTime.isAfter(startTime)) {
            throw new IllegalArgumentException(ORDER_ERROR);
        }
    }

    /**
     * Compares equivalent numeric times across supported formats, or normalized legacy text.
     */
    static boolean hasSameTime(String first, String second) {
        String firstText = first.replaceAll("(?U)\\s+", " ").strip();
        String secondText = second.replaceAll("(?U)\\s+", " ").strip();
        LocalDateTime firstTime = parse(firstText);
        LocalDateTime secondTime = parse(secondText);
        if (firstTime != null && secondTime != null) {
            return hasDate(firstText) == hasDate(secondText) && firstTime.equals(secondTime);
        }
        return firstText.equals(secondText);
    }

    /**
     * Recognizes numeric date prefixes so invalid dates cannot fall back to free text.
     */
    private static boolean hasDate(String text) {
        return text.matches("[0-9]+[-/].*");
    }

    /**
     * Parses a date with optional time, or a clock time on an arbitrary common day.
     * Returns null for legacy text whose chronological meaning cannot be determined.
     */
    private static LocalDateTime parse(String text) {
        boolean isDate = hasDate(text);
        if (!isDate && !text.matches("(?i)[0-9]+(?::[0-9]+)?\\s*(am|pm)?")) {
            return null;
        }
        String[] patterns = isDate
                ? new String[] {"d/M/uuuu HHmm", "uuuu-MM-dd HHmm", "d/M/uuuu", "uuuu-MM-dd"}
                : new String[] {"HHmm", "H:mm", "ha", "h:mma", "h a", "h:mm a"};
        for (String pattern : patterns) {
            DateTimeFormatter format = DateTimeFormatter.ofPattern(pattern, Locale.ENGLISH)
                    .withResolverStyle(ResolverStyle.STRICT);
            try {
                if (!isDate) {
                    return LocalTime.parse(text.toUpperCase(Locale.ROOT), format)
                            .atDate(LocalDate.of(2000, 1, 1));
                }
                if (!pattern.contains("HHmm")) {
                    return LocalDate.parse(text, format).atStartOfDay();
                }
                return LocalDateTime.parse(text, format);
            } catch (DateTimeParseException error) {
                // Try the next supported format.
            }
        }
        throw new IllegalArgumentException("Invalid event date or time. Use d/M/yyyy or yyyy-MM-dd "
                + "with optional HHmm, or a time such as 14:00 or 2pm.");
    }
}
