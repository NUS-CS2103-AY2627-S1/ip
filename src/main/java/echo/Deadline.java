package echo;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Deadline task which has a description and a deadline, which includes a date and optionally a time.
 */
public class Deadline extends Task {
    private final String taskMarker = "[D]";

    private final String deadline;
    private String time = "";

    private LocalDate deadlineDate;

    private String formattedDate;

    /**
     * Creates a deadline task with a description and due date.
     *
     * @param taskArgs arguments used to create the deadline task
     */
    public Deadline(String... taskArgs) throws InvalidCommandException {
        super(taskArgs[0]);
        if (taskArgs.length < 2) {
            throw new InvalidCommandException();
        }
        this.deadline = taskArgs[1];
        try {
            this.deadlineDate = LocalDate.parse(deadline);
        } catch (DateTimeParseException dateTimeParseException) {
            throw new InvalidCommandException();
        }
        if (taskArgs.length > 2) {
            this.time = taskArgs[2];
            try {
                int timeInt = Integer.parseInt(time);
                this.formattedDate = this.deadlineDate
                        .atTime(Math.floorDiv(timeInt, 100), timeInt % 100)
                        .format(DateTimeFormatter.ofPattern("dd MMM yyyy hhmma"));
            } catch (NumberFormatException | DateTimeException e) {
                throw new InvalidCommandException();
            }
        }
        else {
            this.formattedDate = this.deadlineDate.format(DateTimeFormatter.ofPattern("dd MMM yyyy"));
        }
    }

    @Override
    public String toString() {
        return String.format("%s%s %s (By: %s)",
                this.taskMarker,
                this.getStatusMarker(),
                super.getDesc(),
                this.formattedDate);
    }
    @Override
    public String toStorageFormat() {
        return String.join(" // ",
                new String[]{
                        "D",
                        super.getStatus(),
                        super.getDesc(),
                        deadline,
                        time
                });
    }
}
