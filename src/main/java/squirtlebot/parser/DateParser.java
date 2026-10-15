package squirtlebot.parser;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.Temporal;
import java.util.List;
import java.util.Optional;

/**
 * Parses user input strings to identify date or datetime values.
 * Contains a set of accepted date and datetime formats.
 */
public class DateParser {
    private List<String> dateFormats;
    private List<String> dateTimeFormats;

    /**
     * Constructs a DateParser object.
     * Initializes the date and datetime format lists to include all the date and datetime formats it can parse.
     */
    public DateParser() {
        dateFormats = List.<String>of("dd-MM-uuuu", "dd/MM/uuuu", "uuuu-MM-dd", "uuuu/MM/dd");
        dateTimeFormats = List.<String>of("dd-MM-uuuu HH:mm:ss", "dd/MM/uuuu HH:mm:ss",
                "uuuu-MM-dd HH:mm:ss", "uuuu/MM/dd HH:mm:ss");
    }

    /**
     * Attempts to convert user input strings into either a {@link LocalDate} or {@link LocalDateTime} object.
     *
     * @param userInput user input containing a date or datetime.
     * @return an {@link Optional} containing a date or datetime object as a {@link Temporal}
     *      or an empty {@link Optional} if user input is not of a supported format.
     */
    public Optional<Temporal> parseTemporal(String userInput) {
        Optional<Temporal> optionalDate = parseDate(userInput);
        Optional<Temporal> optionalDateTime = parseDateTime(userInput);

        return optionalDate.or(() -> optionalDateTime);
    }

    /**
     * Converts a user input string into a {@link LocalDate} object.
     *
     * @param userInput user input containing a date.
     * @return an {@link Optional} containing the date object represented by user input
     *      or an empty {@link Optional} if the user input is of an unsupported format.
     */
    private Optional<Temporal> parseDate(String userInput) {
        for (String format : dateFormats) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
            try {
                LocalDate date = LocalDate.parse(userInput, formatter);
                return Optional.of(date);
            } catch (DateTimeParseException e) {
                continue;
            }
        }
        return Optional.<Temporal>empty();
    }

    /**
     * Converts a user input string into a {@link LocalDateTime} object.
     *
     * @param userInput user input containing a datetime.
     * @return an {@link Optional} containing the dateTime object represented by user input
     *      or an empty {@link Optional} if the user input is of an unsupported format.
     */
    private Optional<Temporal> parseDateTime(String userInput) {
        for (String format : dateTimeFormats) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
            try {
                LocalDateTime dateTime = LocalDateTime.parse(userInput, formatter);
                return Optional.<Temporal>of(dateTime);
            } catch (DateTimeParseException e) {
                continue;
            }
        }
        return Optional.<Temporal>empty();
    }
}
