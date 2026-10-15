package minerva.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class Event extends Task {
    protected LocalDate from;
    protected String to;

    public Event(String description, String from, String to) throws DateTimeParseException {
        super(description);
        this.from = LocalDate.parse(from.trim());
        this.to = to.trim();
    }

    @Override
    public String toFileFormat() {
        return "E | " + super.toFileFormat() + " | " + from.toString() + " | " + to;
    }

    @Override
    public String toString() {
        String formattedFrom = from.format(DateTimeFormatter.ofPattern("MMM dd yyyy"));
        return "[E]" + super.toString() + " (from: " + formattedFrom + " to: " + to + ")";
    }
}
