package quu.exception;

import java.time.LocalDate;

/**
 * Thrown when an event's end date falls before its start date.
 */
public class InvalidDurationException extends QuuException {

    /**
     * Creates the exception, naming both ends of the invalid range.
     *
     * @param eventStart the start date given
     * @param eventEnd the end date given, which falls before the start
     */
    public InvalidDurationException(LocalDate eventStart, LocalDate eventEnd) {
        super(String.format("%s to %s is not a valid duration", eventStart, eventEnd));
    }
}
