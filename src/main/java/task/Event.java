package task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;

import common.Constants;
import exception.FriedbergException;
import exception.FriedbergInternalException;
import exception.FriedbergUserInputException;

/**
 * Represents an event task that occurs between two dates.
 */
public class Event extends Task {
    private static final DateTimeFormatter INPUT_DATE_FORMATTER = DateTimeFormatter.ofPattern("uuuu-MM-dd")
            .withResolverStyle(ResolverStyle.STRICT);

    private LocalDate fromDatetime;
    private LocalDate toDatetime;

    /**
     * Creates an event with the given name, start date, and end date.
     *
     * @param name name of the event
     * @param fromDatetime start date in ISO local date format
     * @param toDatetime end date in ISO local date format
     * @throws FriedbergException if either date cannot be parsed
     */
    public Event(String name, String fromDatetime, String toDatetime) throws FriedbergException {
        super(name);
        this.parseAndSetDatetimes(fromDatetime, toDatetime);
    }

    /**
     * Recreates an event from its serialized status and date fields.
     *
     * @param name name of the event
     * @param status completion status of the event
     * @param tokens serialized fields containing the start and end dates
     * @throws FriedbergException if the serialized fields are invalid
     */
    public Event(String name, TaskStatus status, String[] tokens) throws FriedbergException {
        super(name, status);
        if (tokens.length != 2) {
            throw new FriedbergInternalException("expected tokens to have length 2");
        }
        this.parseAndSetDatetimes(tokens[0], tokens[1]);
    }

    private void parseAndSetDatetimes(String fromDatetime, String toDatetime) throws FriedbergException {
        try {
            this.fromDatetime = LocalDate.parse(fromDatetime, INPUT_DATE_FORMATTER);
            this.toDatetime = LocalDate.parse(toDatetime, INPUT_DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            throw new FriedbergUserInputException("Unable to parse datetime input, please use the yyyy-mm-dd format");
        }
        if (!this.fromDatetime.isBefore(this.toDatetime)) {
            throw new FriedbergUserInputException("Event start date must be before its end date");
        }
    }

    @Override
    public String renderTask() {
        return String.format("[E]%s (from: %s to: %s)", super.renderTask(),
                this.fromDatetime.format(Constants.DATETIME_RENDER_FORMATTER),
                this.toDatetime.format(Constants.DATETIME_RENDER_FORMATTER));
    }

    @Override
    public String serialize() {
        return String.format("%s,E,%s,%s", super.serialize(), this.fromDatetime.toString(), this.toDatetime.toString());
    }
}
