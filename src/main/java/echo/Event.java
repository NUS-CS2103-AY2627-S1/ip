package echo;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Event task that has start and end dates, and optionally start and end times.
 */
public class Event extends Task {
    String taskMarker = "[E]";

    String from;
    String to;
    String fromTime;
    String toTime;

    LocalDate fromDate;
    LocalDate toDate;

    String formattedFromDate;
    String formattedToDate;

    /**
     * Creates an event item with a from date and to date, and optionally a from time and to time.
     *
     * @param taskArgs arguments used to create the event task
     */
    public Event(String... taskArgs) throws InvalidCommandException {
        super(taskArgs[0]);
        if (taskArgs.length < 3) {
            throw new InvalidCommandException();
        }
        this.from = taskArgs[1];
        this.to = taskArgs[2];
        try {
            this.fromDate = LocalDate.parse(from);
            this.toDate = LocalDate.parse(to);
            if (toDate.isBefore(fromDate)) {
                throw new InvalidCommandException();
            }
        } catch (DateTimeParseException dateTimeParseException) {
            throw new InvalidCommandException();
        }
        if (taskArgs.length >= 5) {
            this.fromTime = taskArgs[3];
            this.toTime = taskArgs[4];

            try {
                int fromTimeInt = Integer.parseInt(fromTime);
                int toTimeInt = Integer.parseInt(toTime);

                if (toTimeInt <= fromTimeInt && this.fromDate.isEqual(this.toDate)) {
                    throw new InvalidCommandException();
                }

                this.formattedFromDate = this.fromDate
                        .atTime(Math.floorDiv(fromTimeInt, 100), fromTimeInt % 100)
                        .format(DateTimeFormatter.ofPattern("dd MMM yyyy hhmma"));
                this.formattedToDate = this.toDate
                        .atTime(Math.floorDiv(toTimeInt, 100), toTimeInt % 100)
                        .format(DateTimeFormatter.ofPattern("dd MMM yyyy hhmma"));
            } catch (NumberFormatException | DateTimeException e) {
                throw new InvalidCommandException();
            }
        } else {
            this.formattedFromDate = this.fromDate.format(DateTimeFormatter.ofPattern("dd MMM yyyy"));
            this.formattedToDate = this.toDate.format(DateTimeFormatter.ofPattern("dd MMM yyyy"));
        }
    }

    @Override
    public String toString() {
        return String.format("%s%s %s (from: %s to: %s)",
                this.taskMarker,
                this.getStatusMarker(),
                super.getDesc(),
                this.formattedFromDate,
                this.formattedToDate);
    }
    @Override
    public String toStorageFormat() {
        return String.join(" // ",
                new String[]{
                        "E",
                        super.getStatus(),
                        super.getDesc(),
                        this.from,
                        this.to,
                        this.fromTime,
                        this.toTime
                });
    }
}
