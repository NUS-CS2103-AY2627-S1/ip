package oz.parser;

import java.time.LocalDate;

/**
 * Identifies a task and, when supplied, one recurring occurrence.
 *
 * @param taskIndex Zero-based task index.
 * @param occurrenceDate Recurring occurrence date, or null for the whole task.
 */
public record TaskTarget(int taskIndex, LocalDate occurrenceDate) {
    /**
     * Validates the parsed task index.
     */
    public TaskTarget {
        assert taskIndex >= 0 : "A parsed task target must have a non-negative index";
    }
}
