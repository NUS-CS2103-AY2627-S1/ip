package bro.parser;

/**
 * Represents parsed arguments for a deadline task.
 *
 * @param description The task description.
 * @param deadline    The deadline date/time string.
 */
public record DeadlineDetails(String description, String deadline) {
}

