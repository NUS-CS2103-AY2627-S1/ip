package ultron.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import ultron.exception.UltronException;

/**
 * Represents a task that must be completed by a specified date and optional time.
 */
public class Deadline extends Task {
    /** Message shown when a deadline command is not formatted correctly. */
    private static final String INVALID_FORMAT_MESSAGE = "You FOOL! Your formatting is WRONG! "
            + "Example input: deadline return book /by 2/12/2019 1800.";

    protected LocalDate date;
    protected String time;

    /**
     * Creates a deadline task from its command argument.
     *
     * @param input the description followed by {@code /by}, a date, and an optional time.
     * @throws UltronException if the deadline is not formatted correctly.
     */
    public Deadline(String input) throws UltronException {
        super(input, TaskType.DEADLINE);
        String[] inputs = input.split(" /by ");
        if (inputs.length != 2) {
            throw new UltronException(INVALID_FORMAT_MESSAGE);
        }
        this.description = inputs[0];
        String[] deadline = inputs[1].split(" ");
        String dateString;
        if (deadline.length == 2) {
            this.time = " " + deadline[1];
            dateString = deadline[0];
        } else if (deadline.length == 1) {
            this.time = "";
            dateString = deadline[0];
        } else {
            throw new UltronException(INVALID_FORMAT_MESSAGE);
        }
        try {
            this.date = LocalDate.parse(dateString, DateTimeFormatter.ofPattern("d/MM/yyyy"));
        } catch (DateTimeParseException e) {
            throw new UltronException(INVALID_FORMAT_MESSAGE);
        }
    }

    @Override
    public String getDescription() {
        String formattedDate = this.date.format(DateTimeFormatter.ofPattern("dd/MMM/yyyy"));
        return this.description + "( by: " + formattedDate + this.time + " )";
    }
}
