package blud.model;

import java.time.LocalDateTime;

import blud.exception.EventException;
import blud.util.DateUtils;

/** Represents a task that occurs between a start date and an end date. */
public class Event extends Task {
    private final LocalDateTime startDate;
    private final LocalDateTime endDate;
    /** Creates an event task from its parsed command parts. */
    public Event(String[] parts) {
        super(validateAndExtractDescription(parts));
        if (parts.length != 3) {
            throw new EventException("Missing start (/from) and end (/to) dates for event task");
        }
        if (!parts[1].startsWith("from ")) {
            throw new EventException("Missing start (/from) date for event task");
        }
        if (!parts[2].startsWith("to ")) {
            throw new EventException("Missing end (/to) date for event task");
        }
        if (parts[1].substring(5).strip().isEmpty()) {
            throw new EventException("Missing date/time after /from for event task");
        }
        if (parts[2].substring(3).strip().isEmpty()) {
            throw new EventException("Missing date/time after /to for event task");
        }
        this.startDate = DateUtils.parseInput(
                parts[1].substring(5).strip()
        );
        this.endDate = DateUtils.parseInput(
                parts[2].substring(3).strip()
        );
        if (!startDate.isBefore(endDate)) {
            throw new EventException("Event start date/time must be earlier than its end date/time");
        }
    }

    /** Returns the event task in display format. */
    @Override
    public String toString() {

        return String.format(
                "[E]%s (from: %s to: %s)",
                super.toString(),
                DateUtils.formatOutput(startDate),
                DateUtils.formatOutput(endDate)
        );
    }

    /** Returns the event task in storage format. */
    @Override
    public String toStorageString() {
        return String.format(
                "E | %d | %s | %s | %s",
                isDone() ? 1 : 0,
                getTaskDescription(),
                DateUtils.formatForStorage(startDate),
                DateUtils.formatForStorage(endDate)
        );
    }

    /** Returns the event start date for task-list sorting. */
    LocalDateTime getStartDate() {
        return startDate;
    }

    /** Returns the event end date for validation and task comparison. */
    LocalDateTime getEndDate() {
        return endDate;
    }

    /** Validates the command parts and returns the event description. */
    private static String validateAndExtractDescription(String[] parts) {
        if (parts == null || parts.length == 0 || parts[0].length() < 7) {
            throw new EventException();
        }
        return parts[0].substring(6).strip();
    }
}
