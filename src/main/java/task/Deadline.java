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
 * Represents a task that must be completed by a specified date.
 */
public class Deadline extends Task {
    private static final DateTimeFormatter INPUT_DATE_FORMATTER = DateTimeFormatter.ofPattern("uuuu-MM-dd")
            .withResolverStyle(ResolverStyle.STRICT);

    private LocalDate byDatetime;

    /**
     * Creates a deadline with the given name and due date.
     *
     * @param name name of the deadline
     * @param byDatetime due date in ISO local date format
     * @throws FriedbergException if the due date cannot be parsed
     */
    public Deadline(String name, String byDatetime) throws FriedbergException {
        super(name);
        this.parseAndSetDatetimes(byDatetime);
    }

    /**
     * Constructor to be used by Task.deserialize
     */
    public Deadline(String name, TaskStatus status, String[] tokens) throws FriedbergException {
        super(name, status);
        if (tokens.length != 1) {
            throw new FriedbergInternalException("expected tokens to have length 1");
        }

        this.parseAndSetDatetimes(tokens[0]);
    }

    private void parseAndSetDatetimes(String byDatetime) throws FriedbergException {
        try {
            this.byDatetime = LocalDate.parse(byDatetime, INPUT_DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            throw new FriedbergUserInputException("Unable to parse datetime input, please use the yyyy-mm-dd format");
        }
    }

    @Override
    public String renderTask() {
        return String.format("[D]%s (by: %s)", super.renderTask(),
                this.byDatetime.format(Constants.DATETIME_RENDER_FORMATTER));
    }

    @Override
    public String serialize() {
        return String.format("%s,D,%s", super.serialize(), this.byDatetime.toString());
    }
}
