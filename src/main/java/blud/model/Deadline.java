package blud.model;

import java.time.LocalDateTime;

import blud.exception.DeadlineException;
import blud.util.DateUtils;

/** Represents a task that must be completed by a specified date and time. */
public class Deadline extends Task {
    private LocalDateTime deadline;
    /** Creates a deadline task from its parsed command parts. */
    public Deadline(String[] parts) {
        super(validateAndExtractDescription(parts));
        if (parts.length != 2 || !parts[1].startsWith("by ")) {
            throw new DeadlineException("Missing deadline (/by) for deadline task");
        }
        if (parts[1].substring(3).strip().isEmpty()) {
            throw new DeadlineException("Missing date/time after /by for deadline task");
        }
        this.deadline = DateUtils.parseInput(
                parts[1].substring(3).strip()
        );
    }

    /** Returns the deadline task in display format. */
    @Override
    public String toString() {
        return String.format(
                "[D]%s (by: %s)",
                super.toString(),
                DateUtils.formatOutput(deadline)
        );
    }

    /** Returns the deadline task in storage format. */
    @Override
    public String toStorageString() {
        return String.format(
                "D | %d | %s | %s",
                isDone() ? 1 : 0,
                getTaskDescription(),
                DateUtils.formatForStorage(deadline)
        );
    }

    /** Returns the deadline for task-list sorting. */
    LocalDateTime getDeadline() {
        return deadline;
    }

    /** Validates the command parts and returns the deadline description. */
    private static String validateAndExtractDescription(String[] parts) {
        if (parts == null || parts.length == 0 || parts[0].length() < 10) {
            throw new DeadlineException();
        }
        return parts[0].substring(9).strip();
    }
}
