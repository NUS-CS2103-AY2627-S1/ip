package bro.parser;

/**
 * Represents parsed arguments for an event task.
 *
 * @param description The task description.
 * @param start       The start date/time string.
 * @param end         The end date/time string.
 */
public record EventDetails(String description, String start, String end) {
}

