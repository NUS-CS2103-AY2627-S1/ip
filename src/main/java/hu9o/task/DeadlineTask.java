package hu9o.task;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

/**
 * Represents a task that has a deadline.
 */
public class DeadlineTask extends Task {
    private final LocalDateTime specification;

    /**
     * Creates an unfinished deadline task.
     *
     * @param description   the task's description.
     * @param specification the deadline, such as {@code "12/08/26 3 PM"}.
     * @throws DateTimeParseException if the deadline is not a valid date-time.
     */
    public DeadlineTask(String description, String specification) throws DateTimeParseException {
        super(description, "D");
        this.specification = DateTime.parseString(specification);
    }

    @Override
    public String compressionString() {
        return super.compressionString() + DateTime.formatForStorage(specification) + "|";
    }

    @Override
    protected String specificationSuffix() {
        return " (by: " + DateTime.formatForDisplay(specification) + ")";
    }
}
