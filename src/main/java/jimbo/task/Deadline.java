package jimbo.task;

import jimbo.JimboException;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

/**
 * A task that should be done by a certain time.
 */
public class Deadline extends Task {
    private LocalDateTime by;

    public Deadline(String data, String by) throws JimboException {
        super(data);
        try {
            this.by = LocalDateTime.parse(by, INPUT_DATE_TIME_FORMAT);
        } catch (DateTimeParseException e) {
            throw new JimboException(INVALID_DATE_TIME_MESSAGE);
        }
    }

    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by " + by.format(FRIENDLY_DATE_TIME_FORMAT) + ") "
                + formatTags();
    }

    @Override
    public String serialise() {
        return "D|" + super.serialise() + "|" + by.format(INPUT_DATE_TIME_FORMAT) + "|" + serialiseTags();
    }
}
