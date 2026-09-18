package korvus.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * A Parser to convert between String and LocalDateTime.
 */
public class DateTimeParser {
    private DateTimeFormatter formatter;

    /**
     * Returns an instance of DateTimeParser.
     *
     * @param formatter DateTimeFormat for displaying and parsing Strings.
     */
    public DateTimeParser(DateTimeFormatter formatter) {
        this.formatter = formatter;
    }

    /**
     * Returns whether the provided String is in the valid format.
     *
     * @param sDatetime Input to be checked.
     * @return Boolean whether the input is in the correct format.
     */
    public boolean isValid(String sDatetime) {
        try {
            formatter.parse(sDatetime);
        } catch (DateTimeParseException e) {
            return false;
        }
        return true;
    }

    /**
     * Changes the DateTime format to be used.
     *
     * @param formatter Input to be checked.
     */
    public void setFormatter(DateTimeFormatter formatter) {
        this.formatter = formatter;
    }

    /**
     * Returns a DateTime object corresponding to the given String.
     * If the String is in an invalid format, nothing will be returned.
     *
     * @param sDatetime Input to be used.
     * @return LocalDateTime object corresponding to the aforementioned String.
     */
    public LocalDateTime parseDateString(String sDatetime) {
        try {
            return LocalDateTime.parse(sDatetime, formatter);
        } catch (DateTimeParseException | NullPointerException e) {
            return null;
        }
    }

    /**
     * Returns the provided DateTime as a String.
     *
     * @param dateTime DateTime to be converted.
     * @return String representation of the provided String, in a specific format.
     */
    public String convertDateToString(LocalDateTime dateTime) {
        return dateTime == null ? "unknown" : dateTime.format(formatter);
    }
}
